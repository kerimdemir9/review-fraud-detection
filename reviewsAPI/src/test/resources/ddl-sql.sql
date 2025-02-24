DROP
DATABASE IF EXISTS reviews;

CREATE
DATABASE reviews
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE
reviews;

DROP TABLE IF EXISTS reviews;

CREATE TABLE reviews
(
    id       INT  NOT NULL AUTO_INCREMENT primary key,
    review   TEXT not null,
    probability DOUBLE null,
    is_fraud BOOLEAN null
);
