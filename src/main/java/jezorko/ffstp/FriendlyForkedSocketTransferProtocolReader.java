package jezorko.ffstp;

import jezorko.ffstp.exception.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

import static jezorko.ffstp.Constants.MESSAGE_DELIMITER;
import static jezorko.ffstp.Constants.PROTOCOL_HEADER;

/**
 * Handles incoming messages.
 *
 * @see FriendlyTemplate
 */
final class FriendlyForkedSocketTransferProtocolReader implements AutoCloseable {

    private final BufferedReader inputReader;

    /**
     * Takes ownership over the provided {@link BufferedReader}.
     *
     * @param inputReader to use for reading incoming messages
     */
    FriendlyForkedSocketTransferProtocolReader(BufferedReader inputReader) {
        this.inputReader = inputReader;
    }

    /**
     * Convenient wrapper for {@link #readMessage()}.
     * Calls it and rethrows any checked exceptions wrapped in a {@link RethrownException}.
     * This method blocks until enough data is available in the buffer.
     *
     * @return a new message parsed from the reader
     */
    Message<String> readMessageRethrowErrors() {
        try {
            return readMessage();
        } catch (RuntimeException uncheckedException) {
            throw uncheckedException;
        } catch (Exception checkedException) {
            throw new RethrownException(checkedException);
        }
    }

    /**
     * Reads messages from the buffer and parses them.
     * If the message is malformed, a variety of exceptions may be thrown:<br/>
     * <li> {@link InvalidHeaderException} if the header is not correct</li>
     * <li> {@link InvalidMessageLengthException} if message length is not a non-negative integer</li>
     * <li> {@link MissingDataException} if buffer was flushed with not enough data to parse the message</li>
     * <li> {@link MessageTooLongException} if buffer contained more data than promised</li>
     * <li> {@link IOException} if an IO error occurs (propagated from the {@link BufferedReader})</li>
     * This method blocks until data is available in the buffer.
     *
     * @return a new message from the buffer
     */
    private Message<String> readMessage() throws IOException {
        final String messageHeader = readDataToBuffer(4);
        if (!Objects.equals(messageHeader, PROTOCOL_HEADER + ";")) {
            throw new InvalidHeaderException(messageHeader);
        }

        final String status = readUntilDelimiter();
        final String dataBytesAmount = readUntilDelimiter();
        int dataBytesAmountAsInt;
        try {
            dataBytesAmountAsInt = Integer.parseInt(dataBytesAmount);
        } catch (NumberFormatException e) {
            throw new InvalidMessageLengthException(dataBytesAmount, e);
        }
        if (dataBytesAmountAsInt < 0) {
            throw new InvalidMessageLengthException(dataBytesAmountAsInt);
        }
        final String messageBody = readDataToBuffer(dataBytesAmountAsInt);
        final Message<String> message = new Message<>(status, messageBody);
        final String shouldBeOnlyDelimiter = readUntilDelimiter();
        if (shouldBeOnlyDelimiter.length() != 0) {
            throw new MessageTooLongException(message, shouldBeOnlyDelimiter);
        }
        return message;
    }

    /**
     * Reads data from the buffer until a {@link Constants#MESSAGE_DELIMITER} is reached.
     *
     * @return data without the delimiter
     */
    private String readUntilDelimiter() throws IOException {
        int currentCharacter;
        StringBuilder resultBuilder = new StringBuilder();
        while ((currentCharacter = inputReader.read()) != MESSAGE_DELIMITER) {
            if (currentCharacter == -1) {
                throw new MissingDataException(resultBuilder.toString());
            }
            resultBuilder.append((char) currentCharacter);
        }
        return resultBuilder.toString();
    }

    /**
     * Reads data to a new buffer with given size.
     *
     * @param bufferSize expected size of the buffer
     *
     * @return data wrapped in a {@link String}
     */
    private String readDataToBuffer(int bufferSize) throws IOException {
        char[] buffer = new char[bufferSize];
        final int actualSize = inputReader.read(buffer, 0, bufferSize);
        if (actualSize != bufferSize) {
            throw new MissingDataException(bufferSize, actualSize);
        }
        return new String(buffer);
    }

    @Override
    public void close() throws Exception {
        inputReader.close();
    }
}
