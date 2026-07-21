package com.e_commerce.shambhu.payment.service.impl;

import com.e_commerce.shambhu.auth.entity.User;
import com.e_commerce.shambhu.auth.enums.RoleType;
import com.e_commerce.shambhu.auth.repository.UserRepository;
import com.e_commerce.shambhu.auth.security.CustomUserDetails;
import com.e_commerce.shambhu.auth.service.AuthService;
import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import com.e_commerce.shambhu.order.repository.OrderRepository;
import com.e_commerce.shambhu.payment.dto.request.CreatePaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.RefundPaymentRequest;
import com.e_commerce.shambhu.payment.dto.request.VerifyPaymentRequest;
import com.e_commerce.shambhu.payment.dto.response.PaymentResponse;
import com.e_commerce.shambhu.payment.dto.response.PaymentSummaryResponse;
import com.e_commerce.shambhu.payment.entity.Payment;
import com.e_commerce.shambhu.payment.enums.PaymentStatus;
import com.e_commerce.shambhu.payment.gateway.PaymentGatewayFactory;
import com.e_commerce.shambhu.payment.gateway.PaymentGatewayService;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayPaymentResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayRefundResponse;
import com.e_commerce.shambhu.payment.gateway.dto.GatewayVerificationResponse;
import com.e_commerce.shambhu.payment.mapper.PaymentMapper;
import com.e_commerce.shambhu.payment.repository.PaymentRepository;
import com.e_commerce.shambhu.payment.service.PaymentService;
import com.e_commerce.shambhu.payment.util.PaymentReferenceGenerator;
import com.e_commerce.shambhu.payment.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final AuthService authService;

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentValidator paymentValidator;

    private final PaymentReferenceGenerator paymentReferenceGenerator;

    private final PaymentGatewayService paymentGatewayService;

    private final PaymentGatewayFactory paymentGatewayFactory;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        log.info("Initiating payment for orderId={}", request.getOrderId());
        // ---------------------------------------------------------
        // Fetch authenticated user
        // ---------------------------------------------------------
        User currentUser = getCurrentUser();
        // ---------------------------------------------------------
        // Fetch order
        // ---------------------------------------------------------
        Order order = getOrder(request.getOrderId());
        // ---------------------------------------------------------
        // Business validations
        // ---------------------------------------------------------
        paymentValidator.validateOrder(order);
        paymentValidator.validateOwnership(order, currentUser);
        paymentValidator.validatePaymentEligibility(order);
        paymentValidator.validateDuplicatePayment(order);
        paymentValidator.validateGateway(request.getPaymentGateway());
        // ---------------------------------------------------------
        // Select payment gateway
        // ---------------------------------------------------------
        PaymentGatewayService gatewayService =
                paymentGatewayFactory.getGateway(
                        request.getPaymentGateway());
        // ---------------------------------------------------------
        // Create payment entity
        // ---------------------------------------------------------
        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setUser(currentUser);
        payment.setAmount(order.getPayableAmount());
        payment.setPaymentReference(paymentReferenceGenerator.generateReference());
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment.setRefundedAmount(BigDecimal.ZERO);
        // ---------------------------------------------------------
        // Initiate payment
        // ---------------------------------------------------------
        GatewayPaymentResponse gatewayResponse = gatewayService.initiatePayment(payment);
        // ---------------------------------------------------------
        // Update payment with gateway response
        // ---------------------------------------------------------
        payment.setGatewayOrderId(gatewayResponse.getGatewayOrderId());
        payment.setGatewayTransactionId(gatewayResponse.getGatewayTransactionId());
        payment.setGatewayResponse(gatewayResponse.getRawResponse());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        // ---------------------------------------------------------
        // Update order payment status
        // ---------------------------------------------------------
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        // ---------------------------------------------------------
        // Persist
        // ---------------------------------------------------------
        payment = paymentRepository.save(payment);
        orderRepository.save(order);
        log.info("Payment created successfully. paymentReference={}", payment.getPaymentReference());
        // ---------------------------------------------------------
        // Return response
        // ---------------------------------------------------------
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        log.info("Verifying payment. reference={}", request.getPaymentReference());
        // ---------------------------------------------------------
        // Fetch Payment
        // ---------------------------------------------------------
        Payment payment = paymentRepository
                .findByPaymentReference(request.getPaymentReference())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment",
                        "paymentReference",
                        request.getPaymentReference()));
        // ---------------------------------------------------------
        // Prevent re-verification
        // ---------------------------------------------------------
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("Payment has already been verified.");
        }
        // ---------------------------------------------------------
        // Select gateway
        // ---------------------------------------------------------
        PaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(payment.getPaymentGateway());
        // ---------------------------------------------------------
        // Verify payment with gateway
        // ---------------------------------------------------------
        GatewayVerificationResponse response = gatewayService.verifyPayment(request);
        // ---------------------------------------------------------
        // Update Payment
        // ---------------------------------------------------------
        payment.setGatewayTransactionId(response.getGatewayTransactionId());
        payment.setGatewayResponse(response.getGatewayResponse());
        if (response.isVerified()) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            Order order = payment.getOrder();
            order.setPaymentStatus(OrderPaymentStatus.PAID);
            orderRepository.save(order);
            log.info("Payment verified successfully. reference={}", payment.getPaymentReference());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            log.warn("Payment verification failed. reference={}", payment.getPaymentReference());
        }
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {

        log.info("Fetching payment details. paymentId={}", paymentId);

        CustomUserDetails currentUser = getCurrentUserDetails();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment",
                        "id",
                        paymentId));

        // Admin can access any payment
        // Customer can access only their own payment
        if (!currentUser.hasRole(getRoleAdmin())
                && !payment.getUser().getId().equals(currentUser.getId())) {

            log.warn(
                    "Unauthorized payment access. paymentId={}, userId={}",
                    paymentId,
                    currentUser.getId());

            throw new ForbiddenException(
                    "You are not authorized to access this payment.");
        }

        log.info("Payment details fetched successfully. paymentId={}", paymentId);

        return paymentMapper.toResponse(payment);
    }

    private static String getRoleAdmin() {
        return String.valueOf(RoleType.ROLE_ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentSummaryResponse> getMyPayments(Pageable pageable) {

        log.info("Fetching payment history.");

        CustomUserDetails currentUser = getCurrentUserDetails();

        Page<Payment> payments =
                paymentRepository.findByUserIdOrderByCreatedAtDesc(
                        currentUser.getId(),
                        pageable);

        log.info(
                "Retrieved {} payment(s) for userId={}",
                payments.getNumberOfElements(),
                currentUser.getId());

        return paymentMapper.toSummaryPage(payments);
    }

    @Override
    public List<PaymentResponse> getOrderPayments(Long orderId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentSummaryResponse> getOrderPayments(
            Long orderId,
            Pageable pageable) {

        log.info(
                "Fetching payments for orderId={}, page={}, size={}",
                orderId,
                pageable.getPageNumber(),
                pageable.getPageSize());

        CustomUserDetails currentUser = getCurrentUserDetails();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order",
                        "id",
                        orderId));

        // Admin can access every order
        // Customer can access only own order
        if (!currentUser.isAdmin()
                && !order.getUser().getId().equals(currentUser.getId())) {

            log.warn(
                    "Unauthorized access to order payments. orderId={}, userId={}",
                    orderId,
                    currentUser.getId());

            throw new ForbiddenException(
                    "You are not authorized to access payments for this order.");
        }

        Page<Payment> payments =
                paymentRepository.findByOrderIdOrderByCreatedAtDesc(
                        orderId,
                        pageable);

        log.info(
                "Retrieved {} payment(s) for orderId={}",
                payments.getNumberOfElements(),
                orderId);

        return paymentMapper.toSummaryPage(payments);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(RefundPaymentRequest request) {
        log.info("Refund initiated. paymentReference={}, amount={}", request.getPaymentId(), request.getRefundAmount());
        // ---------------------------------------------------------
        // Fetch Payment
        // ---------------------------------------------------------
        Payment payment = paymentRepository
                .findByPaymentReference(request.getPaymentId().toString())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment",
                        "paymentReference",
                        request.getPaymentId()));
        // ---------------------------------------------------------
        // Validate refund
        // ---------------------------------------------------------
        paymentValidator.validateRefund(payment, request.getRefundAmount());
        // ---------------------------------------------------------
        // Select Gateway
        // ---------------------------------------------------------
        PaymentGatewayService gatewayService = paymentGatewayFactory.getGateway(payment.getPaymentGateway());
        // ---------------------------------------------------------
        // Call Gateway Refund
        // ---------------------------------------------------------
        GatewayRefundResponse response;
        try {
            response = gatewayService.refundPayment(payment, request);
        } catch (Exception ex) {
            log.error("Refund failed. paymentReference={}", payment.getPaymentReference(), ex);
            throw new BusinessException("Unable to process refund.");
        }
        // ---------------------------------------------------------
        // Update Refunded Amount
        // ---------------------------------------------------------
        BigDecimal refundedAmount = payment.getRefundedAmount().add(request.getRefundAmount());
        payment.setRefundedAmount(refundedAmount);
        payment.setGatewayResponse(response.getGatewayResponse());
        payment.setRefundedAt(LocalDateTime.now());
        // ---------------------------------------------------------
        // Update Payment Status
        // ---------------------------------------------------------
        if (refundedAmount.compareTo(payment.getAmount()) == 0) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setPaymentStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        // ---------------------------------------------------------
        // Update Order Payment Status
        // ---------------------------------------------------------
        Order order = payment.getOrder();
        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            order.setPaymentStatus(OrderPaymentStatus.REFUNDED);
        } else {
            order.setPaymentStatus(OrderPaymentStatus.PARTIALLY_REFUNDED);
        }
        // ---------------------------------------------------------
        // Persist
        // ---------------------------------------------------------
        payment = paymentRepository.save(payment);
        orderRepository.save(order);
        log.info("Refund completed successfully. paymentReference={}", payment.getPaymentReference());
        return paymentMapper.toResponse(payment);
    }
    /**
     * Fetch authenticated user.
     */
    private User getCurrentUser() {
        return authService.getAuthenticatedUser();
    }

    /**
     * Fetch authenticated user.
     */
    private CustomUserDetails getCurrentUserDetails() {
        return authService.getAuthenticatedUserDetails();
    }

    /**
     * Fetch order by id.
     */
    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                orderId));
    }

    /**
     * Fetch payment by id.
     */
    private Payment getPaymentEntity(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment",
                                null,
                                null));
    }

    /**
     * Fetch gateway implementation.
     */
    private PaymentGatewayService getGateway(CreatePaymentRequest request) {
        return paymentGatewayFactory.getGateway(request.getPaymentGateway());
    }




}
