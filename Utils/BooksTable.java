package Utils;

import Templates.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import org.postgresql.jdbc.PgArray;
import java.util.stream.Collectors;

public class BooksTable {
    private Connection connection;

    public BooksTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    private Book convertRowToBook(ResultSet resultSet) throws SQLException {
        UUID bookId = (UUID)resultSet.getObject("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        Timestamp year = (Timestamp)resultSet.getObject("date");
        boolean available = resultSet.getBoolean("available");
        PgArray pgArray = (PgArray) resultSet.getObject("sectionenums");
        Object sectionEnums = pgArray.getArray();
        List<String> sectionStringArr = Arrays.asList((String[]) sectionEnums);
        List<SectionEnum> sectionEnumList = sectionStringArr.stream().map(SectionEnum::valueOf).collect(Collectors.toList());
        Date returnDate = (Date)resultSet.getObject("returndate");
        UUID borrowedBy = (UUID)resultSet.getObject("borrowedby");
        String isbn = resultSet.getString("isbn");

        return new Book(bookId, title, author, 0, new Date(year.getTime()), sectionEnumList, available, returnDate, borrowedBy, isbn);
    }

    public void addBook(Book book) throws SQLException {
        // Create a new book
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Get the database connection
            connection = DBOperations.getInstance().getConnection();

            // Prepare the SQL statement
            String query = "INSERT INTO books (id, title, author, pagecount, date, sectionenums, " +
                    "available, returndate, borrowedby, isbn) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            // Set the parameter values
            statement.setObject(1, book.getId());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setInt(4, book.getPageCount());
            statement.setTimestamp(5, new Timestamp(book.getDate().getTime()));
            statement.setArray(6, connection.createArrayOf("varchar", book.getSections().toArray()));
            statement.setBoolean(7, book.isAvailable());
            statement.setTimestamp(8, book.getReturnDate() != null ? new Timestamp(book.getReturnDate().getTime()) : null);
            statement.setObject(9, book.getBorrowedBy());
            statement.setString(10, book.getIsbn());

            // Execute the statement
            statement.executeUpdate();

            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        } finally {
            // Close the statement (the connection is managed by DBOperations)
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle resource cleanup failure
            }
        }
    }

    public void deleteBook(UUID bookId) throws SQLException {
        // Create a new book
        PreparedStatement statement = null;

        try {
            // Prepare the SQL statement
            String query = "DELETE FROM books WHERE id = ?";
            statement = connection.prepareStatement(query);

            // Set the parameter values
            statement.setObject(1, bookId);

            // Execute the statement
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("No book found with given ID!");
            } else {
                System.out.println("Book deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        } finally {
            // Close the statement
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle resource cleanup failure
            }
        }
    }

    public List<Book> searchBooks(String keyword) throws SQLException {

        List<Book> books = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            UUID uuid = UUID.fromString(keyword);
            statement = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
            statement.setObject(1, uuid);
            resultSet = statement.executeQuery();

        } catch (IllegalArgumentException e) {
            keyword = keyword.toLowerCase();
            statement = connection.prepareStatement("SELECT * FROM books WHERE title ILIKE ? OR author LIKE ?");
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            resultSet = statement.executeQuery();
        }

        while (resultSet.next()) {
            Book book = convertRowToBook(resultSet);
            books.add(book);
        }

        resultSet.close();
        statement.close();

        return books;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM books");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Book book = convertRowToBook(resultSet);
                books.add(book);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Books");
            e.printStackTrace();
        }

        return books;
    }
}
