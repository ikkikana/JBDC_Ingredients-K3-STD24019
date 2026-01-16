package school.hei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    private static final String USER = "mini_dish_db_manager";
    private static final String PASSWORD = "1234";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { throw new RuntimeException(e); }
        }
    }
}
