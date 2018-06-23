package jezorko.ffstp.exception;

import static java.util.Objects.*;

/**
 * Indicates that the protocol could not be initialized properly.
 */
class ProtocolInitializationException extends RuntimeException {
    ProtocolInitializationException(String notInitializedPart, Throwable cause) {
        super("protocol " + notInitializedPart + " could not be initialized", requireNonNull(cause));
    }
}
