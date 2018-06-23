package jezorko.ffstp.exception;

/**
 * Indicates that there is not enough data in the buffer to process a message.
 * Data that has been received before error occurred can be recovered using {@link #getReceivedData()} method.
 * <b>Keep in mind that this is not guaranteed and data might not always be available.</b>
 */
public final class MissingDataException extends RuntimeException {

    private final byte[] receivedData;
    private final int receivedDataLength;

    public MissingDataException(int receivedDataLength) {
        this(receivedDataLength, null);
    }

    public MissingDataException(int receivedDataLength, byte[] receivedData) {
        super("not enough data in the buffer, retrieved " + receivedDataLength + " characters");
        this.receivedData = receivedData;
        this.receivedDataLength = receivedDataLength;
    }

    public MissingDataException(int bufferSize, int actualSize) {
        super("not enough data in the buffer, expected " + bufferSize + " but received " + actualSize + " character(s)");
        receivedData = null;
        receivedDataLength = actualSize;
    }

    /**
     * @return data that has been received or null if data could not be recovered
     */
    public byte[] getReceivedData() {
        return receivedData;
    }

    /**
     * @return the amount of bytes that has been received before this exception was thrown
     */
    public int getReceivedDataLength() {
        return receivedDataLength;
    }
}
