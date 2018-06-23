package jezorko.ffstp.exception;

/**
 * Indicates that a checked exception occurred but we didn't bother to catch it.
 */
public final class RethrownException extends RuntimeException {
    public RethrownException(Throwable throwable) {
        super("a checked exception was rethrown, see cause for more details", throwable);
    }
}
