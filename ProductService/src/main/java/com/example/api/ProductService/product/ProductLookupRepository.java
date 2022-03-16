package com.example.api.ProductService.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLookupRepository extends JpaRepository<ProductLookup, String> {
    Optional<ProductLookup> findByProductIdOrTitle(String productId, String title);
}
