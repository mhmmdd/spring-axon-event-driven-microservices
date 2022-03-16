package com.example.api.ProductService.product.command;

import com.example.api.ProductService.product.event.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        // Validate Create Product Command

        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero");
        }

        if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        // 2. Validate Command
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        // from -> to
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        // Call EventSourcingHandler
        AggregateLifecycle.apply(productCreatedEvent);

//        if (true) {
//            throw new Exception("An error took place in CreateProductCommand @CommandHandler method");
//        }
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        // 3. Get the Event
        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.title = productCreatedEvent.getTitle();
        this.quantity = productCreatedEvent.getQuantity();
    }
}