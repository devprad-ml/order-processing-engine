// JPA (Java Persistence API) 
// maps obj to a table in the database ( obj --> database table)
// we use Lombok to reduce the boilerplate code(Java is famous for this)
// UUID (universally unique identifier)

package com.ope.order_service;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "orders")
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder

// creating the Order class

public class Order {
    // set the @Id at the start so java know to always get or set 
    // using this unique ID
    @Id
    @Column(columnDefinition= "uuid")
    private UUID id;

    @Column(name="idempotency_key", unique = true, nullable = false)
    private String idempotencyKey;

    @Column(name ="product_id", nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id==null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();



    }

}