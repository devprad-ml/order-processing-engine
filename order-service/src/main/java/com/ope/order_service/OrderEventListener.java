package com.ope.order_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = "payment.confirmed", groupId = "order-service")
    public void onPaymentConfirmed(OrderEvent event) {
        log.info("Payment confirmed for order: {}", event.getOrderId());
        orderService.updateOrderStatus(event.getOrderId(), "CONFIRMED");
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service")
    public void onPaymentFailed(OrderEvent event) {
        log.info("Payment failed for order: {}", event.getOrderId());
        orderService.updateOrderStatus(event.getOrderId(), "CANCELLED");
    }

    @KafkaListener(topics = "inventory.failed", groupId = "order-service")
    public void onInventoryFailed(OrderEvent event) {
        log.info("Inventory check failed for order: {}", event.getOrderId());
        orderService.updateOrderStatus(event.getOrderId(), "CANCELLED");
    }
}