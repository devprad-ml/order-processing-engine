package com.ope.inventory_service;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private UUID orderId;
    private String productId;
    private int quantity;
    private String status;
}
