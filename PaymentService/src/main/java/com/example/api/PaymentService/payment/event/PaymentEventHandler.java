package com.example.api.PaymentService.payment.event;

import com.example.api.PaymentService.payment.Payment;
import com.example.api.PaymentService.payment.PaymentRepository;
import com.example.api.core.event.PaymentProcessedEvent;
import lombok.extern.log4j.Log4j2;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PaymentEventHandler {

    private final PaymentRepository paymentRepository;

    public PaymentEventHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        log.info("PaymentProcessedEvent is called for orderId: " + event.getOrderId());

        Payment payment = new Payment();
        BeanUtils.copyProperties(event, payment);

        paymentRepository.save(payment);

    }
}