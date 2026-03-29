package com.ope.inventory_service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "availability_quantity", nullable = false)
    private int availabilityQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;
}
