package jezorko.ffstp.exception;

/**
 * Indicates that the given status contains invalid characters.
 */
public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String status) {
        super("status must not contain semicolons or non-ascii characters, given status '" + status + "' is not valid");
    }

}
