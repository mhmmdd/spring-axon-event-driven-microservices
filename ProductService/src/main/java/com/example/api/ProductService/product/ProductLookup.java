package com.example.api.ProductService.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product_lookup")
public class ProductLookup implements Serializable {


    private static final long serialVersionUID = 8669970677371598242L;

    @Id
    private String productId;
    @Column(unique = true)
    private String title;
}
