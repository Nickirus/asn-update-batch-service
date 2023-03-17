--liquibase formatted sql

--changeset Nikitov:ASN_DATA#1
CREATE TABLE ASN_DATA
(
    RANGE_START    BIGINT
        CONSTRAINT ASN_DATA_PK
            PRIMARY KEY,
    RANGE_END      BIGINT,
    AS_NUMBER      BIGINT,
    COUNTRY_CODE   VARCHAR(20),
    AS_DESCRIPTION VARCHAR
);

-- rollback drop table ASN_DATA