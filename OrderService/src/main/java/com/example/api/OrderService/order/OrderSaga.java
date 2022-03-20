package com.example.api.OrderService.order;

import com.example.api.OrderService.order.command.ApproveOrderCommand;
import com.example.api.OrderService.order.event.OrderApprovedEvent;
import com.example.api.OrderService.order.event.OrderCreatedEvent;
import com.example.api.OrderService.order.command.RejectOrderCommand;
import com.example.api.OrderService.order.event.OrderRejectedEvent;
import com.example.api.core.command.CancelProductReservationCommand;
import com.example.api.core.command.ProcessPaymentCommand;
import com.example.api.core.command.ReserveProductCommand;
import com.example.api.core.event.PaymentProcessedEvent;
import com.example.api.core.event.ProductReservationCancelledEvent;
import com.example.api.core.event.ProductReservedEvent;
import com.example.api.core.model.User;
import com.example.api.core.query.FetchUserPaymentDetailsQuery;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Log4j2
@NoArgsConstructor
@Saga // serialized
public class OrderSaga {
    // transient means not serialized
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private final String DEADLINE_NAME = "payment-processing-deadline";
    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {

        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        log.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId()
                + " and productId: " + reserveProductCommand.getProductId());

//        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
//            @Override
//            public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
//                if (commandResultMessage.isExceptional()) {
//                    // Start a compensating transaction
//
//                }
//            }
//        });
        commandGateway.sendAndWait(reserveProductCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // Process user payment
        log.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId()
                + " and orderId: " + productReservedEvent.getOrderId());


        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User user = null;

        try {
            user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (user == null) {
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }

        log.info("Successfully fetched user payment details for user " + user.getFirstName());


        // Deadline
        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS), DEADLINE_NAME,
                productReservedEvent);

//        if(true) return;

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }
        if (result == null) {
            log.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        cancelDeadline();

        ApproveOrderCommand approveOrderCommand = ApproveOrderCommand.builder()
                .orderId(paymentProcessedEvent.getOrderId())
                .build();

        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        log.info("Order is approved. Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
        // SagaLifecycle.end();
    }


    /**
     * Cancel Payment
     */
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                .orderId(productReservationCancelledEvent.getOrderId())
                .reason(productReservationCancelledEvent.getReason())
                .build();
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        log.info("Successfully rejected order with id: " + orderRejectedEvent.getOrderId());
    }

    @DeadlineHandler(deadlineName = DEADLINE_NAME)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        log.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();

        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();
        commandGateway.send(cancelProductReservationCommand);
    }

    private void cancelDeadline() {
        // Cancel the deadline
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(DEADLINE_NAME, scheduleId);
            scheduleId = null;
            // deadlineManager.cancelAll(DEADLINE_NAME);
        }
    }
}
