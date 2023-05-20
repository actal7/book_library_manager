import LibraryConstants.Constants;
import Templates.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        //Book(String title, String author, int pageCount, List<Section> sections)
        System.out.println(Constants.BANNED_LATE_PERIOD);
        Book test = new Book("test title", "test author", 321, Arrays.asList(SectionEnum.ART, SectionEnum.FANTASY));
        System.out.println(test);

//
//        Connection connection = DB_Connection.getConnection();
//        if (connection != null) {
//            System.out.println("Connection established successfully.");
//            try {
//                Statement statement = connection.createStatement();
//                ResultSet resultSet = statement.executeQuery("SELECT * FROM log"); // replace with your query
//
//                while (resultSet.next()) {
//                    // Process the result set here
//                    // For example, if your table has a column 'name' you can print it as follows:
//                    System.out.println(resultSet.getString("action_name"));
//                }
//
//                resultSet.close();
//                statement.close();
//            } catch (SQLException e) {
//                System.out.println("Failed to create statement");
//                e.printStackTrace();
//            } finally {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    System.out.println("Failed to close connection");
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            System.out.println("Failed to establish connection.");
//        }
    }
}
