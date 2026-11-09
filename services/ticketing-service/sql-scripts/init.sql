CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- TICKET TYPE (catalog)
-- =========================
CREATE TABLE IF NOT EXISTS ticket_type (
                                           id               uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id          uuid NOT NULL,
    event_session_id  uuid NOT NULL,

    code             varchar(50) NOT NULL,
    name             varchar(120) NOT NULL,
    description      text,

    currency         char(3) NOT NULL,
    base_price       numeric(19, 4) NOT NULL,
    service_fee      numeric(19, 4) NOT NULL DEFAULT 0,

    min_per_order    int NOT NULL DEFAULT 1,
    max_per_order    int NOT NULL DEFAULT 10,
    refundable       boolean NOT NULL DEFAULT false,
    status           varchar(30) NOT NULL, -- ACTIVE / INACTIVE

    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_ticket_type_session_code
    ON ticket_type (event_session_id, code);

CREATE INDEX IF NOT EXISTS idx_ticket_type_session
    ON ticket_type (event_session_id);

-- =========================
-- INVENTORY (atomic updates)
-- =========================
CREATE TABLE IF NOT EXISTS ticket_inventory (
                                                id               uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_type_id   uuid NOT NULL REFERENCES ticket_type(id) ON DELETE CASCADE,

    total_quantity   int  NOT NULL,
    sold_quantity    int  NOT NULL DEFAULT 0,
    reserved_quantity int NOT NULL DEFAULT 0,

    version          bigint NOT NULL DEFAULT 0,

    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT ck_inventory_non_negative
    CHECK (total_quantity >= 0 AND sold_quantity >= 0 AND reserved_quantity >= 0),
    CONSTRAINT ck_inventory_not_exceed
    CHECK (sold_quantity + reserved_quantity <= total_quantity)
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_ticket_inventory_type
    ON ticket_inventory (ticket_type_id);

-- =========================
-- ORDER (idempotent + expiry)
-- =========================
CREATE TABLE IF NOT EXISTS order_master (
                                            id                uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id            uuid NOT NULL,

    status            varchar(30) NOT NULL, -- CREATED / RESERVED / PAYMENT_PENDING / PAID / CANCELLED / EXPIRED
    currency          char(3) NOT NULL,

    subtotal_amount   numeric(19,4) NOT NULL DEFAULT 0,
    fee_amount        numeric(19,4) NOT NULL DEFAULT 0,
    discount_amount   numeric(19,4) NOT NULL DEFAULT 0,
    total_amount      numeric(19,4) NOT NULL DEFAULT 0,

    promo_code        varchar(50),
    idempotency_key   varchar(120),

    expires_at        timestamptz,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now(),
    version           bigint NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_order_idempotency_key
    ON order_master (idempotency_key)
    WHERE idempotency_key IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_order_user_status
    ON order_master (user_id, status);

CREATE INDEX IF NOT EXISTS idx_order_expires_at
    ON order_master (expires_at);

CREATE TABLE IF NOT EXISTS order_item (
                                          id               uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id         uuid NOT NULL REFERENCES order_master(id) ON DELETE CASCADE,

    ticket_type_id   uuid NOT NULL,
    event_id         uuid NOT NULL,
    event_session_id uuid NOT NULL,

    quantity         int NOT NULL,
    unit_price       numeric(19,4) NOT NULL,
    unit_fee         numeric(19,4) NOT NULL DEFAULT 0,
    line_discount    numeric(19,4) NOT NULL DEFAULT 0,
    line_total       numeric(19,4) NOT NULL,

    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT ck_order_item_qty CHECK (quantity > 0)
    );

CREATE INDEX IF NOT EXISTS idx_order_item_order
    ON order_item (order_id);

-- =========================
-- PAYMENT (provider attempts)
-- =========================
CREATE TABLE IF NOT EXISTS payment (
                                       id                  uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            uuid NOT NULL REFERENCES order_master(id) ON DELETE CASCADE,

    provider            varchar(50) NOT NULL, -- IYZICO / STRIPE / MOCK
    provider_payment_id varchar(120),
    status              varchar(30) NOT NULL, -- INITIATED / AUTHORIZED / CAPTURED / FAILED / REFUNDED
    amount              numeric(19,4) NOT NULL,
    currency            char(3) NOT NULL,

    raw_payload         jsonb NOT NULL DEFAULT '{}'::jsonb,

    created_at          timestamptz NOT NULL DEFAULT now(),
    updated_at          timestamptz NOT NULL DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_payment_provider_payment_id
    ON payment (provider, provider_payment_id)
    WHERE provider_payment_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_payment_order
    ON payment (order_id);

-- =========================
-- TICKET (issued units)
-- =========================
CREATE TABLE IF NOT EXISTS ticket (
                                      id               uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

    order_id         uuid NOT NULL REFERENCES order_master(id) ON DELETE CASCADE,
    order_item_id    uuid NOT NULL REFERENCES order_item(id) ON DELETE CASCADE,

    owner_user_id    uuid NOT NULL,
    event_id         uuid NOT NULL,
    event_session_id uuid NOT NULL,
    ticket_type_id   uuid NOT NULL,

    serial_no        varchar(80) NOT NULL UNIQUE,
    qr_token_hash    varchar(255) NOT NULL,

    status           varchar(30) NOT NULL, -- ISSUED / USED / CANCELLED / REFUNDED
    issued_at        timestamptz NOT NULL DEFAULT now(),
    used_at          timestamptz,

    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_ticket_owner
    ON ticket (owner_user_id);

CREATE INDEX IF NOT EXISTS idx_ticket_session
    ON ticket (event_session_id, status);

-- =========================
-- CHECK-IN
-- =========================
CREATE TABLE IF NOT EXISTS ticket_check_in (
                                               id              uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_id        uuid NOT NULL REFERENCES ticket(id) ON DELETE CASCADE,

    operator_user_id uuid,
    gate             varchar(80),
    device_id        varchar(120),

    status           varchar(30) NOT NULL, -- SUCCESS / REJECTED
    checked_in_at    timestamptz NOT NULL DEFAULT now(),

    created_at       timestamptz NOT NULL DEFAULT now(),
    updated_at       timestamptz NOT NULL DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_ticket_check_in_ticket
    ON ticket_check_in (ticket_id);
