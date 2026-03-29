# Order Processing Engine

A microservices-based order processing system built with Spring Boot, Apache Kafka, and PostgreSQL. The system uses event-driven architecture to handle the full order lifecycle: creation, inventory validation, and payment processing.

## Architecture

```
Client ──> Order Service ──(order.created)──> Inventory Service
                 ^                                   │
                 │                          ┌────────┴────────┐
                 │                          │                  │
                 │               inventory.confirmed    inventory.failed
                 │                          │                  │
                 │                          v                  │
                 │                   Payment Service           │
                 │                          │                  │
                 │                 ┌────────┴────────┐         │
                 │                 │                  │         │
                 │          payment.confirmed   payment.failed  │
                 │                 │                  │         │
                 └─────────────────┴──────────────────┴─────────┘
```

Services communicate exclusively through Kafka topics — no direct REST calls between services.

## Tech Stack

- **Java 17** / **Spring Boot 3.2.5**
- **Apache Kafka** (event streaming)
- **PostgreSQL 15** (persistence)
- **Redis 7** (caching)
- **Docker Compose** (infrastructure)
- **Maven** (build)
- **Lombok** (boilerplate reduction)

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| order-service | 8080 | PostgreSQL | REST API for order creation/retrieval, orchestrates order lifecycle |
| inventory-service | 8081 | PostgreSQL | Validates product availability and reserves stock |
| payment-service | 8082 | None | Processes payments (currently simulated) |

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose

### 1. Start infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL (port 5433), Redis (port 6379), Zookeeper, and Kafka (port 9092). The `init.sql` script automatically creates tables and seeds inventory data.

### 2. Build and run services

```bash
# Build all services
cd order-service && mvn clean package -DskipTests && cd ..
cd inventory-service && mvn clean package -DskipTests && cd ..
cd payment-service && mvn clean package -DskipTests && cd ..

# Run each service in a separate terminal
java -jar order-service/target/*.jar
java -jar inventory-service/target/*.jar
java -jar payment-service/target/*.jar
```

### 3. Create an order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: unique-key-001" \
  -d '{"productId": "PROD-001", "quantity": 5}'
```

### 4. Check order status

```bash
curl http://localhost:8080/orders/{order-id}
```

## API Reference

### POST /orders

Creates a new order.

**Headers:**
- `Content-Type: application/json`
- `Idempotency-Key: <unique-string>` (required — prevents duplicate orders)

**Request body:**
```json
{
  "productId": "PROD-001",
  "quantity": 5
}
```

**Response (201):**
```json
{
  "id": "a1b2c3d4-...",
  "idempotencyKey": "unique-key-001",
  "productId": "PROD-001",
  "quantity": 5,
  "status": "PENDING",
  "createdAt": "2026-03-29T10:00:00",
  "updatedAt": "2026-03-29T10:00:00"
}
```

### GET /orders/{id}

Retrieves an order by UUID. Returns 404 if not found.

## Kafka Topics

| Topic | Producer | Consumer | Trigger |
|-------|----------|----------|---------|
| `order.created` | order-service | inventory-service | New order placed |
| `inventory.confirmed` | inventory-service | payment-service | Stock reserved |
| `inventory.failed` | inventory-service | order-service | Insufficient stock |
| `payment.confirmed` | payment-service | order-service | Payment succeeded |
| `payment.failed` | payment-service | order-service | Payment failed |

## Order Status Flow

```
PENDING ──> CONFIRMED   (inventory available + payment successful)
PENDING ──> CANCELLED   (insufficient inventory OR payment failure)
```

## Seeded Inventory

| Product ID | Available Quantity |
|------------|-------------------|
| PROD-001 | 100 |
| PROD-002 | 50 |
| PROD-003 | 200 |

## Project Structure

```
order-processing-engine/
├── docker-compose.yml
├── init.sql
├── order-service/
│   └── src/main/java/com/ope/order_service/
│       ├── Order.java
│       ├── OrderRepository.java
│       ├── OrderService.java
│       ├── OrderController.java
│       ├── OrderEvent.java
│       ├── OrderEventListener.java
│       ├── CreateOrderRequest.java
│       └── OrderNotFoundException.java
├── inventory-service/
│   └── src/main/java/com/ope/inventory_service/
│       ├── Inventory.java
│       ├── InventoryRepository.java
│       ├── InventoryService.java
│       ├── InventoryEventListener.java
│       └── OrderEvent.java
└── payment-service/
    └── src/main/java/com/ope/payment_service/
        ├── PaymentService.java
        ├── PaymentEventListener.java
        └── OrderEvent.java
```
