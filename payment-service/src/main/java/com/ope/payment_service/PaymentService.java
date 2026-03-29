package com.ope.payment_service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void processPayment(OrderEvent event) {
        log.info("Processing payment for order: {}", event.getOrderId());

        // Simulate payment processing - in production this would call a payment gateway
        boolean paymentSuccess = simulatePayment(event);

        if (paymentSuccess) {
            OrderEvent confirmedEvent = new OrderEvent(
                    event.getOrderId(), event.getProductId(), event.getQuantity(), "PAYMENT_CONFIRMED");
            kafkaTemplate.send("payment.confirmed", confirmedEvent);
            log.info("Payment confirmed for order: {}", event.getOrderId());
        } else {
            OrderEvent failedEvent = new OrderEvent(
                    event.getOrderId(), event.getProductId(), event.getQuantity(), "PAYMENT_FAILED");
            kafkaTemplate.send("payment.failed", failedEvent);
            log.warn("Payment failed for order: {}", event.getOrderId());
        }
    }

    private boolean simulatePayment(OrderEvent event) {
        // Simulate: all payments succeed for now
        return true;
    }
}
