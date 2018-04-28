package jezorko.ffstp.exception;

import jezorko.ffstp.Message;

/**
 * Indicates that received message is too long than promised.
 * Data can be recovered for further examination using {@link #getReceivedMessage()}
 * and {@link #getAdditionalData()} methods.
 */
public final class MessageTooLongException extends RuntimeException {

    private final Message<String> receivedMessage;
    private final String additionalData;

    public MessageTooLongException(Message<String> receivedMessage, String additionalData) {
        super("Too much data received, expected " + receivedMessage.getData().length() +
              " but received " + additionalData.length() + " character(s)");
        this.receivedMessage = receivedMessage;
        this.additionalData = additionalData;
    }

    /**
     * @return message with expected length that has been received
     */
    public Message<String> getReceivedMessage() {
        return receivedMessage;
    }

    /**
     * @return additional, unexpected data left in the buffer
     */
    public String getAdditionalData() {
        return additionalData;
    }

}
