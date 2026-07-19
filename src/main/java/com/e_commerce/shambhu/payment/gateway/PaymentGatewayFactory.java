package com.e_commerce.shambhu.payment.gateway;

import com.e_commerce.shambhu.common.exception.ResourceNotFoundException;
import com.e_commerce.shambhu.payment.enums.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PaymentGatewayFactory {

    private final Map<PaymentGateway, PaymentGatewayService> gatewayServices;

    public PaymentGatewayFactory(List<PaymentGatewayService> services) {

        this.gatewayServices = new EnumMap<>(PaymentGateway.class);

        services.forEach(service -> {

            PaymentGateway gateway = service.getGateway();

            if (gatewayServices.containsKey(gateway)) {
                throw new IllegalStateException(
                        "Duplicate PaymentGatewayService found for gateway: "
                                + gateway);
            }

            gatewayServices.put(gateway, service);

            log.info("Registered Payment Gateway: {}", gateway);
        });
    }

    public PaymentGatewayService getGateway(PaymentGateway gateway) {

        PaymentGatewayService gatewayService = gatewayServices.get(gateway);

        if (gatewayService == null) {
            throw new ResourceNotFoundException(
                    "Payment gateway '" + gateway + "' is not supported.",
                    null,
                    null
            );
        }

        return gatewayService;
    }

}
