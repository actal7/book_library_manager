package Templates;

import LibraryConstants.Constants;
import java.util.UUID;

public class RegularReader extends Reader {
    private static final int maxBooks = Constants.REGULAR_MAX_BOOKS;
    private static final int maxBorrowPeriod = Constants.REGULAR_PERIOD;

    public RegularReader(UUID id, String name, String stateId) {
        super(id, name, stateId, maxBooks, maxBorrowPeriod);
    }
}
