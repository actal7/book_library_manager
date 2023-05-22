package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorsTable {
    private Connection connection;

    public AuthorsTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    public void getAllAuthors() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM authors");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
//                int authorId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                // Get other author attributes here
//                System.out.println("Author ID: " + authorId);
                System.out.println("Name: " + name);
                System.out.println("-------------------------");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Authors");
            e.printStackTrace();
        }
    }

    // Implement other author-related operations here
}
