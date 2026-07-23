package com.e_commerce.shambhu.shipment.dto.request;

import com.e_commerce.shambhu.shipment.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShipmentStatusRequest {

    @NotNull(message = "Shipment id is required.")
    private Long shipmentId;

    @NotNull(message = "Shipment status is required.")
    private ShipmentStatus shipmentStatus;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters.")
    private String remarks;

    @Size(max = 500, message = "Failure reason cannot exceed 500 characters.")
    private String failureReason;
}
