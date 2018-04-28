package jezorko.ffstp.exception;

/**
 * Indicates that the {@link jezorko.ffstp.Message} header is malformed.
 */
public final class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(String actualHeader) {
        super("Message header is invalid: '" + actualHeader + "'");
    }
}
