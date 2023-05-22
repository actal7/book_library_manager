package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadersTable {
    private Connection connection;

    public ReadersTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    public void getAllReaders() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM readers");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int readerId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                // Get other reader attributes here
                System.out.println("Reader ID: " + readerId);
                System.out.println("Name: " + name);
                System.out.println("-------------------------");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Readers");
            e.printStackTrace();
        }
    }

    // Implement other reader-related operations here
}
