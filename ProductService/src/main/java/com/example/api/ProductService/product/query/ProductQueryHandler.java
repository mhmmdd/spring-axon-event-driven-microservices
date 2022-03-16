package com.example.api.ProductService.product.query;

import com.example.api.ProductService.product.Product;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductQueryHandler {
    private final ProductRepository productRepository;

    public ProductQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestDTO> findProducts(ProductQuery productQuery) {
        List<ProductRestDTO> productRestDTOs = new ArrayList<>();

        // 2. Get data
        List<Product> products = productRepository.findAll();

        for(Product product: products) {
            ProductRestDTO productRestDTO = new ProductRestDTO();
            BeanUtils.copyProperties(product, productRestDTO);
            productRestDTOs.add(productRestDTO);
        }

        return productRestDTOs;
    }

}
