package com.example.api.ProductService.product.command;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductCommandController {

    private final Environment environment;
    private final CommandGateway commandGateway;

    public ProductCommandController(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String create(@Valid @RequestBody CreateProductDTO createProductDTO) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .productId(UUID.randomUUID().toString())
                .price(createProductDTO.getPrice())
                .quantity(createProductDTO.getQuantity())
                .title(createProductDTO.getTitle())
                .build();

        String result = commandGateway.sendAndWait(createProductCommand);;
//        try {
//            // 1. Create Command
//            result = commandGateway.sendAndWait(createProductCommand);
//        } catch (Exception ex) {
//            result = ex.getLocalizedMessage();
//        }
        // 6. Return the result
        return result;
    }

//    @GetMapping
//    public String get() {
//        return "HTTP GET Handled " + environment.getProperty("local.server.port");
//    }
//
//    @PutMapping
//    public String update() {
//        return "HTTP PUT Handled";
//    }
}
