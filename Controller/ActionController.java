package Controller;

import Utils.*;
import Templates.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class ActionController {
    private static ActionController instance;
    private BooksTable booksTable;
    private AuditTable auditTable;

    private ActionController() {
        this.booksTable = new BooksTable();
        this.auditTable = new AuditTable();
    }

    public static synchronized ActionController getInstance() {
        if (instance == null) {
            instance = new ActionController();
        }
        return instance;
    }

    // Book Methods
    public void addBook(Book book) throws SQLException {
        this.booksTable.addBook(book);
    }

    public void deleteBook(String bookId) throws SQLException {
        UUID uuid = UUID.fromString(bookId);
        this.booksTable.deleteBook(uuid);
    }

    public Book addBookWizard() {
        Scanner scanner = new Scanner(System.in);
        Book book = null;

        System.out.println("Enter the book details:");

        try {
            // Get the book title from the user.
            System.out.println("Enter the title of the book:");
            String title = scanner.nextLine();

            // Get the book author from the user.
            System.out.println("Enter the author of the book:");
            String author = scanner.nextLine();

            // Get the page count from the user.
            System.out.println("Enter the page count of the book:");
            int pageCount = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character from the previous nextInt() call.

            // Get the date written from the user
            System.out.println("Enter the date the book was written (YYYY-MM-DD):");
            String dateWritten = scanner.nextLine();
//            String writtenDate = Date.valueOf(dateWritten);

            // Get the ISBN from the user
            System.out.println("Enter the ISBN of the book:");
            String isbn = scanner.nextLine();

            // Get the sections from the user.
            List<SectionEnum> sections = new ArrayList<>();
            System.out.println("Enter the sections of the book (end with DONE):");
            // Consume the newline character before entering the while loop.
            while (scanner.hasNextLine()) {
                String sectionInput = scanner.nextLine();
                if (sectionInput.equals("DONE")) break;
                try {
                    sections.add(SectionEnum.valueOf(sectionInput));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid section, try again:");
                }
            }

            // Use the collected information to create a new book.
            book = new Book(title, author, pageCount, dateWritten, sections, isbn);
            System.out.println("New book created: " + book);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please try again.");
        }

        return book;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = null;
        try {
            results = this.booksTable.searchBooks(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (results.size() == 0) {
            System.out.println("No results found.");
        }

        return results;
    }

    public List<Book> getAllBooks() {
        return this.booksTable.getAllBooks();
    }

    public void printBooks(List<Book> books) {
        for (Book book : books) {
            System.out.println("Book Title: " + book.getTitle() + "\n" + "Author: " + book.getAuthor() + "\n" + "Release Date: " + book.getDate() + "\n" + "Book ID: " + book.getId() + "\n" + "ISBN: " + book.getIsbn() + "\n" + "Sections: " + AdapterMethods.sectionEnumListToString(book.getSections()) + "\n" + "Available: " + book.isAvailable() + "\n");
       }
    }

    // Audit Methods
    public void addAudit(String action, Timestamp timestamp) {
        this.auditTable.insertLog(action, timestamp);
    }

    public void printAudit(int n) {
        this.auditTable.getNLogs(n);
    }
}
