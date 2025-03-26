CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS orders (
    id UUID DEFAULT uuid_generate_v4 () PRIMARY KEY,
    owner VARCHAR(50) NOT NULL,
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50),
    delivery_weight DECIMAL,
    delivery_volume DECIMAL,
    fragile BOOLEAN,
    total_price DECIMAL,
    delivery_price DECIMAL,
    product_price DECIMAL,
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID REFERENCES orders(id),
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);