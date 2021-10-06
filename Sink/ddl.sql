-- DROP TABLE facts.xviewerlogs

CREATE TABLE facts.xviewerlogs
(
    timest timestamp without time zone NOT NULL,
    enviroment text COLLATE pg_catalog."default" NOT NULL,
    technology text COLLATE pg_catalog."default" NOT NULL,
    instance character varying COLLATE pg_catalog."default" NOT NULL,
    uuid text COLLATE pg_catalog."default" NOT NULL,

    filename text COLLATE pg_catalog."default" NOT NULL,
    seqNumber numeric,
    severity text COLLATE pg_catalog."default" NOT NULL,
    threadName text COLLATE pg_catalog."default" NOT NULL,
    category text COLLATE pg_catalog."default" NOT NULL,
    message text COLLATE pg_catalog."default" NOT NULL,
    rawMessage text COLLATE pg_catalog."default" NOT NULL,

    CONSTRAINT uptime_pkey PRIMARY KEY (timest, enviroment, technology, instance,uuid)
)