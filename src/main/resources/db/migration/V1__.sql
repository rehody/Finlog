CREATE TABLE transaction
(
    id               UUID                                           NOT NULL,
    user_id          UUID                                           NOT NULL,
    amount           DECIMAL                                        NOT NULL,
    description      TEXT                                           NOT NULL,
    category         VARCHAR(255)                                   NOT NULL,
    transaction_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()      NOT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

CREATE TABLE user_
(
    id                UUID                                          NOT NULL,
    email             VARCHAR(255)                                  NOT NULL,
    name              VARCHAR(255)                                  NOT NULL,
    password_hash     VARCHAR(255)                                  NOT NULL,
    registration_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()     NOT NULL,
    soft_delete       BOOLEAN                                       NOT NULL,
    CONSTRAINT pk_user_ PRIMARY KEY (id)
);

ALTER TABLE user_
    ADD CONSTRAINT uc_user__email UNIQUE (email);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_USER FOREIGN KEY (user_id) REFERENCES user_ (id);