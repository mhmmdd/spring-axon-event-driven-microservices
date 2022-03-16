package com.example.api.ProductService.product.command;

import com.example.api.ProductService.product.ProductLookup;
import com.example.api.ProductService.product.ProductLookupRepository;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Log4j2
@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }


    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {

        return (index, command) -> {

            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                log.info("Intercepted command: " + command.getPayload());

                // 1. Interceptor
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                Optional<ProductLookup> productLookup = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(),
                        createProductCommand.getTitle());

                if (productLookup.isPresent()) {
                    throw new IllegalStateException(String.format("Product with productId %s or title %s already exist",
                            createProductCommand.getProductId(), createProductCommand.getTitle()));
                }

//                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
//                    throw new IllegalArgumentException("Price cannot be less or equal than zero");
//                }
//
//                if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
//                    throw new IllegalArgumentException("Title cannot be empty");
//                }
            }

            return command;
        };
    }
}
