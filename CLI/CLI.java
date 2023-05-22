package CLI;

import Controller.ActionController;
import Templates.Book;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.sql.Date;

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
            case "0":
                System.out.println("Exiting...");
                CLI.shouldExit = true;
                break;
            case "1":
                System.out.println("Switching to Readers Operations");
                CLI.setState(new ReadersOperationsState());
                break;
            case "2":
                System.out.println("Switching to Books Operations");
                CLI.setState(new BooksOperationsState());
                break;
            case "3":
                System.out.println("> AUTHOR OPERATIONS <");
                CLI.setState(new AuthorsOperationsState());
                break;
            case "4":
                System.out.println("> SECTIONS OPERATIONS <");
                CLI.setState(new SectionsOperationsState());
                break;
            case "5":
                System.out.println("> AUDIT OPERATIONS <");
                CLI.setState(new AuditOperationsState());
                break;
            default:
                // Handle invalid input?
                break;
        }
    }
}

class ReadersOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to Main\n 1) Add reader\n 2) Delete reader\n 3) Update reader status\n 4) Print all readers\n 5) Print reader (by name/id)");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new MainState());
                break;
            case "1":
                // Add reader
                System.out.println("Enter reader name:");
                String readerName = scanner.nextLine();
                System.out.println("Adding reader: " + readerName);
                break;
            case "2":
                // Delete reader
                System.out.println("Enter reader id to delete:");
                String readerId = scanner.nextLine();
                System.out.println("Deleting reader: " + readerId);
                break;
            case "3":
                // Update reader status
                System.out.println("Enter reader id to update status:");
                String updateReaderId = scanner.nextLine();
                System.out.println("Updating status for reader: " + updateReaderId);
                break;
            case "4":
                // Print all readers
                System.out.println("Printing all readers:");
                // printAllReaders();
                break;
            case "5":
                // Print reader (by name/id)
                System.out.println("Enter reader name or id to print:");
                String readerQuery = scanner.nextLine();
                System.out.println("Printing reader: " + readerQuery);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}

class PrintBooksState extends State {
    void handle() {
        System.out.println("Enter command: \n 0) Return to Books Operations \n 1) Print all books \n 2) Print book (by name/id)");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new BooksOperationsState());
                break;
            case "1":
                // Print all books
                System.out.println("Printing all books:");
                controller.addAudit("Print all books", new Timestamp(System.currentTimeMillis()));
                controller.printBooks(controller.getAllBooks());
                // printAllBooks();
                break;
            case "2":
                // Print book (by name/id)
                System.out.println("Enter book name or id to print:");
//                String bookQuery = scanner.nextLine(;
                // modify the line above to scan a whole line instead of a single word
                String bookQuery = scanner.nextLine();

                System.out.println("Printing book: " + bookQuery);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}
class BooksOperationsState extends State {
    void handle() throws SQLException {
        System.out.println("Enter command: \n 0) Return to Main \n 1) Add Book \n 2) Delete Book \n 3) Borrow Book \n 4) Return Book \n 5) Search Book \n 6) Show All Books");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new MainState());
                break;
            case "1":
                // Add book
                Book book = controller.addBookWizard();
                controller.addBook(book);
                break;
            case "2":
                // Delete book
                System.out.println("Enter book id to delete:");
                String bookId = scanner.nextLine();
                System.out.println("Attempting to delete book: " + bookId);
                controller.deleteBook(bookId);
                break;
            case "3":
                // Borrow book
                System.out.println("Enter book id to borrow:");
                String borrowBookId = scanner.nextLine();
                System.out.println("Borrowing book: " + borrowBookId);
                break;
            case "4":
                // Return book
                System.out.println("Enter book id to return:");
                String returnBookId = scanner.nextLine();
                System.out.println("Returning book: " + returnBookId);
                break;
            case "5":
                // Search book
                System.out.println("Enter book name or id to search:");
                String searchQuery = scanner.nextLine();
                System.out.println("Searching for book: " + searchQuery);
                controller.printBooks(controller.searchBooks(searchQuery));
                break;
            case "6":
                // Print books menu
                CLI.setState(new PrintBooksState());
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}


class AuthorsOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to main\n 1) Add author\n 2) Delete author\n 3) Print all authors\n 4) Print author (by id/name)");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new MainState());
                break;
            case "1":
                // Add author
                System.out.println("Enter author name:");
                String authorName = scanner.nextLine();
                System.out.println("Adding author: " + authorName);
                break;
            case "2":
                // Delete author
                System.out.println("Enter author id to delete:");
                String authorId = scanner.nextLine();
                System.out.println("Deleting author: " + authorId);
                break;
            case "3":
                // Print all authors
                System.out.println("Printing all authors:");
                // printAllAuthors();
                break;
            case "4":
                // Print author (by id/name)
                System.out.println("Enter author id or name to print:");
                String authorQuery = scanner.nextLine();
                System.out.println("Printing author: " + authorQuery);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}

class SectionsOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to Main\n 1) Add section\n 2) Delete section\n 3) Print all sections\n 4) Print section (by name)");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new MainState());
                break;
            case "1":
                // Add section
                System.out.println("Enter section name:");
                String sectionName = scanner.nextLine();
                System.out.println("Adding section: " + sectionName);
                break;
            case "2":
                // Delete section
                System.out.println("Enter section id to delete:");
                String sectionId = scanner.nextLine();
                System.out.println("Deleting section: " + sectionId);
                break;
            case "3":
                // Print all sections
                System.out.println("Printing all sections:");
                // printAllSections();
                break;
            case "4":
                // Print section (by name)
                System.out.println("Enter section name to print:");
                String sectionNameQuery = scanner.nextLine();
                System.out.println("Printing section: " + sectionNameQuery);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}

class AuditOperationsState extends State {
    void handle() {
        System.out.println("Enter command:\n 0) Return to Main \n 1) Print last n actions");
        String input = scanner.nextLine();
        switch (input) {
            case "0":
                // Switch to MainState
                CLI.setState(new MainState());
                break;
            case "1":
                // Print last n actions
                System.out.println("Number of actions to display:");
                System.out.println("Printing last n actions:");
                int n = scanner.nextInt();
                controller.addAudit("Printed last " + n + " actions", new Timestamp(System.currentTimeMillis()));
                controller.printAudit(n);
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
}

public class CLI {
    public static boolean shouldExit = false;
    private static State state;

    public CLI() {
        this.state = new MainState();
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
