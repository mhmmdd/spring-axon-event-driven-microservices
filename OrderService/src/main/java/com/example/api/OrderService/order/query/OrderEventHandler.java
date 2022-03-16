package com.example.api.OrderService.order.query;

import com.example.api.OrderService.order.Order;
import com.example.api.OrderService.order.OrderRepository;
import com.example.api.OrderService.order.event.OrderCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

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

}
