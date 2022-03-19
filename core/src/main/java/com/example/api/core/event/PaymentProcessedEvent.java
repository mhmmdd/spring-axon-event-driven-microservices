package com.example.api.core.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentProcessedEvent {
    private final String orderId;
    private final String paymentId;
}