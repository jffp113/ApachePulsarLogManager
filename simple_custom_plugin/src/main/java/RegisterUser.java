import data.PasswordHash;
import util.SecurityProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterUser {

    public static final String XVIEWER_USERNAME = "xviewer";
    public static final String XVIEWER_DB_PASSWORD = "xviewer";

    public static void main(String[] args) throws Exception {
        String username = "username";
        String password = "password";

        PasswordHash p = SecurityProvider.generatePasswordHash(password);
        insertIntoDatabase(username,p.getPasswordHash(),p.getPasswordSalt());
    }

    private static void insertIntoDatabase(String username, String password, String salt) throws Exception {
        try(Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/xviewer-r2", XVIEWER_USERNAME, XVIEWER_DB_PASSWORD)){

            String sql = "INSERT INTO auth.credentials VALUES (?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setString(2, password);
            st.setString(3, salt);

            st.execute();
        }
    }
}
