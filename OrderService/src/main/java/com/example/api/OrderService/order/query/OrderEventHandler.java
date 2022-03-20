package com.example.api.OrderService.order.query;

import com.example.api.OrderService.order.Order;
import com.example.api.OrderService.order.OrderRepository;
import com.example.api.OrderService.order.event.OrderApprovedEvent;
import com.example.api.OrderService.order.event.OrderCreatedEvent;
import com.example.api.OrderService.order.event.OrderRejectedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Order orderEntity = new Order();
        BeanUtils.copyProperties(event, orderEntity);

        this.orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        Optional<Order> order = orderRepository.findByOrderId(event.getOrderId());

        if (order.isEmpty()) {
            // Todo: do something
            return;
        }

        order.get().setOrderStatus(event.getOrderStatus());
        orderRepository.save(order.get());
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {
        Optional<Order> order = orderRepository.findByOrderId(event.getOrderId());

        if (order.isEmpty()) {
            // Todo: do something
            return;
        }

        order.get().setOrderStatus(event.getOrderStatus());
        orderRepository.save(order.get());
    }
}
