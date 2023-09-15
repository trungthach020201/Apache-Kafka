package com.study.service;

import com.study.entity.Product;
import com.study.dto.ProductEvent;
import com.study.repository.ProductReposiroty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductReposiroty productReposiroty;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public Product create(ProductEvent productEvent) {
        Product productDO = productReposiroty.save(productEvent.getProduct());
        ProductEvent event = new ProductEvent("CreateProduct",productDO);
        kafkaTemplate.send("product-event-topic", event);
        return productDO;
    }

    public Product update(Long id,ProductEvent productEvent) throws ChangeSetPersister.NotFoundException {
        Product updateProduct = productReposiroty.findById(id).orElseThrow(()-> new ChangeSetPersister.NotFoundException());
        Product product = productEvent.getProduct();
        updateProduct.setName(product.getName());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setDescription(product.getDescription());
        Product productDO = productReposiroty.save(updateProduct);
        ProductEvent event = new ProductEvent("UpdateProduct",productDO);
        kafkaTemplate.send("product-event-topic", event);
        return productDO;
    }

//    Test
    public void sendMessageToTopic(String message){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("product-event-topic", message);
        future.whenComplete((result,ex)->{
            if (ex == null) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "] of partition=["+result.getRecordMetadata().partition()+"]");
            } else {
                System.out.println("Unable to send message=[" +
                        message + "] due to : " + ex.getMessage());
            }
        });

    }

    public void sendEventsToTopic(ProductEvent productEvent) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("product-event-topic", productEvent);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + productEvent.toString() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            productEvent.toString() + "] due to : " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            System.out.println("ERROR : "+ ex.getMessage());
        }
    }



}
