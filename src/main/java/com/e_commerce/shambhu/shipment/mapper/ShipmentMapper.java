package com.e_commerce.shambhu.shipment.mapper;

import com.e_commerce.shambhu.shipment.entity.Shipment;
import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    /**
     * CreateShipmentRequest → Shipment
     */
    public Shipment toEntity(CreateShipmentRequest request) {

        if (request == null) {
            return null;
        }

        return Shipment.builder()
                .deliveryPartner(request.getDeliveryPartner())
                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                .shippingCharge(request.getShippingCharge())
                .remarks(request.getRemarks())
                .shipmentStatus(ShipmentStatus.CREATED)
                .build();
    }

    /**
     * Shipment → ShipmentResponse
     */
    public ShipmentResponse toResponse(Shipment shipment) {

        if (shipment == null) {
            return null;
        }

        return ShipmentResponse.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrder() != null ? shipment.getOrder().getId() : null)
                .orderNumber(shipment.getOrder() != null ? shipment.getOrder().getOrderNumber() : null)
                .customerId(shipment.getOrder() != null && shipment.getOrder().getUser() != null
                        ? shipment.getOrder().getUser().getId()
                        : null)
                .customerName(shipment.getOrder() != null && shipment.getOrder().getUser() != null
                        ? shipment.getOrder().getUser().getFirstName() + " " + shipment.getOrder().getUser().getLastName()
                        : null)
                .trackingNumber(shipment.getTrackingNumber())
                .courierTrackingNumber(shipment.getCourierTrackingNumber())
                .courierReferenceId(shipment.getCourierReferenceId())
                .deliveryPartner(shipment.getDeliveryPartner())
                .shipmentStatus(shipment.getShipmentStatus())
                .shippingCharge(shipment.getShippingCharge())
                .estimatedDeliveryDate(shipment.getEstimatedDeliveryDate())
                .actualDeliveryDate(shipment.getActualDeliveryDate())
                .dispatchedAt(shipment.getDispatchedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .returnedAt(shipment.getReturnedAt())
                .remarks(shipment.getRemarks())
                .createdAt(shipment.getCreatedAt())
                .build();
    }

    /**
     * Shipment → ShipmentSummaryResponse
     */
    public ShipmentSummaryResponse toSummaryResponse(Shipment shipment) {

        if (shipment == null) {
            return null;
        }

        return ShipmentSummaryResponse.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrder() != null ? shipment.getOrder().getId() : null)
                .orderNumber(shipment.getOrder() != null ? shipment.getOrder().getOrderNumber() : null)
                .trackingNumber(shipment.getTrackingNumber())
                .deliveryPartner(shipment.getDeliveryPartner())
                .shipmentStatus(shipment.getShipmentStatus())
                .estimatedDeliveryDate(shipment.getEstimatedDeliveryDate())
                .actualDeliveryDate(shipment.getActualDeliveryDate())
                .build();
    }

    /**
     * Page<Shipment> → Page<ShipmentSummaryResponse>
     */
    public Page<ShipmentSummaryResponse> toSummaryPage(Page<Shipment> shipments) {

        if (shipments == null) {
            return Page.empty();
        }

        return shipments.map(this::toSummaryResponse);
    }
}
