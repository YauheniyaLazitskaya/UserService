--liquibase formatted sql

--changeset program:001-create-table-users
--comment: Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

--rollback DROP TABLE IF EXISTS users;

--changeset program:002-create-table-payment-cards
--comment: Создание таблицы payment_cards и добавление внешнего ключа
CREATE TABLE IF NOT EXISTS payment_cards (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    number VARCHAR(16) NOT NULL UNIQUE,
    holder VARCHAR(60) NOT NULL,
    expiration_date DATE NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

--rollback DROP TABLE IF EXISTS payment_cards;


--changeset program:003-create-index-on-payment-cards-user-id
--comment: Создание индекса на колонку user_id для ускорения поиска карт по пользователю
CREATE INDEX IF NOT EXISTS idx_payment_cards_user_id ON payment_cards(user_id);

--rollback DROP INDEX IF EXISTS idx_payment_cards_user_id;