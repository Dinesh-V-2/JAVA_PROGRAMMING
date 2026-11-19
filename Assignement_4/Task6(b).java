
import java.sql.*;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null)
            throw new SQLException(
                "Set DB_URL, DB_USER and DB_PASSWORD");

        return DriverManager.getConnection(
            url, user, password);
    }
}
