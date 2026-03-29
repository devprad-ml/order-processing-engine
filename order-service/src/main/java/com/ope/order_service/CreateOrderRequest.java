package com.ope.order_service;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String productId;
    private int quantity;
}