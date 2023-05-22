package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBOperations {
    private static DBOperations instance;
    private Connection connection;

    private DBOperations() {
        // Private constructor to prevent instantiation from outside
        establishConnection();
    }

    public static DBOperations getInstance() {
        if (instance == null) {
            synchronized (DBOperations.class) {
                if (instance == null) {
                    instance = new DBOperations();
                }
            }
        }
        return instance;
    }

    private void establishConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/book_library_db", "actal", "java_oop_project");
        } catch (SQLException e) {
            System.out.println("Connection to library database failed");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
