-- DROP TABLE facts.xviewer_indexer_metrics_pulsar_mi

CREATE TABLE facts.xviewer_indexer_metrics_pulsar_mi
(
    timest timestamp without time zone NOT NULL,
    metricType text COLLATE pg_catalog."default" NOT NULL,
    avg_metrictime numeric,
    xv_count_metrictime numeric,

    CONSTRAINT indexer_metrics_pkey_mi PRIMARY KEY (timest, metricType)
);

CREATE TABLE facts.xviewer_indexer_metrics_pulsar_h
(
    timest timestamp without time zone NOT NULL,
    metricType text COLLATE pg_catalog."default" NOT NULL,
    avg_metrictime numeric,
    xv_count_metrictime numeric,

    CONSTRAINT indexer_metrics_pkey_h PRIMARY KEY (timest, metricType)
);

CREATE TABLE facts.xviewer_indexer_metrics_pulsar_d
(
    timest timestamp without time zone NOT NULL,
    metricType text COLLATE pg_catalog."default" NOT NULL,
    avg_metrictime numeric,
    xv_count_metrictime numeric,

    CONSTRAINT indexer_metrics_pkey_d PRIMARY KEY (timest, metricType)
)


--tmp database

CREATE TABLE facts.xviewer_indexer_metrics_pulsar_latency
(
    timest timestamp without time zone NOT NULL,
    avg_metrictime numeric,

    --CONSTRAINT indexer_metrics_pkey_latency PRIMARY KEY (timest)
)
