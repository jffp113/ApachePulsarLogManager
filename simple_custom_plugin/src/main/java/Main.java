import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) throws Exception {
       Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/xviewer-r2", "xviewer", "xviewer");

       PreparedStatement st = conn.prepareStatement("SELECT * FROM auth.credentials WHERE username=?");
       st.setString(1,"jorge");
       ResultSet r = st.executeQuery();
       r.next();
       System.out.println(r.getString(1));
       System.out.println(r.getString(2));
       System.out.println(r.getString(3));
    }
}
