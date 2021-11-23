import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainScript {
    public static void main(String[] args) throws Exception {
        FileWriter myWriter = new FileWriter("counts.txt");

        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://192.168.0.252:5432/xviewer-r2",
                "xviewer", "xviewer");

        while(true){
            String sql = "SELECT count(*) FROM facts.xviewerlogs WHERE timest > '2021-11-23 13:00:07.00'";
            System.out.println("Executing query");
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            rs.next();
            System.out.println("Write ");
            myWriter.write(rs.getInt(1) + "\n");
            myWriter.flush();
            Thread.sleep(1000*60);
        }

        //2021-09-16 14:17:21,654 INFO  [org.jboss.weld.deployer] (MSC service thread 1-6) WFLYWELD0003:


    }
}
