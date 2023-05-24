package Utils;

import Templates.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

import org.postgresql.jdbc.PgArray;

public class ReadersTable {
    private Connection connection;

    public ReadersTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    private Reader convertRowToReader(ResultSet resultSet) throws SQLException {
        UUID readerId = (UUID)resultSet.getObject("id");
        String name = resultSet.getString("name");

        PgArray pgArrayBooks = (PgArray) resultSet.getObject("borrowed_books");
        UUID[] borrowedBooks = (UUID[]) pgArrayBooks.getArray();
        List<UUID> borrowedBooksUuidList = Arrays.asList(borrowedBooks);

        int bookReturnScore = resultSet.getInt("book_return_score");
        int maxBooks = resultSet.getInt("max_books");
        int maxBorrowPeriod = resultSet.getInt("max_borrow_period");

        return new Reader(readerId, name, borrowedBooksUuidList, bookReturnScore, maxBooks, maxBorrowPeriod);
    }

    public void addReader(Reader reader) throws SQLException {
        PreparedStatement statement = null;

        try {
            String query = "INSERT INTO Readers (id, name, borrowed_books, book_return_score, max_books, max_borrow_period) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            statement.setObject(1, reader.getId());
            statement.setString(2, reader.getName());
            statement.setArray(3, connection.createArrayOf("uuid", reader.getBorrowedBooks().toArray()));
            statement.setInt(4, reader.getBookReturnScore());
            statement.setInt(5, reader.getMaxBooks());
            statement.setInt(6, reader.getMaxBorrowPeriod());

            statement.executeUpdate();

            System.out.println("Reader added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
        }
    }

    public void deleteReader(UUID readerId) throws SQLException {
        PreparedStatement statement = null;

        try {
            String query = "DELETE FROM Readers WHERE id = ?";
            statement = connection.prepareStatement(query);

            statement.setObject(1, readerId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("No reader found with given ID!");
            } else {
                System.out.println("Reader deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
        }
    }

    public void updateBorrowedBooks(UUID readerId, UUID bookId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Fetch current borrowed books list
            String selectSql = "SELECT borrowed_books FROM Readers WHERE id = ?";
            statement = this.connection.prepareStatement(selectSql);
            statement.setObject(1, readerId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                PgArray pgArrayBooks = (PgArray) resultSet.getObject("borrowed_books");
                UUID[] borrowedBooksArray = (UUID[]) pgArrayBooks.getArray();
                List<UUID> borrowedBooks = new ArrayList<>(Arrays.asList(borrowedBooksArray));

                // Add new bookId to the list
                borrowedBooks.add(bookId);

                // Convert updated list back to array
                UUID[] updatedBorrowedBooks = borrowedBooks.toArray(new UUID[0]);

                // Update record in the database
                String updateSql = "UPDATE Readers SET borrowed_books = ? WHERE id = ?";
                statement = this.connection.prepareStatement(updateSql);
                statement.setArray(1, this.connection.createArrayOf("uuid", updatedBorrowedBooks));
                statement.setObject(2, readerId);
                statement.executeUpdate();

                System.out.println("Reader's borrowed books updated successfully!");
            } else {
                System.out.println("No reader found with given ID!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Reader> searchReaders(String keyword) throws SQLException {
        List<Reader> readers = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            UUID uuid = UUID.fromString(keyword);
            statement = connection.prepareStatement("SELECT * FROM Readers WHERE id = ?");
            statement.setObject(1, uuid);
            resultSet = statement.executeQuery();
        } catch (IllegalArgumentException e) {
            keyword = keyword.toLowerCase();
            statement = connection.prepareStatement("SELECT * FROM Readers WHERE name ILIKE ?");
            statement.setString(1, "%" + keyword + "%");
            resultSet = statement.executeQuery();
        }

        while (resultSet.next()) {
            Reader reader = convertRowToReader(resultSet);
            readers.add(reader);
        }

        resultSet.close();
        statement.close();

        return readers;
    }

    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Readers");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Reader reader = convertRowToReader(resultSet);
                readers.add(reader);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Readers");
            e.printStackTrace();
        }

        return readers;
    }

    public void updateReader(Reader reader) throws SQLException {
        String sql = "UPDATE readers SET name = ?, max_books = ?, max_borrow_period = ?, borrowed_books = string_to_array(?, ',')::uuid[] WHERE id = ?::uuid";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, reader.getName());
            pstmt.setInt(2, reader.getMaxBooks());
            pstmt.setInt(3, reader.getMaxBorrowPeriod());

            // Convert List<UUID> to comma separated String
            String borrowedBooksStr = reader.getBorrowedBooks().stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));
            pstmt.setString(4, borrowedBooksStr);

            pstmt.setString(5, reader.getId().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error executing updateReader");
            System.out.println(e.getMessage());
        }
    }


//    public void updateReader(Reader reader) throws SQLException {
//        String sql = "UPDATE readers SET name = ?, max_books = ?, max_borrow_period = ?, borrowed_books = ? WHERE id = ?::uuid";
//
//        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
//            pstmt.setString(1, reader.getName());
//            pstmt.setInt(2, reader.getMaxBooks());
//            pstmt.setInt(3, reader.getMaxBorrowPeriod());
//
//            // Convert List<UUID> to comma separated String
//            String borrowedBooksStr = reader.getBorrowedBooks().stream()
//                    .map(UUID::toString)
//                    .collect(Collectors.joining(","));
//            pstmt.setString(4, borrowedBooksStr);
//
//            pstmt.setString(5, reader.getId().toString());
//
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("Error executing updateReader");
//            System.out.println(e.getMessage());
//        }
//    }
}
