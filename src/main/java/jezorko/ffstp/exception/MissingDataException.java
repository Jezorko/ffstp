package jezorko.ffstp.exception;

/**
 * Indicates that there is not enough data in the buffer to process a message.
 * Data that has been received before error occurred can be recovered using {@link #getReceivedData()} method.
 * <b>Keep in mind that this is not guaranteed and data might not always be available.</b>
 */
public final class MissingDataException extends RuntimeException {

    private final String receivedData;

    public MissingDataException(String receivedData) {
        super("Not enough data in the buffer, retrieved " + receivedData.length() + " characters");
        this.receivedData = receivedData;
    }

    public MissingDataException(int bufferSize, int actualSize) {
        super("Not enough data in the buffer, expected " + bufferSize + " but received " + actualSize + " character(s)");
        receivedData = null;
    }

    /**
     * @return data that has been received or null if data could not be recovered
     */
    public String getReceivedData() {
        return receivedData;
    }
}
