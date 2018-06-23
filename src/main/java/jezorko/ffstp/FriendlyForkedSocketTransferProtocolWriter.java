package jezorko.ffstp;

import jezorko.ffstp.exception.InvalidStatusException;
import jezorko.ffstp.exception.RethrownException;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.nio.charset.CharsetEncoder;

import static jezorko.ffstp.Constants.*;
import static jezorko.ffstp.Status.UNKNOWN;

/**
 * Handles outgoing messages.
 *
 * @see FriendlyTemplate
 */
final class FriendlyForkedSocketTransferProtocolWriter implements AutoCloseable {

    private final static byte[] EMPTY_DATA = new byte[0];
    private final static CharsetEncoder ASCII_ENCODER = DEFAULT_CHARSET.newEncoder();

    private final DataOutputStream outputStream;

    /**
     * Takes ownership over the provided {@link PrintWriter}.
     *
     * @param outputStream to use for writing outgoing messages
     */
    FriendlyForkedSocketTransferProtocolWriter(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Writes given message to a buffer and flushes it.
     * Message will be parsed into the protocol format before being sent.
     *
     * @param message to be written
     *
     * @throws Exception of some sort, sometimes, probably
     */
    void writeMessage(Message<byte[]> message) {
        final byte[] dataToSend = message.getData() != null ? message.getData() : EMPTY_DATA;
        final String dataBytesAmountAsString = String.valueOf(dataToSend.length);

        final String statusToSend = message.getStatus() != null ? message.getStatus() : UNKNOWN.name();

        if (statusToSend.contains(";") || !ASCII_ENCODER.canEncode(statusToSend)) {
            throw new InvalidStatusException(statusToSend);
        }

        try {
            outputStream.write(PROTOCOL_HEADER);
            outputStream.writeByte(MESSAGE_DELIMITER);

            outputStream.write(statusToSend.getBytes(DEFAULT_CHARSET));
            outputStream.writeByte(MESSAGE_DELIMITER);

            outputStream.write(dataBytesAmountAsString.getBytes(DEFAULT_CHARSET));
            outputStream.writeByte(MESSAGE_DELIMITER);

            outputStream.write(dataToSend);
            outputStream.writeByte(MESSAGE_DELIMITER);

            outputStream.flush();
        } catch (Exception e) {
            throw new RethrownException(e);
        }
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
