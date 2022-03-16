package com.example.api.OrderService.order;

import com.example.api.OrderService.order.command.OrderStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "order")
public class Order implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    @Id
    @Column(unique = true)
    public String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus;
}
