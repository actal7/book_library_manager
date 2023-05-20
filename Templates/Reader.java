package Templates;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reader {
    private UUID id;
    private String name;
    private String stateId;
    private List<UUID> borrowedBooks;
    private int bookReturnScore;
    private int maxBooks;
    private int maxBorrowPeriod;

    public Reader(UUID id, String name, String stateId, int maxBooks, int maxBorrowPeriod) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
        this.borrowedBooks = new ArrayList<>();
        this.bookReturnScore = 0;
        this.maxBooks = maxBooks;
        this.maxBorrowPeriod = maxBorrowPeriod;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStateId() {
        return stateId;
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
}