package com.e_commerce.shambhu.shipment.validator;

import com.e_commerce.shambhu.common.exception.BusinessException;
import com.e_commerce.shambhu.common.exception.ForbiddenException;
import com.e_commerce.shambhu.order.entity.Order;
import com.e_commerce.shambhu.order.enums.OrderPaymentStatus;
import com.e_commerce.shambhu.order.enums.OrderStatus;
import com.e_commerce.shambhu.order.enums.PaymentMethod;
import com.e_commerce.shambhu.shipment.entity.Shipment;
import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShipmentValidator {

    /**
     * Validate order existence.
     */
    public void validateOrder(Order order) {

        if (order == null) {
            throw new BusinessException("Order does not exist.");
        }
    }

    /**
     * Shipment creation eligibility.
     */
    public void validateShipmentEligibility(Order order) {

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(
                    "Shipment cannot be created for a cancelled order.");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException(
                    "Shipment already completed for this order.");
        }

        /*
         * Online payments must be PAID.
         * COD orders are allowed.
         */
        if (order.getPaymentMethod() != PaymentMethod.COD
                && order.getPaymentStatus() != OrderPaymentStatus.PAID) {

            throw new BusinessException(
                    "Shipment can only be created after successful payment.");
        }
    }

    /**
     * Prevent duplicate shipment.
     */
    public void validateDuplicateShipment(boolean shipmentExists) {

        if (shipmentExists) {
            throw new BusinessException(
                    "Shipment already exists for this order.");
        }
    }

    /**
     * Delivery partner validation.
     */
    public void validateDeliveryPartner(
            DeliveryPartner deliveryPartner) {

        if (deliveryPartner == null) {

            throw new BusinessException(
                    "Delivery partner is required.");
        }

        if (deliveryPartner == DeliveryPartner.NOT_ASSIGNED) {

            throw new BusinessException(
                    "Please select a valid delivery partner.");
        }
    }

    /**
     * Tracking assignment validation.
     */
    public void validateTrackingAssignment(
            Shipment shipment,
            String trackingNumber) {

        if (shipment.getShipmentStatus() == ShipmentStatus.DELIVERED) {

            throw new BusinessException(
                    "Tracking number cannot be updated after delivery.");
        }

        if (trackingNumber == null || trackingNumber.isBlank()) {

            throw new BusinessException(
                    "Courier tracking number is required.");
        }
    }

    /**
     * Shipment status transition validation.
     */
    public void validateShipmentStatusTransition(
            ShipmentStatus current,
            ShipmentStatus next) {

        if (current == next) {
            return;
        }

        switch (current) {

            case CREATED -> {

                if (next != ShipmentStatus.PACKED
                        && next != ShipmentStatus.CANCELLED) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case PACKED -> {

                if (next != ShipmentStatus.READY_FOR_DISPATCH
                        && next != ShipmentStatus.CANCELLED) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case READY_FOR_DISPATCH -> {

                if (next != ShipmentStatus.SHIPPED) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case SHIPPED -> {

                if (next != ShipmentStatus.OUT_FOR_DELIVERY) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case OUT_FOR_DELIVERY -> {

                if (next != ShipmentStatus.DELIVERED
                        && next != ShipmentStatus.DELIVERY_FAILED) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case DELIVERY_FAILED -> {

                if (next != ShipmentStatus.OUT_FOR_DELIVERY
                        && next != ShipmentStatus.RETURNED) {

                    throw new BusinessException(
                            "Invalid shipment status transition.");
                }
            }

            case DELIVERED -> {

                if (next != ShipmentStatus.RETURNED) {

                    throw new BusinessException(
                            "Only return process is allowed after delivery.");
                }
            }

            case RETURNED,
                 CANCELLED ->

                    throw new BusinessException(
                            "Shipment status cannot be changed.");
        }
    }

    /**
     * Shipment cancellation validation.
     */
    public void validateCancellation(Shipment shipment) {

        ShipmentStatus status = shipment.getShipmentStatus();

        if (status == ShipmentStatus.SHIPPED
                || status == ShipmentStatus.OUT_FOR_DELIVERY
                || status == ShipmentStatus.DELIVERED
                || status == ShipmentStatus.RETURNED) {

            throw new BusinessException(
                    "Shipment cannot be cancelled.");
        }
    }

    /**
     * Return validation.
     */
    public void validateReturn(Shipment shipment) {

        if (shipment.getShipmentStatus() != ShipmentStatus.DELIVERED) {

            throw new BusinessException(
                    "Only delivered shipments can be returned.");
        }
    }

    /**
     * Ownership validation.
     */
    public void validateOwnership(
            Long shipmentOwnerId,
            Long currentUserId,
            boolean isAdmin) {

        if (!shipmentOwnerId.equals(currentUserId)
                && !isAdmin) {

            throw new ForbiddenException(
                    "You are not authorized to access this shipment.");
        }
    }

}
