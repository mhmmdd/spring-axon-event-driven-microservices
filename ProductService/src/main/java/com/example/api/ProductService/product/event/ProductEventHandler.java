package com.example.api.ProductService.product.event;

import com.example.api.ProductService.product.Product;
import com.example.api.ProductService.product.query.ProductRepository;
import com.example.api.core.event.ProductReservedEvent;
import lombok.extern.log4j.Log4j2;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {

    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }


    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        // 4. Catch the Event
        Product product = new Product();
        BeanUtils.copyProperties(event, product);

        try {
            productRepository.save(product);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

//        if (true) {
//            throw new Exception("Forcing exception in the Event Handler class");
//        }
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        Product product = productRepository.findByProductId(productReservedEvent.getProductId());
        product.setQuantity(product.getQuantity() - productReservedEvent.getQuantity());
        productRepository.save(product);

        log.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId()
                + " and orderId: " + productReservedEvent.getOrderId());
    }
}
