package com.example.api.OrderService.order.event;

import com.example.api.OrderService.order.command.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatusEnum orderStatus = OrderStatusEnum.REJECTED;
}
