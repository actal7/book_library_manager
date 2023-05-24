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
    private AuthorsTable authorsTable;

    private ReadersTable readersTable;

    private ActionController() {
        this.booksTable = new BooksTable();
        this.auditTable = new AuditTable();
        this.authorsTable = new AuthorsTable();
        this.readersTable = new ReadersTable();
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
        UUID uuid = null;
        try {
            uuid = UUID.fromString(bookId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
            return;
        }

        // Check if the book is available
        boolean isAvailable = this.booksTable.isBookAvailable(uuid);
        if (!isAvailable) {
            System.out.println("Can't delete the book as it is currently borrowed by a reader.");
            return;
        }

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

    public List<Author> searchAuthors(String keyword) {
        List<Author> results = null;
        try {
            results = this.authorsTable.searchAuthors(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (results == null || results.isEmpty()) {
            System.out.println("No results found.");
        }

        return results;
    }

//    public void borrowBook(UUID bookId, UUID readerId) {
//        try {
//            // Check if the book is available
//            Book book = this.booksTable.searchBooks(bookId.toString()).get(0);
//            if (book != null && book.isAvailable()) {
//                // Update book availability in database
//                this.booksTable.updateBookAvailability(bookId, false);
//
//                // Update reader's borrowed books in database
//                this.readersTable.updateBorrowedBooks(readerId, bookId);
//
//                // Log the action in audit table
//                this.auditTable.insertLog(String.format("Reader %s borrowed book %s", readerId, bookId), new Timestamp(System.currentTimeMillis()));
//
//                System.out.println(String.format("Book %s has been borrowexd by Reader %s", bookId, readerId));
//            } else {
//                System.out.println("Book is not available for borrowing.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }



    public void borrowBookWizard() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Book Borrowing Wizard! Let's begin...");

        System.out.print("Please enter your Reader ID: ");
        String readerIdInput = scanner.nextLine();
        String readerId = readerIdInput;

        System.out.print("Please enter the Book ID you want to borrow: ");
        String bookIdInput = scanner.nextLine();
        String bookId = bookIdInput;

        // check that the book and reader IDs can become UUIDs
        try {
            UUID.fromString(bookId);
            UUID.fromString(readerId);
        } catch (IllegalArgumentException e) {
            System.out.println("Input is not a valid UUID. Please try again.");
            return;
        }

        try {
            // Check if the reader and the book both exist
            Reader reader = readersTable.searchReaders(readerId).size() != 0 ? readersTable.searchReaders(readerId).get(0) : null;
            Book book = booksTable.searchBooks(bookId).size() != 0 ? booksTable.searchBooks(bookId).get(0) : null;

            if (reader == null) {
                System.out.println("The provided Reader ID does not exist.");
                return;
            }

            if (book == null) {
                System.out.println("The provided Book ID does not exist.");
                return;
            }

            // Check if the reader has not exceeded the maximum number of books
            if (reader.getBorrowedBooks().size() >= reader.getMaxBooks()) {
                System.out.println("Sorry, you have reached the maximum number of books you can borrow.");
                return;
            }

            // Check if the book is available
            if (booksTable.isBookAvailable(UUID.fromString(bookId))) {
                // Update book's availability
//                booksTable.updateBookAvailability(UUID.fromString(bookId), false);
                booksTable.borrowBook(UUID.fromString(bookId), UUID.fromString(readerId));
                // Update reader's borrowed books
                readersTable.updateBorrowedBooks(UUID.fromString(readerId), UUID.fromString(bookId));

                System.out.println("You successfully borrowed the book! Enjoy reading.");
            } else {
                System.out.println("Sorry, the book you requested is not available at this moment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(String bookId) throws SQLException {

        UUID uuid = null;

        // Check that UUID is valid
        try {
            uuid = UUID.fromString(bookId);
        } catch (IllegalArgumentException e) {
            System.out.println("Input is not a valid UUID. Please try again.");
            return;
        }

        // Check if the book is already available
        boolean isAvailable = this.booksTable.isBookAvailable(uuid);
        if (isAvailable) {
            System.out.println("This book is already available and doesn't need to be returned.");
            return;
        }

        // Fetch the borrower's UUID
        UUID borrowedBy = this.booksTable.searchBooks(uuid.toString()).get(0).getBorrowedBy();
        if (borrowedBy == null) {
            System.out.println("The book wasn't borrowed by anyone.");
            return;
        }

        // Get the reader who borrowed the book
        Reader reader = this.readersTable.searchReaders(borrowedBy.toString()).get(0);
        if (reader == null) {
            System.out.println("The reader doesn't exist in the system.");
            return;
        }

        // If not, update its availability, remove borrower, and clear return date
        this.booksTable.updateBookAvailability(uuid, true);  // Set book as available
        this.booksTable.updateBorrowedBy(uuid, null);  // Remove borrower
        this.booksTable.updateReturnDate(uuid, null);  // Clear return date

        // Remove book from reader's borrowed books
        UUID bookUuid = UUID.fromString(bookId);
        List<UUID> mutableBorrowedBooks = new ArrayList<>(reader.getBorrowedBooks());
        mutableBorrowedBooks.removeIf(bookUuid::equals);
        reader.setBorrowedBooks(mutableBorrowedBooks);

        this.readersTable.updateReader(reader);
        System.out.println("Book returned successfully!");
    }



    public List<Book> getAllBooks() {
        return this.booksTable.getAllBooks();
    }

    public void printBooks(List<Book> books) {
        for (Book book : books) {
            System.out.println("Book Title: " + book.getTitle() + "\n" +
                    "Author: " + book.getAuthor() + "\n" +
                    "Page Count: " + book.getPageCount() + "\n" +
                    "Release Date: " + book.getDate() + "\n" +
                    "Book ID: " + book.getId() + "\n" +
                    "ISBN: " + book.getIsbn() + "\n" +
                    "Sections: " + AdapterMethods.sectionEnumListToString(book.getSections()) + "\n" +
                    "Available: " + book.isAvailable());

            if (!book.isAvailable()) {
                System.out.println("Return Date: " + book.getReturnDate());
            }

            System.out.println(); // for empty line between books
        }
    }


    // Reader Methods
    public void addReader(Reader reader) {
        try {
            readersTable.addReader(reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReader(UUID readerId) {
        try {
            // Get the reader with the given readerId
            Reader reader = null;
            try {
                reader = readersTable.searchReaders(readerId.toString()).get(0);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("The provided Reader ID does not exist.");
                return;
            }
            if (reader == null) {
                System.out.println("The provided Reader ID does not exist.");
                return;
            }

            // Check if the reader has any borrowed books
            if (reader.getBorrowedBooks().size() > 0) {
                System.out.println("The reader with ID " + readerId + " currently has borrowed books and cannot be deleted.");
            } else {
                readersTable.deleteReader(readerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reader> searchReaders(String keyword) {
        List<Reader> results = null;
        try {
            results = readersTable.searchReaders(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (results.size() == 0) {
            System.out.println("No results found.");
        }

        return results;
    }

    public List<Reader> getAllReaders() {
        return readersTable.getAllReaders();
    }

    public void printReaders(List<Reader> readers) {
        System.out.println("Readers: " + readers.size() + "\n");
        for (Reader reader : readers) {
            List<String> bookTitles = new ArrayList<>();
            for (UUID bookId : reader.getBorrowedBooks()) {
                try {
                    String bookTitle = booksTable.getBookTitle(bookId);
                    if (bookTitle != null) {
                        bookTitles.add(bookTitle);
                    } else {
                        bookTitles.add("Book not found");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Reader ID: " + reader.getId() + "\n" +
                    "Reader Name: " + reader.getName() + "\n" +
                    "Borrowed Books IDs: " + reader.getBorrowedBooks() + "\n" +
                    "Borrowed Books Titles: " + bookTitles + "\n" +
                    "Book Return Score: " + reader.getBookReturnScore() + "\n" +
                    "Max Books: " + reader.getMaxBooks() + "\n" +
                    "Max Borrow Period: " + reader.getMaxBorrowPeriod() + "\n");
        }
    }

    public Reader addReaderWizard() {
        Scanner scanner = new Scanner(System.in);
        Reader reader = null;

        System.out.println("Enter the reader details:");

        try {
            // Get the reader name from the user.
            System.out.println("Enter the name of the reader:");
            String name = scanner.nextLine();

            // Use the collected information to create a new reader.
            reader = new Reader(name);
            System.out.println("New reader created: " + reader.getName());

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please try again.");
        }

        return reader;
    }

    // Author Methods

    public void addAuthor(Author author) throws SQLException {
        this.authorsTable.addAuthor(author);
    }

    public void deleteAuthor(String authorId) throws SQLException {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(authorId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID. Please try again.");
        }
        this.authorsTable.deleteAuthor(uuid);
    }

    public List<Author> getAllAuthors() {
        return this.authorsTable.getAllAuthors();
    }

    public void printAuthors(List<Author> authors) {
        for (Author author : authors) {
            List<String> bookTitles = new ArrayList<>();
            for (UUID bookId : author.getBookIds()) {
                try {
                    String bookTitle = booksTable.getBookTitle(bookId);
                    if (bookTitle != null) {
                        bookTitles.add(bookTitle);
                    } else {
                        bookTitles.add("Book not found");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Author ID: " + author.getId() + "\n" +
                    "Author Name: " + author.getName() + "\n" +
                    "Sections: " + AdapterMethods.sectionEnumListToString(author.getSectionTypes()) + "\n" +
                    "Book IDs: " + author.getBookIds() + "\n" +
                    "Book Titles: " + String.join(", ", bookTitles) + "\n");
        }
    }

    public Author addAuthorWizard() {
        Scanner scanner = new Scanner(System.in);
        Author author = null;

        System.out.println("Enter the author details:");

        try {
            // Get the author name from the user.
            System.out.println("Enter the name of the author:");
            String name = scanner.nextLine();

            // Get the section types from the user.
            List<SectionEnum> sectionTypes = new ArrayList<>();
            System.out.println("Enter the section types of the author (end with DONE):");
            while (scanner.hasNextLine()) {
                String sectionInput = scanner.nextLine();
                if (sectionInput.equals("DONE")) break;
                try {
                    sectionTypes.add(SectionEnum.valueOf(sectionInput));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid section, try again:");
                }
            }

            // Get the book IDs from the user.
            List<UUID> bookIds = new ArrayList<>();
            System.out.println("Enter the book IDs of the author (end with DONE):");
            while (scanner.hasNextLine()) {
                String bookIdInput = scanner.nextLine();
                if (bookIdInput.equals("DONE")) break;
                try {
                    bookIds.add(UUID.fromString(bookIdInput));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid book ID, try again:");
                }
            }

            // Use the collected information to create a new author.
            author = new Author(name, sectionTypes, bookIds);
            System.out.println("New author created: " + author);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please try again.");
        }

        return author;
    }

    // Section Methods
    public void printAllSections() {
        SectionEnum[] sections = SectionEnum.values();

        System.out.println("Currently existing sections:");
        for (SectionEnum section : sections) {
            System.out.println(section);
        }
    }

    public List<Book> getBooksBySection(String section) throws SQLException {
        SectionEnum sectionEnum = null;
        List<Book> books = null;
        try {
          sectionEnum = SectionEnum.valueOf(section);
        } catch (IllegalArgumentException e) {
          System.out.println("Invalid section. Please try again.");
        }

        books = booksTable.getBooksBySection(sectionEnum);
        return books;
    }

    // Audit Methods
    public void addAudit(String action, Timestamp timestamp) {
        this.auditTable.insertLog(action, timestamp);
    }

    public void printAudit(int n) {
        this.auditTable.getNLogs(n);
    }
}
