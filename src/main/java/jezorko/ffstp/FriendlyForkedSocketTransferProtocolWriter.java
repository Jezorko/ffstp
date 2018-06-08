package jezorko.ffstp;

import jezorko.ffstp.exception.InvalidStatusException;

import java.io.PrintWriter;

import static jezorko.ffstp.Constants.MESSAGE_DELIMITER;
import static jezorko.ffstp.Constants.PROTOCOL_HEADER;

/**
 * Handles outgoing messages.
 *
 * @see FriendlyTemplate
 */
final class FriendlyForkedSocketTransferProtocolWriter implements AutoCloseable {

    private final PrintWriter outputWriter;

    /**
     * Takes ownership over the provided {@link PrintWriter}.
     *
     * @param outputWriter to use for writing outgoing messages
     */
    FriendlyForkedSocketTransferProtocolWriter(PrintWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    /**
     * Writes given message to a buffer and flushes it.
     * Message will be parsed into the protocol format before being sent.
     *
     * @param message to be written
     *
     * @throws Exception of some sort, sometimes, probably
     */
    void writeMessage(Message<String> message) {
        final String dataToSend = message.getData() != null ? message.getData() : "";
        final int dataBytesAmount = dataToSend.length();

        final String statusToSend = message.getStatus() != null ? message.getStatus() : Status.UNKNOWN.name();

        if (statusToSend.contains(";")) {
            throw new InvalidStatusException(statusToSend);
        }

        final String messageToSend = PROTOCOL_HEADER + MESSAGE_DELIMITER +
                                     statusToSend + MESSAGE_DELIMITER +
                                     dataBytesAmount + MESSAGE_DELIMITER +
                                     dataToSend + MESSAGE_DELIMITER;

        outputWriter.print(messageToSend);
        outputWriter.flush();
    }

    @Override
    public void close() {
        outputWriter.close();
    }
}
