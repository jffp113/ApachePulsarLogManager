package IndexAggregator.entities;

import javax.validation.constraints.Min;

public enum IndexerPrecision {
    DAY,HOUR,MINUTE;


    public static IndexerPrecision convertPrecisionString(String precision){
        return IndexerPrecision.valueOf(precision.toUpperCase());
    }

}
