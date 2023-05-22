package Utils;

import java.sql.*;


public class AuditTable {
    private Connection connection;

    public AuditTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    public void insertLog(String logAction, Timestamp datetime ) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO log (action_name, datetime) VALUES (?, ?)");
            statement.setString(1, logAction);
            statement.setTimestamp(2, datetime);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Audit");
            e.printStackTrace();
        }
    }

    public void getNLogs(int n) {
        if  (n < 0) {
            System.out.println("Invalid number of logs");
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM log ORDER BY datetime DESC LIMIT ?");
            statement.setInt(1, n);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Action: " + resultSet.getString("action_name") + " Date: " + resultSet.getTimestamp("datetime"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Audit");
            e.printStackTrace();
        }
    }
}
