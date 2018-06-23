package jezorko.ffstp.exception;

/**
 * Indicates that the {@link jezorko.ffstp.Message} header is malformed.
 */
public final class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(byte invalidByte, int position) {
        super("message header is invalid, contains byte with value " + invalidByte +
              getMessagePartIfByteIsReadableAscii(invalidByte) + " on position " + position);
    }

    private static String getMessagePartIfByteIsReadableAscii(byte b) {
        return b > 32 && b < 127 ? " (" + (char) b + ")" : "";
    }
}
