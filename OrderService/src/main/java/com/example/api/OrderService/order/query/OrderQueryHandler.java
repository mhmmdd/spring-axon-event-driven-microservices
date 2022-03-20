package com.example.api.OrderService.order.query;

import com.example.api.OrderService.order.Order;
import com.example.api.OrderService.order.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderQueryHandler {

    private final OrderRepository orderRepository;

    public OrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        Optional<Order> order = orderRepository.findByOrderId(findOrderQuery.getOrderId());
        if(order.isEmpty()) {
            return null;
        }
        return new OrderSummary(order.get().getOrderId(), order.get().getOrderStatus(), "");
    }
}
