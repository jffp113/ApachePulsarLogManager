package IndexAggregator;

import IndexAggregator.entities.IndexerPrecision;
import io.prometheus.client.Counter;

public class Metrics {
    /*
     * These counters are metrics to calculate the cache efficiency
     * */
    static final Counter minutes_cache_hit = Counter.build()
            .name("minutes_cache_hits_total")
            .help("Total cache hits in the minutes cache")
            .register();

    static final Counter hours_cache_hit = Counter.build()
            .name("hours_cache_hits_total")
            .help("Total cache hits in the hours cache")
            .register();

    static final Counter days_cache_hit = Counter.build()
            .name("days_cache_hits_total")
            .help("Total cache hits in the days cache")
            .register();


    static final Counter minutes_cache_misses = Counter.build()
            .name("minutes_cache_misses_total")
            .help("Total cache misses in the minutes cache")
            .register();

    static final Counter hours_cache_misses = Counter.build()
            .name("hours_cache_misses_total")
            .help("Total cache misses in the hours cache")
            .register();

    static final Counter days_cache_misses = Counter.build()
            .name("days_cache_misses_total")
            .help("Total cache misses in the days cache")
            .register();


    public static void add_cache_hit(IndexerPrecision precision){
        switch (precision){
            case DAY:
                days_cache_hit.inc();
                break;
            case HOUR:
                hours_cache_hit.inc();
                break;
            case MINUTE:
                minutes_cache_hit.inc();
                break;
        }
    }

    public static void add_cache_misses(IndexerPrecision precision){
        switch (precision){
            case DAY:
                days_cache_misses.inc();
                break;
            case HOUR:
                hours_cache_misses.inc();
                break;
            case MINUTE:
                minutes_cache_misses.inc();
                break;
        }
    }
}
