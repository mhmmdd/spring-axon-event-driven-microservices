package com.example.api.ProductService.product.event;

import com.example.api.ProductService.product.ProductLookup;
import com.example.api.ProductService.product.ProductLookupRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventHandler {
    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        // 5. Catch the Event
        ProductLookup productLookup = new ProductLookup(event.getProductId(), event.getTitle());
        productLookupRepository.save(productLookup);
    }

    @ResetHandler
    public void reset() {
        productLookupRepository.deleteAll();
    }
}
