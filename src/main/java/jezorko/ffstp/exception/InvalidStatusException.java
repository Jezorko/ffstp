package jezorko.ffstp.exception;

/**
 * Indicates that the given status contains invalid characters.
 */
public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String status) {
        super("status must not contain semicolons, given status '" + status + "' is not valid");
    }

}
