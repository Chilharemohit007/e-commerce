package com.e_commerce.shambhu.order.dto.request;

import com.e_commerce.shambhu.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required.")
    private OrderStatus status;
}
