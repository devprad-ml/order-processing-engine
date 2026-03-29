package com.ope.inventory_service;

import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public void reserveInventory(OrderEvent event) {
        Optional<Inventory> optInventory = inventoryRepository.findById(event.getProductId());

        if (optInventory.isEmpty()) {
            log.warn("Product not found: {}", event.getProductId());
            publishEvent("inventory.failed", event, "INVENTORY_FAILED");
            return;
        }

        Inventory inventory = optInventory.get();
        int available = inventory.getAvailabilityQuantity() - inventory.getReservedQuantity();

        if (available < event.getQuantity()) {
            log.warn("Insufficient inventory for product: {}. Available: {}, Requested: {}",
                    event.getProductId(), available, event.getQuantity());
            publishEvent("inventory.failed", event, "INVENTORY_FAILED");
            return;
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + event.getQuantity());
        inventoryRepository.save(inventory);
        log.info("Inventory reserved for order: {}. Product: {}, Quantity: {}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        publishEvent("inventory.confirmed", event, "INVENTORY_CONFIRMED");
    }

    private void publishEvent(String topic, OrderEvent event, String status) {
        OrderEvent outEvent = new OrderEvent(event.getOrderId(), event.getProductId(), event.getQuantity(), status);
        kafkaTemplate.send(topic, outEvent);
        log.info("Published {} for order: {}", topic, event.getOrderId());
    }
}
