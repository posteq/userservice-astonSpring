--liquibase formatted sql

--changeset posteq:1
INSERT INTO users (name,email,age,created_at)
VALUES ('Dima','dima@test.com',24,'2025-07-23'),
       ('Alex','alex@test.com',43,'2025-07-23'),
       ('Petr','petr@test.com',14,'2025-07-23'),
       ('Lida','lida@test.com',89,'2025-07-23'),
       ('Bob','bob@test.com',59,'2025-07-23');