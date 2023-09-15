package com.study.controller;

import com.study.dto.ProductEvent;
import com.study.service.ProductCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductCommandController {
    private final ProductCommandService productCommandService;
    @PostMapping("/create")
    public ResponseEntity createProduct(@RequestBody ProductEvent productEvent){
        return ResponseEntity.ok(productCommandService.create(productEvent));
    }
    @PutMapping("/update")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody ProductEvent productEvent)
            throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(productCommandService.update(id,productEvent));
    }

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            for (int i = 0; i <= 100000; i++) {
                productCommandService.sendMessageToTopic(message + " : " + i);
            }
            return ResponseEntity.ok("message published successfully ..");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/publish")
    public void sendEvents(@RequestBody ProductEvent productEvent) {
        productCommandService.sendEventsToTopic(productEvent);
    }

}
