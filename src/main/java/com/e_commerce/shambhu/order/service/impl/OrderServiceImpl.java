package com.e_commerce.shambhu.order.service.impl;

import com.e_commerce.shambhu.address.entity.Address;
import com.e_commerce.shambhu.address.repository.AddressRepository;
import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.inventory.service.InventoryService;
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

    @Override
    public OrderResponse placeOrder(CreateOrderRequest request) {
        return null;
    }

    @Override
    public OrderResponse placeOrder(Cart cart, CreateOrderRequest request) {
        return null;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        return null;
    }

    @Override
    public Page<OrderSummaryResponse> getMyOrders(Pageable pageable) {
        return null;
    }

    @Override
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return null;
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, CancelOrderRequest request) {
        return null;
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        return null;
    }

    @Override
    public OrderResponse updatePaymentStatus(Long orderId, UpdatePaymentStatusRequest request) {
        return null;
    }

    private void validateCart(Cart cart) {

        if (cart == null) {
            throw new ResourceNotFoundException(
                    "Shopping cart not found.", null, null
            );
        }

        if (Boolean.TRUE.equals(cart.getDeleted())) {
            throw new BusinessException(
                    "Shopping cart has been deleted."
            );
        }

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new BusinessException(
                    "Only active carts can be checked out."
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCartAndDeletedFalse(cart);

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    "Shopping cart is empty."
            );
        }

        LOGGER.info(
                "Cart validation completed successfully. CartId={}, ItemCount={}",
                cart.getId(),
                cartItems.size()
        );
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
