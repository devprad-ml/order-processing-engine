CREATE TABLE IF NOT EXISTS orders (
id UUID PRIMARY KEY,
idempotency_key VARCHAR(255) UNIQUE NOT NULL,
product_id VARCHAR(255) NOT NULL,
quantity INT NOT NULL,
status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
created_at TIMESTAMP DEFAULT NOW(),
updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory (
    product_id VARCHAR(255) PRIMARY KEY,
    availability_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL DEFAULT 0
);

INSERT INTO inventory (product_id, availability_quantity) VALUES
     ('PROD-001', 100),
     ('PROD-002', 50),
     ('PROD-003', 200)
ON CONFLICT DO NOTHING;

