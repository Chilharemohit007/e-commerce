package com.e_commerce.shambhu.shipment.dto.request;

import com.e_commerce.shambhu.shipment.enums.DeliveryPartner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTrackingRequest {

    @NotNull(message = "Shipment id is required.")
    private Long shipmentId;

    @NotNull(message = "Delivery partner is required.")
    private DeliveryPartner deliveryPartner;

    @NotBlank(message = "Courier tracking number is required.")
    @Size(max = 100, message = "Courier tracking number cannot exceed 100 characters.")
    private String courierTrackingNumber;

    @Size(max = 100, message = "Courier reference id cannot exceed 100 characters.")
    private String courierReferenceId;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters.")
    private String remarks;

}