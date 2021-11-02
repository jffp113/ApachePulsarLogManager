CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE auth.credentials
(
    username text COLLATE pg_catalog."default" NOT NULL,
    password text COLLATE pg_catalog."default" NOT NULL,
    salt text COLLATE pg_catalog."default" NOT NULL,

    CONSTRAINT auth PRIMARY KEY (username)
)


    INSERT INTO auth.credentials (username, password, salt) VALUES ('jorge', 'KE+2kBJQqNqR1VGR4QpGAQ==', 'qAZO2/7BbsiKsNGd4NoRDw==');
INSERT INTO auth.credentials (username, password, salt) VALUES ('username', 'S6iWBw7hQw7zkB3VoHIDXg==', '1LeID0dwpFOlm4lLq0e76w==');