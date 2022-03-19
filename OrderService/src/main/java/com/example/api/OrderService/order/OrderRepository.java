package com.example.api.OrderService.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository <Order, String>{
    Optional<Order> findByOrderId(String orderId);
}
