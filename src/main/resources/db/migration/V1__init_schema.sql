-- ============================================================
-- V1 - Initial Schema for Barbershop Application
-- ============================================================

-- USERS
CREATE TABLE users (
    id             BIGSERIAL PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    phone          VARCHAR(20),
    password_hash  VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL CHECK (role IN ('USER', 'BARBER', 'ADMIN')),
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    profile_photo  VARCHAR(500),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

-- BARBER PROFILES
CREATE TABLE barber_profiles (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES users (id),
    shop_name  VARCHAR(200) NOT NULL,
    bio        TEXT,
    address    VARCHAR(500),
    city       VARCHAR(100),
    latitude   DECIMAL(10, 7),
    longitude  DECIMAL(10, 7),
    phone      VARCHAR(20),
    is_visible BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- OPENING HOURS
CREATE TABLE opening_hours (
    id                BIGSERIAL PRIMARY KEY,
    barber_id         BIGINT   NOT NULL REFERENCES barber_profiles (id),
    day_of_week       SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    open_time         TIME,
    close_time        TIME,
    is_closed         BOOLEAN  NOT NULL DEFAULT FALSE,
    slot_duration_min SMALLINT NOT NULL DEFAULT 30,
    UNIQUE (barber_id, day_of_week)
);

-- CLOSING DAYS
CREATE TABLE closing_days (
    id          BIGSERIAL PRIMARY KEY,
    barber_id   BIGINT       NOT NULL REFERENCES barber_profiles (id),
    closed_date DATE         NOT NULL,
    reason      VARCHAR(200),
    UNIQUE (barber_id, closed_date)
);

-- SERVICES CATALOG
CREATE TABLE services (
    id            BIGSERIAL PRIMARY KEY,
    barber_id     BIGINT        NOT NULL REFERENCES barber_profiles (id),
    name          VARCHAR(200)  NOT NULL,
    category      VARCHAR(50)   NOT NULL CHECK (category IN ('MENS_CUT', 'WOMENS_CUT', 'BEARD', 'TREATMENT', 'OTHER')),
    description   TEXT,
    price         DECIMAL(10, 2) NOT NULL,
    duration_min  SMALLINT      NOT NULL DEFAULT 30,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    display_order SMALLINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- PRODUCTS
CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    barber_id       BIGINT        NOT NULL REFERENCES barber_profiles (id),
    name            VARCHAR(200)  NOT NULL,
    description     TEXT,
    sku             VARCHAR(100),
    price_sell      DECIMAL(10, 2) NOT NULL,
    price_cost      DECIMAL(10, 2),
    stock_quantity  INT           NOT NULL DEFAULT 0,
    stock_alert_min INT           NOT NULL DEFAULT 5,
    is_for_sale     BOOLEAN       NOT NULL DEFAULT TRUE,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    category        VARCHAR(100),
    photo_url       VARCHAR(500),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- APPOINTMENTS
CREATE TABLE appointments (
    id             BIGSERIAL PRIMARY KEY,
    barber_id      BIGINT   NOT NULL REFERENCES barber_profiles (id),
    client_id      BIGINT   NOT NULL REFERENCES users (id),
    service_id     BIGINT   NOT NULL REFERENCES services (id),
    start_time     TIMESTAMP NOT NULL,
    end_time       TIMESTAMP NOT NULL,
    status         VARCHAR(30) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
    queue_position INT,
    notes          TEXT,
    reminder_sent  BOOLEAN  NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_appointments_barber_date ON appointments (barber_id, start_time);
CREATE INDEX idx_appointments_client ON appointments (client_id);
CREATE INDEX idx_appointments_status ON appointments (status);

-- INVOICES
CREATE TABLE invoices (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT        NOT NULL UNIQUE REFERENCES appointments (id),
    barber_id      BIGINT        NOT NULL REFERENCES barber_profiles (id),
    client_id      BIGINT        NOT NULL REFERENCES users (id),
    invoice_number VARCHAR(50)   NOT NULL UNIQUE,
    issued_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    subtotal       DECIMAL(10, 2) NOT NULL,
    tax_rate       DECIMAL(5, 2) NOT NULL DEFAULT 0,
    tax_amount     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total          DECIMAL(10, 2) NOT NULL,
    pdf_url        VARCHAR(500),
    notes          TEXT
);

-- INVOICE LINES
CREATE TABLE invoice_lines (
    id          BIGSERIAL PRIMARY KEY,
    invoice_id  BIGINT        NOT NULL REFERENCES invoices (id),
    line_type   VARCHAR(20)   NOT NULL CHECK (line_type IN ('SERVICE', 'PRODUCT')),
    service_id  BIGINT REFERENCES services (id),
    product_id  BIGINT REFERENCES products (id),
    label       VARCHAR(200)  NOT NULL,
    unit_price  DECIMAL(10, 2) NOT NULL,
    quantity    INT           NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL
);

-- PRODUCT SALES
CREATE TABLE product_sales (
    id         BIGSERIAL PRIMARY KEY,
    barber_id  BIGINT        NOT NULL REFERENCES barber_profiles (id),
    client_id  BIGINT REFERENCES users (id),
    product_id BIGINT        NOT NULL REFERENCES products (id),
    invoice_id BIGINT REFERENCES invoices (id),
    quantity   INT           NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    sale_date  TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- STOCK MOVEMENTS
CREATE TABLE stock_movements (
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT      NOT NULL REFERENCES products (id),
    movement_type  VARCHAR(20) NOT NULL CHECK (movement_type IN ('IN', 'OUT', 'ADJUSTMENT', 'SALE', 'USED_IN_SERVICE')),
    quantity       INT         NOT NULL,
    reference_id   BIGINT,
    reference_type VARCHAR(30),
    notes          TEXT,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- SUPPLIERS
CREATE TABLE suppliers (
    id           BIGSERIAL PRIMARY KEY,
    barber_id    BIGINT       NOT NULL REFERENCES barber_profiles (id),
    name         VARCHAR(200) NOT NULL,
    contact_name VARCHAR(200),
    email        VARCHAR(255),
    phone        VARCHAR(20),
    address      TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- PRODUCT ORDERS
CREATE TABLE product_orders (
    id            BIGSERIAL PRIMARY KEY,
    barber_id     BIGINT        NOT NULL REFERENCES barber_profiles (id),
    supplier_id   BIGINT REFERENCES suppliers (id),
    order_date    TIMESTAMP     NOT NULL DEFAULT NOW(),
    expected_date DATE,
    received_date DATE,
    status        VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT', 'ORDERED', 'PARTIAL', 'RECEIVED', 'CANCELLED')),
    total_amount  DECIMAL(10, 2),
    notes         TEXT
);

-- PRODUCT ORDER LINES
CREATE TABLE product_order_lines (
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT        NOT NULL REFERENCES product_orders (id),
    product_id        BIGINT        NOT NULL REFERENCES products (id),
    quantity_ordered  INT           NOT NULL,
    quantity_received INT           NOT NULL DEFAULT 0,
    unit_cost         DECIMAL(10, 2) NOT NULL,
    total_cost        DECIMAL(10, 2) NOT NULL
);

-- REVIEWS
CREATE TABLE reviews (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT   NOT NULL UNIQUE REFERENCES appointments (id),
    barber_id      BIGINT   NOT NULL REFERENCES barber_profiles (id),
    client_id      BIGINT   NOT NULL REFERENCES users (id),
    rating         SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment        TEXT,
    is_visible     BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- REVIEW PHOTOS
CREATE TABLE review_photos (
    id          BIGSERIAL PRIMARY KEY,
    review_id   BIGINT      NOT NULL REFERENCES reviews (id),
    photo_url   VARCHAR(500) NOT NULL,
    photo_type  VARCHAR(10) NOT NULL CHECK (photo_type IN ('BEFORE', 'AFTER')),
    uploaded_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- NOTIFICATIONS
CREATE TABLE notifications (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT   NOT NULL REFERENCES users (id),
    appointment_id BIGINT REFERENCES appointments (id),
    channel        VARCHAR(20) NOT NULL CHECK (channel IN ('SMS', 'WHATSAPP', 'EMAIL', 'PUSH')),
    message        TEXT        NOT NULL,
    sent_at        TIMESTAMP,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- REFRESH TOKENS
CREATE TABLE refresh_tokens (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Default admin user (password: Admin@123)
INSERT INTO users (email, phone, password_hash, first_name, last_name, role, is_active, email_verified)
VALUES ('admin@barbershop.ma', '+212600000000',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQK0bz3y',
        'Admin', 'System', 'ADMIN', TRUE, TRUE);
