package com.ope.order_service;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderEvent {
    private UUID orderID;
    private String productId;
    private int quantity;
    private String status;
    
}