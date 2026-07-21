package com.e_commerce.shambhu.shipment.enums;

public enum DeliveryPartner {

    /**
     * Delhivery Logistics
     */
    DELHIVERY("Delhivery"),

    /**
     * Blue Dart Express
     */
    BLUE_DART("Blue Dart"),

    /**
     * DTDC Courier
     */
    DTDC("DTDC"),

    /**
     * XpressBees Logistics
     */
    XPRESSBEES("XpressBees"),

    /**
     * Ekart Logistics
     */
    EKART("Ekart"),

    /**
     * India Post
     */
    INDIA_POST("India Post"),

    /**
     * DHL Express
     */
    DHL("DHL"),

    /**
     * FedEx
     */
    FEDEX("FedEx"),

    /**
     * Amazon Shipping
     */
    AMAZON_SHIPPING("Amazon Shipping"),

    /**
     * Self Delivery / Local Delivery
     */
    SELF_DELIVERY("Self Delivery"),

    /**
     * Shipment partner not assigned yet.
     */
    NOT_ASSIGNED("Not Assigned");

    private final String displayName;

    DeliveryPartner(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
