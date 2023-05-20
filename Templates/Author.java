package Templates;

import java.util.List;
import java.util.UUID;

public class Author {
    private UUID id;
    private String name;
    private List<SectionEnum> sectionTypes;
    private List<UUID> bookIds;

    public Author(String name, List<SectionEnum> sectionTypes, List<UUID> bookIds) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.sectionTypes = sectionTypes;
        this.bookIds = bookIds;
    }

    public Author(UUID id, String name, List<SectionEnum> sectionTypes, List<UUID> bookIds) {
        this.id = id;
        this.name = name;
        this.sectionTypes = sectionTypes;
        this.bookIds = bookIds;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SectionEnum> getSectionTypes() {
        return sectionTypes;
    }

    public void setSectionTypes(List<SectionEnum> sectionTypes) {
        this.sectionTypes = sectionTypes;
    }

    public List<UUID> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<UUID> bookIds) {
        this.bookIds = bookIds;
    }
}
