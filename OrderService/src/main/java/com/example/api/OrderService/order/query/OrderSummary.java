package com.example.api.OrderService.order.query;

import com.example.api.OrderService.order.command.OrderStatusEnum;
import lombok.Value;

@Value
public class OrderSummary {
    String orderId;
    OrderStatusEnum orderStatus;
    String message;
}
