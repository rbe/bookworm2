package wbh.bookworm.hoerbuchkatalog.repository;

final class ImportFailedException extends Exception {

    ImportFailedException(final String message) {
        super(message);
    }

    ImportFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    ImportFailedException(final Throwable cause) {
        super(cause);
    }

}
