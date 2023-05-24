package Utils;

import Templates.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import org.postgresql.jdbc.PgArray;
import java.util.stream.Collectors;

public class BooksTable {
    private Connection connection;
    private ReadersTable readersTable;

    public BooksTable() {
        connection = DBOperations.getInstance().getConnection();
        readersTable = new ReadersTable();
    }

    private Book convertRowToBook(ResultSet resultSet) throws SQLException {
        UUID bookId = (UUID)resultSet.getObject("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        Timestamp year = (Timestamp)resultSet.getObject("date");
        int pagecount = resultSet.getInt("pagecount");
        boolean available = resultSet.getBoolean("available");
        PgArray pgArray = (PgArray) resultSet.getObject("sectionenums");
        Object sectionEnums = pgArray.getArray();
        List<String> sectionStringArr = Arrays.asList((String[]) sectionEnums);
        List<SectionEnum> sectionEnumList = sectionStringArr.stream().map(SectionEnum::valueOf).collect(Collectors.toList());
        Timestamp returnTimestamp = resultSet.getTimestamp("returndate");
        Date returnDate = returnTimestamp != null ? new Date(returnTimestamp.getTime()) : null;
        UUID borrowedBy = (UUID)resultSet.getObject("borrowedby");
        String isbn = resultSet.getString("isbn");

        return new Book(bookId, title, author, pagecount, new Date(year.getTime()), sectionEnumList, available, returnDate, borrowedBy, isbn);
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
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Book> getBooksBySection(SectionEnum section) throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books WHERE ? = ANY(sectionenums)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, section.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Book book = convertRowToBook(resultSet);
                books.add(book);
            }
        }

        return books;
    }

    public void updateBorrowedBy(UUID bookId, UUID borrowedBy) throws SQLException {
        String sql = "UPDATE books SET borrowedby = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, borrowedBy);
            preparedStatement.setObject(2, bookId);

            preparedStatement.executeUpdate();
        }
    }

    public void updateReturnDate(UUID bookId, Date returnDate) throws SQLException {
        String sql = "UPDATE books SET returndate = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, returnDate != null ? new Timestamp(returnDate.getTime()) : null);
            preparedStatement.setObject(2, bookId);

            preparedStatement.executeUpdate();
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

    public String getBookTitle(UUID bookId) throws SQLException {
        String sql = "SELECT title FROM books WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, bookId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getString("title");
        } else {
            return null;
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

    public void borrowBook(UUID bookId, UUID readerId) throws SQLException {
        // Check if the book is available first
        if(!isBookAvailable(bookId)) {
            System.out.println("Book is currently not available for borrowing");
            return;
        }

        // Set the return date
        java.util.Date utilDate = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        int maxBorrowPeriod = readersTable.searchReaders(readerId.toString()).get(0).getMaxBorrowPeriod();
        cal.add(Calendar.DATE, maxBorrowPeriod);
        utilDate = cal.getTime();

        Date returnDate = new Date(utilDate.getTime());
        Timestamp returnTimestamp = new Timestamp(returnDate.getTime());

        String sql = "UPDATE books SET available = false, borrowedby = ?, returndate = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, readerId);
            preparedStatement.setTimestamp(2, returnTimestamp);
            preparedStatement.setObject(3, bookId);

            preparedStatement.executeUpdate();
        }
    }

    public void updateBookAvailability(UUID bookId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isAvailable);
            preparedStatement.setObject(2, bookId);

            preparedStatement.executeUpdate();
        }
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

    public boolean isBookAvailable(UUID bookId) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isAvailable = false;

        try {
            String query = "SELECT available FROM Books WHERE id = ?";
            statement = connection.prepareStatement(query);

            statement.setObject(1, bookId);

            resultSet = statement.executeQuery();

            // If resultSet has at least one row, we can get availability
            if (resultSet.next()) {
                isAvailable = resultSet.getBoolean("available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        return isAvailable;
    }

}
