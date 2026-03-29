package com.ope.order_service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor

// create the service obj

public class OrderController {
    private final OrderService orderService;
   // the response entity obj is checking the format of the request
    @PostMapping
    public ResponseEntity<Order> createOrder (
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody CreateOrderRequest request) {
            Order order = orderService.createOrder(idempotencyKey, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(order);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Order> getOrder(@PathVariable UUID id) {
            return ResponseEntity.ok(orderService.getOrder(id));
        }
    }
