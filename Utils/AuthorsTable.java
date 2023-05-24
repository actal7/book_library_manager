package Utils;

import Templates.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import Utils.*;

import org.postgresql.jdbc.PgArray;

public class AuthorsTable {
    private Connection connection;

    public AuthorsTable() {
        connection = DBOperations.getInstance().getConnection();
    }

    private Author convertRowToAuthor(ResultSet resultSet) throws SQLException {
        UUID authorId = (UUID)resultSet.getObject("id");
        String name = resultSet.getString("name");

        PgArray pgArray = (PgArray) resultSet.getObject("section_types");
        Object sectionTypes = pgArray.getArray();
        List<String> sectionStringArr = Arrays.asList((String[]) sectionTypes);
        List<SectionEnum> sectionEnumList = sectionStringArr.stream().map(SectionEnum::valueOf).collect(Collectors.toList());

        PgArray pgArrayBooks = (PgArray) resultSet.getObject("book_ids");
        UUID[] bookIds = (UUID[]) pgArrayBooks.getArray();
        List<UUID> bookUuidList = Arrays.asList(bookIds);

        return new Author(authorId, name, sectionEnumList, bookUuidList);
    }

    public void addAuthor(Author author) throws SQLException {
        // Create a new author
        PreparedStatement statement = null;

        try {
            // Prepare the SQL statement
            String query = "INSERT INTO authors (id, name, section_types, book_ids) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            // Set the parameter values
            statement.setObject(1, author.getId());
            statement.setString(2, author.getName());
            statement.setArray(3, connection.createArrayOf("varchar", author.getSectionTypes().toArray()));
            statement.setArray(4, connection.createArrayOf("uuid", author.getBookIds().toArray()));

            // Execute the statement
            statement.executeUpdate();

            System.out.println("Author added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the statement
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAuthor(UUID authorId) throws SQLException {
        // Delete an author
        PreparedStatement statement = null;

        try {
            // Prepare the SQL statement
            String query = "DELETE FROM authors WHERE id = ?";
            statement = connection.prepareStatement(query);

            // Set the parameter values
            statement.setObject(1, authorId);

            // Execute the statement
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("No author found with given ID!");
            } else {
                System.out.println("Author deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the statement
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Author> searchAuthors(String keyword) throws SQLException {
        List<Author> authors = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;

        try {
            UUID uuid = UUID.fromString(keyword);
            statement = connection.prepareStatement("SELECT * FROM authors WHERE id = ?");
            statement.setObject(1, uuid);
            resultSet = statement.executeQuery();
        } catch (IllegalArgumentException e) {
            keyword = keyword.toLowerCase();
            statement = connection.prepareStatement("SELECT * FROM authors WHERE name ILIKE ?");
            statement.setString(1, "%" + keyword + "%");
            resultSet = statement.executeQuery();
        }

        while (resultSet.next()) {
            Author author = convertRowToAuthor(resultSet);
            authors.add(author);
        }

        resultSet.close();
        statement.close();

        return authors;
    }


    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        System.out.println("Getting all authors");

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM authors");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Author author = convertRowToAuthor(resultSet);
                authors.add(author);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error executing query in Authors");
            e.printStackTrace();
        }

        return authors;
    }
}
