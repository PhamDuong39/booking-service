--liquibase formatted sql

--changeset booking:002-create-liquibase-demo
--comment: Learning example only; this is not a concert domain table.
CREATE TABLE booking.liquibase_demo (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE booking.liquibase_demo;
