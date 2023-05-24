package CLI;

import Controller.ActionController;
import Templates.Book;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.UUID;

abstract class State {
    Scanner scanner = new Scanner(System.in);
    ActionController controller = ActionController.getInstance();
    abstract void handle() throws SQLException;
}

class MainState extends State {
    void handle() {
        System.out.println("Enter command: \n 0) Exit program \n 1) Readers Ops \n 2) Books Ops \n 3) Authors Ops \n 4) Sections Ops \n 5) Audit");
        String input = scanner.nextLine();

        switch (input) {
            case "0" -> {
                System.out.println("Exiting...");
                CLI.shouldExit = true;
            }
            case "1" -> {
                System.out.println("Switching to Readers Operations");
                CLI.setState(new ReadersOperationsState());
            }
            case "2" -> {
                System.out.println("Switching to Books Operations");
                CLI.setState(new BooksOperationsState());
            }
            case "3" -> {
                System.out.println("> AUTHOR OPERATIONS <");
                CLI.setState(new AuthorsOperationsState());
            }
            case "4" -> {
                System.out.println("> SECTIONS OPERATIONS <");
                CLI.setState(new SectionsOperationsState());
            }
            case "5" -> {
                System.out.println("> AUDIT OPERATIONS <");
                CLI.setState(new AuditOperationsState());
            }
            default -> {
            }
        }
    }
}

class ReadersOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to Main\n 1) Add reader\n 2) Delete reader\n 3) Search readers (name / id) \n 4) Print all readers");
        String input = scanner.nextLine();
        switch (input) {
            case "0" ->
                // Switch to MainState
                    CLI.setState(new MainState());
            case "1" -> {
                // Add reader
                controller.addReader(controller.addReaderWizard());
                controller.addAudit("Added reader", new Timestamp(System.currentTimeMillis()));
            }
            case "2" -> {
                // Delete reader
                System.out.println("Enter reader id to delete:");
                try {
                    controller.deleteReader(UUID.fromString(scanner.nextLine()));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID. Please try again.");
                }
                controller.addAudit("Attempted to delete reader", new Timestamp(System.currentTimeMillis()));
            }
            case "3" -> {
                // Print reader (by name/id)
                System.out.println("Enter reader name or id to print:");
                String readerQuery = scanner.nextLine();
//                System.out.println("Printing reader: " + readerQuery);
                controller.printReaders(controller.searchReaders(readerQuery));
                controller.addAudit("Searched reader: " + readerQuery, new Timestamp(System.currentTimeMillis()));
            }
            case "4" -> {
                // Print all readers
                controller.printReaders(controller.getAllReaders());
                controller.addAudit("Printed all readers", new Timestamp(System.currentTimeMillis()));
            }

            default -> System.out.println("Invalid command. Please try again.");
        }
    }
}

class BooksOperationsState extends State {
    void handle() throws SQLException {
        System.out.println("Enter command: \n 0) Return to Main \n 1) Add Book \n 2) Delete Book \n 3) Borrow Book \n 4) Return Book \n 5) Search Book \n 6) Show All Books");
        String input = scanner.nextLine();
        switch (input) {
            case "0" ->
                // Switch to MainState
                    CLI.setState(new MainState());
            case "1" -> {
                // Add book
                Book book = controller.addBookWizard();
                controller.addBook(book);
                controller.addAudit("Attempted to add book: " + book.getId(), new Timestamp(System.currentTimeMillis()));
            }
            case "2" -> {
                // Delete book
                System.out.println("Enter book id to delete:");
                String bookId = scanner.nextLine();
                System.out.println("Attempting to delete book: " + bookId);
                controller.deleteBook(bookId);
                controller.addAudit("Attempted to delete book: " + bookId, new Timestamp(System.currentTimeMillis()));
            }
            case "3" ->
                // Borrow book
                    controller.borrowBookWizard();
            case "4" -> {
                // Return book
                System.out.println("Enter book id to return:");
                String returnBookId = scanner.nextLine();
                controller.returnBook(returnBookId);
                controller.addAudit("Attempted to return book: " + returnBookId, new Timestamp(System.currentTimeMillis()));
            }
            case "5" -> {
                // Search book
                System.out.println("Enter book name or id to search:");
                String searchQuery = scanner.nextLine();
                System.out.println("Searching for book: " + searchQuery);
                controller.printBooks(controller.searchBooks(searchQuery));
                controller.addAudit("Searched book: " + searchQuery, new Timestamp(System.currentTimeMillis()));
            }
            case "6" -> {
                // Print books
                System.out.println("Printing all books:");
                controller.addAudit("Print all books", new Timestamp(System.currentTimeMillis()));
                controller.printBooks(controller.getAllBooks());
                controller.addAudit("Printed all books", new Timestamp(System.currentTimeMillis()));
            }
            // printAllBooks();
            default -> System.out.println("Invalid command. Please try again.");
        }
    }
}

