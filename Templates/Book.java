package Templates;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Book {
    private UUID id;
    private String title;
    private String author;
    private int pageCount;
    private Date date;
    private List<SectionEnum> sectionEnums;
    private boolean available;
    private Date returnDate;
    private UUID borrowedBy;
    private String isbn;

    public Book(String title, String author, int pageCount, List<SectionEnum> sectionEnums) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.date = new Date();
        this.sectionEnums = sectionEnums;
        this.available = true;
        this.isbn = "";
    }

    public Book(UUID id, String title, String author, int pageCount, Date date, List<SectionEnum> sectionEnums,
                boolean available, Date returnDate, UUID borrowedBy, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.date = date;
        this.sectionEnums = sectionEnums;
        this.available = available;
        this.returnDate = returnDate;
        this.borrowedBy = borrowedBy;
        this.isbn = isbn;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SectionEnum> getSections() {
        return sectionEnums;
    }

    public void setSections(List<SectionEnum> sectionEnums) {
        this.sectionEnums = sectionEnums;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public UUID getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(UUID borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private int getPublicationYear() {
        // Extracting the year from the date field
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
    }

    @Override
    public String toString() {
        String availability = available ? "Yes" : "No";
        String sectionList = sectionEnums.toString();
        return title + ", " + author + ", " + getPublicationYear() + ", ISBN: " + isbn +
                ", Available: " + availability + ", Sections: " + sectionList + ", ID: " + id;
    }
}
