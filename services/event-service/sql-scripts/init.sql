-- Extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- Base structure: timestamps
-- (BaseEntity zaten id/created_at/updated_at bekliyor)
-- =========================

-- ==============
-- VENUE
-- ==============
CREATE TABLE IF NOT EXISTS venue (
                                     id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     name        varchar(255) NOT NULL,
                                     city        varchar(100) NOT NULL,
                                     country     varchar(100) NOT NULL,
                                     address_line varchar(255),
                                     capacity    integer,
                                     timezone    varchar(50) NOT NULL,

                                     created_at  timestamptz NOT NULL DEFAULT now(),
                                     updated_at  timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_venue_city
    ON venue (city);

CREATE INDEX IF NOT EXISTS idx_venue_name
    ON venue (name);


-- ==============
-- EVENT
-- ==============
CREATE TABLE IF NOT EXISTS event (
                                     id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     slug         varchar(200) NOT NULL UNIQUE,
                                     title        varchar(255) NOT NULL,
                                     description  text,
                                     category     varchar(50) NOT NULL,
                                     organizer_id uuid NOT NULL,
                                     venue_id     uuid NOT NULL REFERENCES venue(id),

                                     status       varchar(50) NOT NULL,  -- DRAFT / PUBLISHED / CANCELLED / SOLD_OUT
                                     start_date   date NOT NULL,
                                     end_date     date NOT NULL,

                                     min_price    numeric(10,2),
                                     max_price    numeric(10,2),

                                     is_featured  boolean NOT NULL DEFAULT false,

                                     created_at   timestamptz NOT NULL DEFAULT now(),
                                     updated_at   timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_event_status_start_date
    ON event (status, start_date);

CREATE INDEX IF NOT EXISTS idx_event_venue_id
    ON event (venue_id);


-- ==============
-- EVENT_SESSION
-- ==============
CREATE TABLE IF NOT EXISTS event_session (
                                             id               uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                             event_id         uuid NOT NULL REFERENCES event(id) ON DELETE CASCADE,

                                             start_time       timestamptz NOT NULL,
                                             end_time         timestamptz NOT NULL,
                                             door_open_time   timestamptz,

                                             status           varchar(50) NOT NULL,  -- SCHEDULED / ON_SALE / SOLD_OUT / CANCELLED / COMPLETED

                                             sales_start_time timestamptz,
                                             sales_end_time   timestamptz,

                                             min_price        numeric(10,2),
                                             max_price        numeric(10,2),

                                             created_at       timestamptz NOT NULL DEFAULT now(),
                                             updated_at       timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_event_session_event_id_start_time
    ON event_session (event_id, start_time);

CREATE INDEX IF NOT EXISTS idx_event_session_status_start_time
    ON event_session (status, start_time);


-- ==============
-- TAG
-- ==============
CREATE TABLE IF NOT EXISTS tag (
                                   id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   code         varchar(100) NOT NULL UNIQUE,
                                   display_name varchar(100) NOT NULL,

                                   created_at   timestamptz NOT NULL DEFAULT now(),
                                   updated_at   timestamptz NOT NULL DEFAULT now()
);


-- ==============
-- EVENT_TAG (many-to-many)
-- ==============
CREATE TABLE IF NOT EXISTS event_tag (
                                         id         uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
                                         event_id   uuid NOT NULL REFERENCES event(id) ON DELETE CASCADE,
                                         tag_id     uuid NOT NULL REFERENCES tag(id)   ON DELETE CASCADE,

                                         created_at timestamptz NOT NULL DEFAULT now(),
                                         updated_at timestamptz NOT NULL DEFAULT now(),

                                         CONSTRAINT uk_event_tag_event_id_tag_id UNIQUE (event_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_event_tag_event_id
    ON event_tag (event_id);

CREATE INDEX IF NOT EXISTS idx_event_tag_tag_id
    ON event_tag (tag_id);
