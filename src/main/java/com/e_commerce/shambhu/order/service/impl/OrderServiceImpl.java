package com.e_commerce.shambhu.order.service.impl;

import com.e_commerce.shambhu.address.entity.Address;
import com.e_commerce.shambhu.address.repository.AddressRepository;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.enums.RoleType;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.inventory.service.InventoryService;
import com.e_commerce.shambhu.order.common.OrderMapper;
import com.e_commerce.shambhu.order.component.OrderNumberGenerator;
import com.e_commerce.shambhu.order.dto.request.CancelOrderRequest;
import com.e_commerce.shambhu.order.dto.request.CreateOrderRequest;
import com.e_commerce.shambhu.order.dto.request.UpdateOrderStatusRequest;
import com.e_commerce.shambhu.order.dto.request.UpdatePaymentStatusRequest;
import com.e_commerce.shambhu.order.dto.response.OrderResponse;
import com.e_commerce.shambhu.order.dto.response.OrderSummaryResponse;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.entity.OrderItem;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentStatus;
import com.e_commerce.shambhu.order.repository.OrderItemRepository;
import com.e_commerce.shambhu.order.repository.OrderRepository;
import com.e_commerce.shambhu.order.service.OrderService;
import com.e_commerce.shambhu.order.validator.OrderValidator;
import com.e_commerce.shambhu.product.entity.Product;
import com.e_commerce.shambhu.product.productImage.service.ProductImageService;
import com.e_commerce.shambhu.shoppingCart.entity.Cart;
import com.e_commerce.shambhu.shoppingCart.entity.CartItem;
import com.e_commerce.shambhu.shoppingCart.enums.CartStatus;
import com.e_commerce.shambhu.shoppingCart.repository.CartItemRepository;
import com.e_commerce.shambhu.shoppingCart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    public static Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final InventoryService inventoryService;

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final OrderNumberGenerator orderNumberGenerator;

    private final ProductImageService productImageService;

    private final OrderMapper orderMapper;

    private final OrderValidator orderValidator;

    @Override
    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest request) {

        LOGGER.info("Initiating order placement.");

        // 1. Get authenticated user
        User user = getAuthenticatedUser();

        LOGGER.info("Authenticated userId={}", user.getId());

        // 2. Fetch active cart
        Cart cart = cartRepository
                .findByUserAndStatusAndDeletedFalse(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new BusinessException("Active shopping cart not found."));

        return placeOrder(cart, request);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(
            Cart cart,
            CreateOrderRequest request) {

        LOGGER.info("Processing checkout for cartId={}", cart.getId());

        /*
         * 1. Validate Cart
         */
        orderValidator.validateCart(cart);

        /*
         * 2. Fetch User
         */
        User user = cart.getUser();

        /*
         * 3. Validate Inventory
         */
        inventoryService.validateInventory(cart);

        /*
         * 4. Shipping Address
         */
        Address shippingAddress =
                getShippingAddress(
                        request.getShippingAddressId(),
                        user);

        /*
         * 5. Billing Address
         */
        Address billingAddress =
                getBillingAddress(
                        request.getBillingAddressId(),
                        user);

        /*
         * 6. Fetch Cart Items
         */
        List<CartItem> cartItems =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        /*
         * 7. Build Order
         */
        Order order =
                buildOrder(
                        user,
                        cart,
                        shippingAddress,
                        billingAddress,
                        request);

        /*
         * 8. Build Order Items
         */
        List<OrderItem> orderItems =
                buildOrderItems(order, cartItems);

        /*
         * 9. Calculate Totals
         */
        calculateOrderTotals(order, orderItems);

        /*
         * 10. Persist Order
         */
        order = orderRepository.save(order);

        /*
         * 11. Persist Order Items
         */
        orderItemRepository.saveAll(orderItems);

        /*
         * 12. Update Inventory
         */
        inventoryService.reduceInventory(cartItems);

        /*
         * 13. Clear Cart
         */
        cartItemRepository.deleteAll(cartItems);

        cart.setStatus(CartStatus.CHECKED_OUT);

        cartRepository.save(cart);

        LOGGER.info(
                "Order placed successfully. OrderNumber={}",
                order.getOrderNumber());

        /*
         * 14. Convert Response
         */
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        LOGGER.info("Fetching order. OrderId={}", orderId);

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findDetailedById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found.",
                                null,
                                null));

        boolean isOwner = order.getUser().getId().equals(user.getId());

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role ->
                        role.getName().equals(RoleType.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException(
                    "You are not authorized to access this order.");
        }

        LOGGER.info(
                "Order {} fetched successfully.",
                order.getOrderNumber());

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getMyOrders(
            Pageable pageable) {

        User user = getAuthenticatedUser();

        LOGGER.info(
                "Fetching orders for userId={}",
                user.getId());

        Page<Order> orders =
                orderRepository.findByUser(
                        user,
                        pageable);

        return orderMapper.toSummaryPage(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getAllOrders(
            Pageable pageable) {

        LOGGER.info(
                "Fetching all orders.");

        Page<Order> orders =
                orderRepository.findAll(pageable);

        return orderMapper.toSummaryPage(orders);
    }

    @Override
    public OrderResponse cancelOrder(
            Long orderId,
            CancelOrderRequest request) {

        User user = getAuthenticatedUser();

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found.",
                                        null,
                                        null));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(
                    "You cannot cancel another user's order.");
        }

        orderValidator.validateCancellation(order);

        order.setOrderStatus(OrderStatus.CANCELLED);

        // order.setCancelledAt(LocalDateTime.now());

        orderRepository.save(order);

        LOGGER.info(
                "Order {} cancelled successfully.",
                order.getOrderNumber());

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found.",
                                        null,
                                        null));

        orderValidator.validateStatusTransition(
                order.getOrderStatus(),
                request.getStatus());

        order.setOrderStatus(
                request.getStatus());

        orderRepository.save(order);

        LOGGER.info(
                "Order {} updated to {}",
                order.getOrderNumber(),
                order.getOrderStatus());

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse updatePaymentStatus(
            Long orderId,
            UpdatePaymentStatusRequest request) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found.",
                                        null,
                                        null));

        orderValidator.validatePaymentTransition(
                order.getPaymentStatus(),
                request.getPaymentStatus());

        order.setPaymentStatus(
                request.getPaymentStatus());

        orderRepository.save(order);

        LOGGER.info(
                "Payment updated. OrderNumber={}, Status={}",
                order.getOrderNumber(),
                order.getPaymentStatus());

        return orderMapper.toResponse(order);
    }

    private Address getShippingAddress(
            Long addressId,
            User user) {

        return getAddress(addressId, user, "Shipping");
    }

    private Address getBillingAddress(
            Long addressId,
            User user) {

        return getAddress(addressId, user, "Billing");
    }

    private Address getAddress(
            Long addressId,
            User user,
            String type) {

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                type + " address not found.", null, null
                        ));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(
                    type + " address does not belong to the authenticated user."
            );
        }

        return address;
    }

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmailAndDeletedFalse(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found.", null, null
                        ));
    }


    private Order buildOrder(
            User user,
            Cart cart,
            Address shippingAddress,
            Address billingAddress,
            CreateOrderRequest request) {

        LOGGER.info(
                "Building order for userId={} and cartId={}",
                user.getId(),
                cart.getId()
        );

        Order order = new Order();

        order.setOrderNumber(
                orderNumberGenerator.generate()
        );

        order.setUser(user);

        order.setShippingAddress(
                shippingAddress
        );

        order.setBillingAddress(
                billingAddress
        );

        order.setOrderStatus(
                OrderStatus.PENDING
        );

        order.setPaymentStatus(
                PaymentStatus.PENDING
        );

        order.setPaymentMethod(
                request.getPaymentMethod()
        );

        order.setCustomerNote(
                request.getCustomerNote()
        );

        /*
         * Pricing fields will be calculated later.
         */

        order.setTotalItems(0);

        order.setTotalAmount(
                BigDecimal.ZERO
        );

        order.setDiscountAmount(
                BigDecimal.ZERO
        );

        order.setTaxAmount(
                BigDecimal.ZERO
        );

        order.setShippingCharge(
                BigDecimal.ZERO
        );

        order.setPayableAmount(
                BigDecimal.ZERO
        );

        order.setDeleted(false);

        LOGGER.info(
                "Order initialized successfully. OrderNumber={}",
                order.getOrderNumber()
        );

        return order;
    }

    private List<OrderItem> buildOrderItems(
            Order order,
            List<CartItem> cartItems) {

        LOGGER.info(
                "Building {} order items for orderNumber={}",
                cartItems.size(),
                order.getOrderNumber()
        );

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(product);

            /*
             * Product Snapshot
             */
            orderItem.setProductName(
                    product.getName()
            );

            orderItem.setSku(
                    product.getSku()
            );

            orderItem.setPrimaryImage(
                    productImageService.getPrimaryImageUrl(product.getId())
            );
            /*
             * Pricing Snapshot
             */
            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setUnitPrice(
                    cartItem.getUnitPrice()
            );

            orderItem.setDiscountAmount(
                    cartItem.getDiscountPrice() != null
                            ? cartItem.getDiscountPrice()
                            : BigDecimal.ZERO
            );

            /*
             * Tax will be calculated later.
             */
            orderItem.setTaxAmount(
                    BigDecimal.ZERO
            );

            /*
             * Line Total (before tax)
             */
            BigDecimal lineTotal =
                    cartItem.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
                            .subtract(
                                    orderItem.getDiscountAmount()
                            );

            orderItem.setTotalPrice(
                    lineTotal
            );

            orderItem.setDeleted(false);

            orderItems.add(orderItem);
        }

        LOGGER.info(
                "Successfully built {} order items.",
                orderItems.size()
        );

        return orderItems;
    }

    private void calculateOrderTotals(
            Order order,
            List<OrderItem> orderItems) {

        LOGGER.info(
                "Calculating totals for orderNumber={}",
                order.getOrderNumber()
        );

        int totalItems = 0;

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {

            totalItems += item.getQuantity();

            totalAmount = totalAmount.add(
                    item.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            )
            );

            totalDiscount = totalDiscount.add(
                    item.getDiscountAmount()
            );

            totalTax = totalTax.add(
                    item.getTaxAmount()
            );
        }

        /*
         * Shipping Charge
         * Future:
         * - Free shipping
         * - Express shipping
         * - Zone-wise shipping
         */

        BigDecimal shippingCharge = BigDecimal.ZERO;

        BigDecimal payableAmount =
                totalAmount
                        .subtract(totalDiscount)
                        .add(totalTax)
                        .add(shippingCharge);

        order.setTotalItems(totalItems);

        order.setTotalAmount(totalAmount);

        order.setDiscountAmount(totalDiscount);

        order.setTaxAmount(totalTax);

        order.setShippingCharge(shippingCharge);

        order.setPayableAmount(payableAmount);

        LOGGER.info(
                """
                Order totals calculated successfully.
                OrderNumber={}
                TotalItems={}
                TotalAmount={}
                Discount={}
                Tax={}
                Shipping={}
                Payable={}
                """,
                order.getOrderNumber(),
                totalItems,
                totalAmount,
                totalDiscount,
                totalTax,
                shippingCharge,
                payableAmount
        );
    }

    /*private Address getShippingAddress(
            Long addressId,
            User user) {

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shipping address not found.", null, null
                        ));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(
                    "Shipping address does not belong to the authenticated user."
            );
        }

        return address;
    }

    private Address getBillingAddress(
            Long addressId,
            User user) {

        Address address = addressRepository
                .findByIdAndDeletedFalse(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Billing address not found.", null, null
                        ));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(
                    "Billing address does not belong to the authenticated user."
            );
        }

        return address;
    }*/
}
