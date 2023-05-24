package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionsTable {
    private Connection connection;

    public SectionsTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    public void getAllSections() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM sections");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int sectionId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                // Get other section attributes here
                System.out.println("Section ID: " + sectionId);
                System.out.println("Name: " + name);
                System.out.println("-------------------------");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Sections");
            e.printStackTrace();
        }
    }
}
