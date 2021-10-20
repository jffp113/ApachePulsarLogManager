-- DROP TABLE facts.xviewer_indexer_metrics_pulsar_mi

CREATE TABLE facts.xviewer_indexer_metrics_pulsar_mi
(
    timest timestamp without time zone NOT NULL,
    metricType text COLLATE pg_catalog."default" NOT NULL,
    avg_metrictime numeric,
    xv_count_metrictime numeric,

    CONSTRAINT indexer_metrics_pkey_mi PRIMARY KEY (timest, metricType)
)