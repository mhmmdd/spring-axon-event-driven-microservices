package com.example.api.ProductService.product.query;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final QueryGateway queryGateway;

    public ProductQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestDTO> getProducts() {
        ProductQuery productQuery = new ProductQuery();
        // 1. Query
        List<ProductRestDTO> productRestDTOs = queryGateway.query(productQuery,
                ResponseTypes.multipleInstancesOf(ProductRestDTO.class)).join();

        // 3. Return data
        return productRestDTOs;
    }
}
