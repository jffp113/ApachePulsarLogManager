package IndexAggregator;

import IndexAggregator.entities.IndexerPrecision;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println(IndexerPrecision.convertPrecisionString("MINUTE"));
    }
}
