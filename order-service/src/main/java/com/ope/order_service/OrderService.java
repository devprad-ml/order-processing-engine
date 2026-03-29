package com.ope.order_service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Slf4j

public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public Order createOrder(String idempotencyKey, String productId, int quantity) {
        // idempotency check - same key return same order, no duplicate
        Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Duplicate request detected for key: {}", idempotencyKey);
            return existing.get();
        }
        // save order as PENDING
        Order order = Order.builder()
         .idempotencyKey(idempotencyKey)
         .productId(productId)
         .quantity(quantity)
         .status("PENDING")
         .build();
        orderRepository.save(order);
        log.info("Order created: {}", order.getId());

        // Publish event to Kafka
        OrderEvent event = new OrderEvent(
            order.getId(),
            productId,
            quantity,
            "PENDING"
        );
        kafkaTemplate.send("order.created", event);
        log.info("Published order.created event for order: {}", order.getId());

        return order;
    }
   // now we create an obj which returns the Order class or throws an exception
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

    }
    // func which updates the order status

    public void updateOrderStatus(UUID orderId, String status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, status);
    }

}