class AuthorsOperationsState extends State {
    void handle() throws SQLException {
        System.out.println("Enter command:\n 0) Return to main\n 1) Add author\n 2) Delete author\n 3) Search authors (name / id) \n 4) Print all authors");
        String input = scanner.nextLine();
        switch (input) {
            case "0" ->
                // Switch to MainState
                    CLI.setState(new MainState());
            case "1" ->
                // Add author
               controller.addAuthor(controller.addAuthorWizard());
//               controller.addAudit("Attempted to add author", new Timestamp(System.currentTimeMillis()));
            case "2" -> {
                // Delete author
                System.out.println("Enter author id to delete:");
                String authorId = scanner.nextLine();
                controller.deleteAuthor(authorId);
                controller.addAudit("Attempted to delete author " + authorId, new Timestamp(System.currentTimeMillis()));
            }
            case "3" -> {
                // Print author (by id/name)
                System.out.println("Enter author id or name to search:");
                String authorId = scanner.nextLine();
                controller.printAuthors(controller.searchAuthors(authorId));
                controller.addAudit("Searched author " + authorId, new Timestamp(System.currentTimeMillis()));
            }
            case "4" -> {
                // Print all authors
                System.out.println("Printing all authors:");
                controller.printAuthors(controller.getAllAuthors());
                controller.addAudit("Printed all authors", new Timestamp(System.currentTimeMillis()));
            }
            default -> System.out.println("Invalid command. Please try again.");
        }
    }
}

class SectionsOperationsState extends State {
    void handle() throws SQLException {
        System.out.println("Enter command:\n 0) Return to Main\n 1) Print all sections\n 2) Print books in section (by name)");
        String input = scanner.nextLine();
        switch (input) {
            case "0" ->
                // Switch to MainState
                    CLI.setState(new MainState());
            case "1" -> {
                // Print all sections
                System.out.println("Printing all sections:");
                controller.printAllSections();
                controller.addAudit("Printed all book actions", new Timestamp(System.currentTimeMillis()));
            }
            case "2" -> {
                // Print books in given section
                System.out.println("Enter section:");
                String sectionId = scanner.nextLine();
                controller.printBooks(controller.getBooksBySection(sectionId));
                controller.addAudit("Printed books in section " + sectionId, new Timestamp(System.currentTimeMillis()));
            }
            default -> System.out.println("Invalid command. Please try again.");
        }
    }
}

class AuditOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to Main \n 1) Print last n actions");
        String input = scanner.nextLine();
        switch (input) {
            case "0" ->
                // Switch to MainState
                    CLI.setState(new MainState());
            case "1" -> {
                // Print last n actions
                System.out.println("Number of actions to display:");
                System.out.println("Printing last n actions:");
                int n = scanner.nextInt();
                controller.addAudit("Printed last " + n + " actions", new Timestamp(System.currentTimeMillis()));
                controller.printAudit(n);
            }
            default -> System.out.println("Invalid command. Please try again.");
        }
    }
}

public class CLI {
    public static boolean shouldExit = false;
    private static State state;

    public CLI() {
        state = new MainState();
    }

    public static void setState(State newState) {
        state = newState;
    }

    public void handleInput() {
        try {
            state.handle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
