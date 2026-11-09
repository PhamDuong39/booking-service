--liquibase formatted sql

--changeset booking:001-create-booking-schema
CREATE SCHEMA booking;

--rollback DROP SCHEMA booking;
