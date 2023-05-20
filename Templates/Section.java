package Templates;

import java.util.List;
import java.util.UUID;

import java.util.List;
import java.util.UUID;

public class Section {
    private UUID id;
    private SectionEnum sectionType;
    private List<UUID> bookIds;
    private List<UUID> authorIds;

    public Section(SectionEnum sectionType) {
        this.id = UUID.randomUUID();
        this.sectionType = sectionType;
    }

    public Section(UUID id, SectionEnum sectionType, List<UUID> bookIds, List<UUID> authorIds) {
        this.id = id;
        this.sectionType = sectionType;
        this.bookIds = bookIds;
        this.authorIds = authorIds;
    }

    public UUID getId() {
        return id;
    }

    public SectionEnum getSectionType() {
        return sectionType;
    }

    public void setSectionType(SectionEnum sectionType) {
        this.sectionType = sectionType;
    }

    public List<UUID> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<UUID> bookIds) {
        this.bookIds = bookIds;
    }

    public List<UUID> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<UUID> authorIds) {
        this.authorIds = authorIds;
    }
}
