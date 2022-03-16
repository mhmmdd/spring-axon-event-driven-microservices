package com.example.api.ProductService.product.query;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRestDTO {
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
