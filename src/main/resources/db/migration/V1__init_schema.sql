CREATE TABLE leads
(
    id           BIGSERIAL PRIMARY KEY,
    chat_id      BIGINT        NOT NULL,
    source       VARCHAR(64)   NOT NULL,
    request_type VARCHAR(64)   NOT NULL,
    name         VARCHAR(255)  NOT NULL,
    phone        VARCHAR(255)  NOT NULL,
    description  VARCHAR(2000) NOT NULL,
    created_at   TIMESTAMP     NOT NULL
);

CREATE TABLE user_sessions
(
    id                 BIGSERIAL PRIMARY KEY,
    chat_id            BIGINT      NOT NULL UNIQUE,
    source             VARCHAR(64) NOT NULL,
    step               VARCHAR(64) NOT NULL,
    draft_request_type VARCHAR(64),
    draft_name         VARCHAR(255),
    draft_phone        VARCHAR(255),
    draft_description VARCHAR(2000),
    updated_at         TIMESTAMP   NOT NULL
);

CREATE TABLE bot_events
(
    id         BIGSERIAL PRIMARY KEY,
    chat_id    BIGINT      NOT NULL,
    source     VARCHAR(64) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL
);

CREATE INDEX idx_bot_events_created_at ON bot_events (created_at);
CREATE INDEX idx_bot_events_event_type ON bot_events (event_type);
CREATE INDEX idx_bot_events_chat_id ON bot_events (chat_id);