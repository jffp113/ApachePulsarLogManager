-- DROP TABLE facts.xviewer_indexer_metrics

CREATE TABLE facts.xviewer_indexer_metrics
(
    timest timestamp without time zone NOT NULL,
    metricType text COLLATE pg_catalog."default" NOT NULL,
    indexer text COLLATE pg_catalog."default" NOT NULL,
    metricTime numeric,

    CONSTRAINT indexer_metrics_pkey PRIMARY KEY (timest, metricType, indexer)
)