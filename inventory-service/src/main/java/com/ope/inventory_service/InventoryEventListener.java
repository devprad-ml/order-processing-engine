package com.ope.inventory_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void onOrderCreated(OrderEvent event) {
        log.info("Received order.created event for order: {}", event.getOrderId());
        inventoryService.reserveInventory(event);
    }
}
