package jezorko.ffstp.exception;

/**
 * Indicates that the protocol writer could not be initialized properly.
 */
public final class ProtocolWriterInitializationException extends ProtocolInitializationException {
    public ProtocolWriterInitializationException(Throwable cause) {
        super("writer stream", cause);
    }
}
