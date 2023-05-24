package Templates;

import LibraryConstants.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reader {
    private UUID id;
    private String name;
    private List<UUID> borrowedBooks;
    private int bookReturnScore;
    private int maxBooks = Constants.REGULAR_MAX_BOOKS;
    private int maxBorrowPeriod = Constants.REGULAR_PERIOD;

    public Reader (String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
        this.bookReturnScore = 0;
    }

    public Reader(UUID id, String name, int maxBooks, int maxBorrowPeriod) {
        this.id = id;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
        this.bookReturnScore = 0;
        this.maxBooks = maxBooks;
        this.maxBorrowPeriod = maxBorrowPeriod;
    }

    public Reader(UUID readerId, String name, List<UUID> borrowedBooksUuidList, int bookReturnScore, int maxBooks, int maxBorrowPeriod) {
        this.id = readerId;
        this.name = name;
        this.borrowedBooks = borrowedBooksUuidList;
        this.bookReturnScore = bookReturnScore;
        this.maxBooks = maxBooks;
        this.maxBorrowPeriod = maxBorrowPeriod;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getBorrowedBooks() {
        return borrowedBooks;
    }

    public int getBookReturnScore() {
        return bookReturnScore;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public int getMaxBorrowPeriod() {
        return maxBorrowPeriod;
    }

    public void setBorrowedBooks(List<UUID> mutableBorrowedBooks) {
        this.borrowedBooks = new ArrayList<>(mutableBorrowedBooks);
    }
}