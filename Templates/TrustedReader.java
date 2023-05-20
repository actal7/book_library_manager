package Templates;

import LibraryConstants.Constants;
import java.util.UUID;

public class TrustedReader extends Reader {
    private static final int maxBooks = Constants.TRUSTED_MAX_BOOKS;
    private static final int maxBorrowPeriod = Constants.TRUSTED_PERIOD;

    public TrustedReader(UUID id, String name, String stateId) {
        super(id, name, stateId, maxBooks, maxBorrowPeriod);
    }
}
