package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Connection {

    private static final String URL = "jdbc:postgresql://localhost:5432/book_library_db";
    private static final String USER = "actal";
    private static final String PASSWORD = "java_oop_project";

    public static Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection to library database failed");
            e.printStackTrace();
        }

        return conn;
    }
}
