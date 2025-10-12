CREATE TABLE suggestion
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    title   VARCHAR(255)          NULL,
    content VARCHAR(255)          NULL,
    author  VARCHAR(255)          NULL,
    CONSTRAINT pk_suggestion PRIMARY KEY (id)
);