package com.example.api.ProductService.product.query;

import com.example.api.ProductService.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    Product findByProductId(String productId);

    Product findByProductIdOrTitle(String productId, String title);
}
