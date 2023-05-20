package Templates;

import LibraryConstants.Constants;
import java.util.UUID;

public class LateReader extends Reader {
    private static final int maxBooks = Constants.LATE_MAX_BOOKS;
    private static final int maxBorrowPeriod = Constants.LATE_PERIOD;

    public LateReader(UUID id, String name, String stateId) {
        super(id, name, stateId, maxBooks, maxBorrowPeriod);
    }
}
