package com.example.api.PaymentService.payment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 3924177606013772142L;

    @Id
    private String paymentId;

    @Column
    public String orderId;


}