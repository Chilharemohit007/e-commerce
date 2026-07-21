package com.e_commerce.shambhu.shipment.enums;

public enum ShipmentStatus {

    /**
     * Shipment record has been created.
     */
    CREATED,

    /**
     * Order has been packed and is ready for dispatch.
     */
    PACKED,

    /**
     * Shipment has been assigned to a delivery partner.
     */
    READY_FOR_DISPATCH,

    /**
     * Shipment has left the warehouse.
     */
    SHIPPED,

    /**
     * Shipment is out for delivery.
     */
    OUT_FOR_DELIVERY,

    /**
     * Shipment has been delivered successfully.
     */
    DELIVERED,

    /**
     * Delivery attempt failed.
     */
    DELIVERY_FAILED,

    /**
     * Shipment has been returned to the warehouse.
     */
    RETURNED,

    /**
     * Shipment has been cancelled before dispatch.
     */
    CANCELLED
}
