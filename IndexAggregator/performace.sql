SELECT *
FROM (SELECT sum(xv_count_metrictime) as xv_count
	 FROM xv_idx.dim_metric_237265_237261_mi
	 WHERE timest >= '2021-10-21 11:34:00') as xv,
	 (SELECT sum(xv_count_metrictime) as pulsar_count
	 FROM facts.xviewer_indexer_metrics_pulsar_mi as pulsar
	 WHERE timest >= '2021-10-21 11:34:00') as pulsar;

SELECT pulsar.timest, xv.xv_count_metrictime as xv_count, pulsar.xv_count_metrictime as pulsar_count
FROM
	( SELECT timest, sum(xv.xv_count_metrictime) as xv_count_metrictime
	FROM xv_idx.dim_metric_237265_237261_mi as xv
	WHERE timest > '2021-10-21 10:11:00'
	GROUP BY timest
	ORDER BY timest desc
	) as xv ,
	facts.xviewer_indexer_metrics_pulsar_mi as pulsar
WHERE xv.timest = pulsar.timest and xv.timest > '2021-10-21 10:11:00'
ORDER BY timest desc

--SELECT timest, count(xv.xv_count_metrictime)
--FROM xv_idx.dim_metric_237265_237261_mi as xv
--GROUP BY timest

