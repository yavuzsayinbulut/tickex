-- (Opsiyonel) UUID üretimi DB tarafında lazım olacaksa aç
-- Senin tablolarında default uuid yok, yani app UUID basacaksa şart değil.
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USER MASTER
CREATE TABLE IF NOT EXISTS user_master (
                                           id            UUID         NOT NULL PRIMARY KEY,
                                           identity_hash VARCHAR(255),
    status        VARCHAR(50)  NOT NULL,
    user_type     VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version       BIGINT       NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_master_identity_hash
    ON user_master (identity_hash)
    WHERE identity_hash IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_user_master_status
    ON user_master (status);

CREATE INDEX IF NOT EXISTS idx_user_master_user_type
    ON user_master (user_type);

-- USER MAIL (email + password)
CREATE TABLE IF NOT EXISTS user_mail (
                                         id            UUID         NOT NULL PRIMARY KEY,
                                         user_id       UUID         NOT NULL,
                                         email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_primary    BOOLEAN      NOT NULL DEFAULT TRUE,
    is_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_mail_user
    FOREIGN KEY (user_id) REFERENCES user_master (id) ON DELETE CASCADE
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_mail_email
    ON user_mail (LOWER(email));

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_mail_user_email
    ON user_mail (user_id, LOWER(email));

CREATE INDEX IF NOT EXISTS idx_user_mail_primary
    ON user_mail (user_id)
    WHERE is_primary = TRUE;

-- USER PHONE
CREATE TABLE IF NOT EXISTS user_phone (
                                          id           UUID         NOT NULL PRIMARY KEY,
                                          user_id      UUID         NOT NULL,
                                          country_code VARCHAR(8),
    phone_number VARCHAR(32)  NOT NULL,
    is_primary   BOOLEAN      NOT NULL DEFAULT TRUE,
    is_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version      BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_phone_user
    FOREIGN KEY (user_id) REFERENCES user_master (id) ON DELETE CASCADE
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_phone_number
    ON user_phone (country_code, phone_number);

CREATE INDEX IF NOT EXISTS idx_user_phone_user
    ON user_phone (user_id);

-- USER LOGIN FAIL LOG
CREATE TABLE IF NOT EXISTS user_login_fail (
                                               id         UUID         NOT NULL PRIMARY KEY,
                                               user_id    UUID,
                                               email      VARCHAR(255),
    ip_address VARCHAR(64),
    user_agent VARCHAR(512),
    reason     VARCHAR(255),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version    BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_login_fail_user
    FOREIGN KEY (user_id) REFERENCES user_master (id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_user_login_fail_user
    ON user_login_fail (user_id);

CREATE INDEX IF NOT EXISTS idx_user_login_fail_email
    ON user_login_fail (LOWER(email));

-- RETAIL PROFILE
CREATE TABLE IF NOT EXISTS user_retail_profile (
                                                   id          UUID         NOT NULL PRIMARY KEY,
                                                   user_id     UUID         NOT NULL,
                                                   first_name  VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name   VARCHAR(100) NOT NULL,
    birth_date  DATE,
    nationality VARCHAR(3),
    gender      VARCHAR(20),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_retail_user
    FOREIGN KEY (user_id) REFERENCES user_master (id) ON DELETE CASCADE
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_retail_profile_user
    ON user_retail_profile (user_id);

-- USER ROLE
CREATE TABLE IF NOT EXISTS user_role (
                                         id         UUID         NOT NULL PRIMARY KEY,
                                         user_id    UUID         NOT NULL,
                                         role       VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    version    BIGINT       NOT NULL DEFAULT 0
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_role_user_role
    ON user_role (user_id, role);

CREATE INDEX IF NOT EXISTS idx_user_role_role
    ON user_role (role);
