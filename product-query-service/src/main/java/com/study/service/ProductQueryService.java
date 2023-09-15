package com.study.service;

import com.study.dto.ProductEvent;
import com.study.entity.Product;
import com.study.repository.ProductReposiroty;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ProductReposiroty productReposiroty;
    public List<Product> getAllProduct() {
       return productReposiroty.findAll();
    }

    @KafkaListener(topics = "product-event-topic", groupId = "product-event-group")
    public void processProudctEvent (ProductEvent productEvent){
        Product product = productEvent.getProduct();
        System.out.println("This is event product: "+ product);
        if (productEvent.getEventtype().equals("CreateProduct")){
            productReposiroty.save(productEvent.getProduct());
        }
        if (productEvent.getEventtype().equals("UpdateProduct")){
            Product updateProduct = productReposiroty.findById(product.getId()).get();
            updateProduct.setName(product.getName());
            updateProduct.setDescription(product.getDescription());
            updateProduct.setPrice(product.getPrice());
            productReposiroty.save(updateProduct);
        }
    }


}
