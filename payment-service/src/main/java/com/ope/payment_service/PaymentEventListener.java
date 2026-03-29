package com.ope.payment_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = "inventory.confirmed", groupId = "payment-service")
    public void onInventoryConfirmed(OrderEvent event) {
        log.info("Received inventory.confirmed event for order: {}", event.getOrderId());
        paymentService.processPayment(event);
    }
}
