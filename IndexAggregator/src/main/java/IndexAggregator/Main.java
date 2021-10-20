package IndexAggregator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/xviewer-r2", "xviewer", "xviewer");
        PreparedStatement st = conn.prepareStatement(" SELECT * from facts.xviewerlogs where timest = ?");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
        LocalDateTime t = LocalDateTime.parse("2021-10-08 11:07:59,167",formatter);
        st.setTimestamp(1, Timestamp.valueOf(t));
        ResultSet r = st.executeQuery();
        r.next();
        System.out.println(r.getTimestamp(1).toString().replace('.',','));
    }
}
