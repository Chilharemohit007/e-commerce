package com.e_commerce.shambhu.common.exception;

import com.e_commerce.shambhu.payment.enums.PaymentGateway;

public class UnsupportedPaymentGatewayException extends BusinessException {

    public UnsupportedPaymentGatewayException(PaymentGateway gateway) {
        super("Payment gateway '" + gateway + "' is not supported.");
    }
}
