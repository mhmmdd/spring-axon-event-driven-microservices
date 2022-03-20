package com.example.api.OrderService.order;

import com.example.api.OrderService.order.command.ApproveOrderCommand;
import com.example.api.OrderService.order.command.CreateOrderCommand;
import com.example.api.OrderService.order.command.OrderStatusEnum;
import com.example.api.OrderService.order.event.OrderApprovedEvent;
import com.example.api.OrderService.order.event.OrderCreatedEvent;
import com.example.api.OrderService.order.command.RejectOrderCommand;
import com.example.api.OrderService.order.event.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatusEnum orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getUserId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }


    /**
     * Approve
     */

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        // Create and publish the OrderApprovedEvent

        OrderApprovedEvent orderCreatedEvent = OrderApprovedEvent.builder()
                .orderId(approveOrderCommand.getOrderId())
                .build();

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }


    /**
     * Reject
     */
    @CommandHandler
    public void handle(RejectOrderCommand rejectOrderCommand) {
        // Create and publish the OrderApprovedEvent

        OrderRejectedEvent orderRejectedEvent = OrderRejectedEvent.builder()
                .orderId(rejectOrderCommand.getOrderId())
                .reason(rejectOrderCommand.getReason())
                .build();

        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
        this.orderStatus = orderRejectedEvent.getOrderStatus();
    }

}
