package com.e_commerce.shambhu.payment.util;

public final class MockIdGenerator {

    private MockIdGenerator() {
    }

    public static String gatewayOrderId() {
        return "MOCK_ORDER_" + System.currentTimeMillis();
    }

    public static String transactionId() {
        return "MOCK_TXN_" + System.currentTimeMillis();
    }

    public static String refundId() {
        return "MOCK_REFUND_" + System.currentTimeMillis();
    }
}
