package jezorko.ffstp.exception;

/**
 * Indicates that the protocol reader could not be initialized properly.
 */
public final class ProtocolReaderInitializationException extends ProtocolInitializationException {
    public ProtocolReaderInitializationException(Throwable cause) {
        super("reader stream", cause);
    }
}
