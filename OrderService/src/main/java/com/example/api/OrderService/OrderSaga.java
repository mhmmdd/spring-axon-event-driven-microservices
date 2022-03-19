package com.example.api.OrderService;

import com.example.api.OrderService.order.event.OrderCreatedEvent;
import com.example.api.core.command.ReserveProductCommand;
import com.example.api.core.event.ProductReservedEvent;
import com.example.api.core.model.User;
import com.example.api.core.query.FetchUserPaymentDetailsQuery;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@NoArgsConstructor
@Saga // serialized
public class OrderSaga {
    // transient means not serialized
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

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
            return;
        }

        if (user == null) {
            // Start compensating transaction
            return;
        }

        log.info("Successfully fetched user payment details for user " + user.getFirstName());

    }
}
