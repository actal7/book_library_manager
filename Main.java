import Utils.*;
import Templates.*;
import CLI.*;

import java.util.List;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.handleInput();

        while (!cli.shouldExit) {
            cli.handleInput();
        }

//        List<SectionEnum> sectionList = new ArrayList<>();
//
//        // Adding elements to the list
//        sectionList.add(SectionEnum.ART);
//        sectionList.add(SectionEnum.FANTASY);
//        sectionList.add(SectionEnum.MYSTERY);
//
//        BooksTable booksTable = new BooksTable();
//        booksTable.addBook("Test Book Name", "Test author name", 445, "1995-05-31", sectionList, "test-ISBN-555");
//        booksTable.getAllBooks();


    }
}
