package jezorko.ffstp;

import jezorko.ffstp.exception.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import static java.lang.Integer.parseInt;
import static jezorko.ffstp.Constants.*;

/**
 * Handles incoming messages.
 *
 * @see FriendlyTemplate
 */
final class FriendlyForkedSocketTransferProtocolReader implements AutoCloseable {

    private final DataInputStream inputStream;

    /**
     * Takes ownership over the provided {@link BufferedReader}.
     *
     * @param inputStream to use for reading incoming messages
     */
    FriendlyForkedSocketTransferProtocolReader(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Convenient wrapper for {@link #readMessage()}.
     * Calls it and rethrows any checked exceptions wrapped in a {@link RethrownException}.
     * This method blocks until enough data is available in the buffer.
     *
     * @return a new message parsed from the reader
     */
    Message<byte[]> readMessageRethrowErrors() {
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
     * This method blocks until data is available in the buffer.
     *
     * @return a new message from the buffer
     */
    private Message<byte[]> readMessage() {
        readAndValidateHeader();

        final String status = readAsciiUntilDelimiter();
        final String dataBytesAmount = readAsciiUntilDelimiter();
        int dataBytesAmountAsInt;
        try {
            dataBytesAmountAsInt = parseInt(dataBytesAmount);
        } catch (NumberFormatException exception) {
            throw new InvalidMessageLengthException(dataBytesAmount, exception);
        }
        if (dataBytesAmountAsInt < 0) {
            throw new InvalidMessageLengthException(dataBytesAmountAsInt);
        }
        final byte[] messageBody = readDataToBuffer(dataBytesAmountAsInt);
        final Message<byte[]> message = new Message<>(status, messageBody, dataBytesAmountAsInt);
        final String shouldBeOnlyDelimiter = readAsciiUntilDelimiter();
        if (shouldBeOnlyDelimiter.length() != 0) {
            throw new MessageTooLongException(message, shouldBeOnlyDelimiter);
        }
        return message;
    }

    /**
     * Reads data until the {@link Constants#MESSAGE_DELIMITER} is encountered.
     * Data, without the delimiter, is validated against {@link Constants#PROTOCOL_HEADER}.
     *
     * @throws InvalidHeaderException if there are too many bytes or bytes don't match the header
     */
    private void readAndValidateHeader() {
        byte currentByte;
        int receivedDataLength = 0;

        do {
            try {
                currentByte = inputStream.readByte();

                if (currentByte == MESSAGE_DELIMITER) {
                    ++receivedDataLength;
                    break;
                }

                if (receivedDataLength == PROTOCOL_HEADER.length ||
                    currentByte != PROTOCOL_HEADER[receivedDataLength]) {
                    throw new InvalidHeaderException(currentByte, receivedDataLength + 1);
                }

                ++receivedDataLength;
            } catch (EOFException exception) {
                throw new MissingDataException(receivedDataLength);
            } catch (IOException exception) {
                throw new RethrownException(exception);
            }
        } while (true);
    }

    /**
     * Reads ASCII data from the buffer until a {@link Constants#MESSAGE_DELIMITER} is reached.
     *
     * @return data without the delimiter
     */
    private String readAsciiUntilDelimiter() {
        byte currentByte;
        StringBuilder resultBuilder = new StringBuilder();

        do {
            try {
                currentByte = inputStream.readByte();

                if (currentByte == MESSAGE_DELIMITER) {
                    break;
                }

                resultBuilder.append((char) currentByte);
            } catch (EOFException exception) {
                byte[] receivedBytes = resultBuilder.toString()
                                                    .getBytes(DEFAULT_CHARSET);
                throw new MissingDataException(receivedBytes.length, receivedBytes);
            } catch (IOException exception) {
                throw new RethrownException(exception);
            }
        } while (true);

        return resultBuilder.toString();
    }

    /**
     * Reads data to a new buffer with given size.
     *
     * @param bufferSize expected size of the buffer
     *
     * @return received data
     */
    private byte[] readDataToBuffer(int bufferSize) {
        byte[] buffer = new byte[bufferSize];
        if (bufferSize == 0) {
            return buffer;
        }

        int receivedDataLength = 0;

        do {
            try {
                buffer[receivedDataLength] = inputStream.readByte();
                ++receivedDataLength;
            } catch (EOFException exception) {
                throw new MissingDataException(receivedDataLength);
            } catch (IOException exception) {
                throw new RethrownException(exception);
            }
        } while (receivedDataLength < bufferSize);

        return buffer;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
