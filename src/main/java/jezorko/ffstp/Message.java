package jezorko.ffstp;

import java.util.Objects;

import static jezorko.ffstp.Constants.MAX_DATA_LENGTH_REASONABLY_PRINTABLE;
import static jezorko.ffstp.Status.*;

/**
 * A representation of a single FFSTP message.
 * Consists of a payload (data) and a status.
 *
 * @param <T> type of the payload
 *
 * @see Status
 */
public class Message<T> {

    private final static int UNKNOWN_MESSAGE_DATA_BYTES_LENGTH = -1;

    /**
     * Returns a message instance that has both status and payload set to null.
     */
    @SuppressWarnings("unchecked")
    public final static Message EMPTY = new Message((String) null, null, UNKNOWN_MESSAGE_DATA_BYTES_LENGTH);

    private final String status;
    private final T data;
    private final int dataBytesLength;

    /**
     * Returns a message instance that has both status and payload set to null.
     *
     * @param <T> the type of expected message
     *
     * @return static instance of message
     */
    @SuppressWarnings("unchecked")
    public static <T> Message<T> empty() {
        return EMPTY;
    }

    /**
     * Convenient method for sending a message with a {@link Status#OK} status
     */
    public static <T> Message<T> ok() {
        return new Message<>(OK, null);
    }

    /**
     * Convenient method for sending a message with a {@link Status#OK} status
     */
    public static <T> Message<T> ok(T data) {
        return new Message<>(OK, data);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR} status
     */
    public static <T> Message<T> error() {
        return new Message<>(ERROR, null);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR} status
     */
    public static <T> Message<T> error(T data) {
        return new Message<>(ERROR, data);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR_INVALID_STATUS} status
     */
    public static <T> Message<T> errorInvalidStatus() {
        return new Message<>(ERROR_INVALID_STATUS, null);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR_INVALID_STATUS} status
     */
    public static <T> Message<T> errorInvalidStatus(T data) {
        return new Message<>(ERROR_INVALID_STATUS, data);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR_INVALID_PAYLOAD} status
     */
    public static <T> Message<T> errorInvalidPayload() {
        return new Message<>(ERROR_INVALID_PAYLOAD, null);
    }

    /**
     * Convenient method for sending a message with a {@link Status#ERROR_INVALID_PAYLOAD} status
     */
    public static <T> Message<T> errorInvalidPayload(T data) {
        return new Message<>(ERROR_INVALID_PAYLOAD, data);
    }

    /**
     * Convenient method for sending a message with a {@link Status#DIE} status
     */
    public static <T> Message<T> die() {
        return new Message<>(DIE, null);
    }

    /**
     * Convenient method for sending a message with a {@link Status#DIE} status
     */
    public static <T> Message<T> die(T data) {
        return new Message<>(DIE, data);
    }

    public Message(Status status, T data) {
        this(status.name(), data, UNKNOWN_MESSAGE_DATA_BYTES_LENGTH);
    }

    public Message(Enum<?> status, T data) {
        this(status.name(), data, UNKNOWN_MESSAGE_DATA_BYTES_LENGTH);
    }

    public Message(String status, T data) {
        this(status, data, UNKNOWN_MESSAGE_DATA_BYTES_LENGTH);
    }

    Message(String status, T data, int dataBytesLength) {
        this.status = status;
        this.data = data;
        this.dataBytesLength = dataBytesLength;
    }

    /**
     * @return the payload contained within this message
     */
    public T getData() {
        return data;
    }

    /**
     * @return the status of this message
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the status of this message translated with {@link Status#fromString(String)}
     */
    public Status getStatusAsEnum() {
        return Status.fromString(status);
    }

    /**
     * Meta-data that can be used for serializers optimization.
     * The value includes only the amount of data bytes,
     * the amount of bytes used for the header, status and delimiters
     * is omitted.
     *
     * @return amount of bytes of data that were read
     */
    public int getDataBytesLength() {
        return dataBytesLength;
    }

    /**
     * @return a {@link String} representation of this message
     */
    @Override
    public String toString() {
        String dataAsString;
        if (data == null) {
            dataAsString = "";
        }
        else {
            dataAsString = String.valueOf(data);
            if (dataAsString.length() > MAX_DATA_LENGTH_REASONABLY_PRINTABLE) {
                dataAsString = dataAsString.substring(0, MAX_DATA_LENGTH_REASONABLY_PRINTABLE);
            }
        }
        final String lengthPart;
        if (data instanceof byte[]) {
            lengthPart = "(" + (((byte[]) data).length) + ")";
        }
        else if (dataBytesLength != UNKNOWN_MESSAGE_DATA_BYTES_LENGTH) {
            lengthPart = "(" + dataBytesLength + ")";
        }
        else {
            lengthPart = "";
        }

        final String statusPart = status == null ? "" : status;
        final String statusAndDataPart = statusPart.isEmpty() && dataAsString.isEmpty()
                                         ? ""
                                         : statusPart.isEmpty()
                                           ? "[" + dataAsString + "]"
                                           : dataAsString.isEmpty()
                                             ? "[" + statusPart + "]"
                                             : "[" + statusPart + ";" + dataAsString + "]";

        final String variablePart = lengthPart + statusAndDataPart;
        return "Message" + (variablePart.isEmpty() ? ".empty" : variablePart);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Message &&
               Objects.equals(status, ((Message) other).status) &&
               Objects.equals(data, ((Message) other).data);
    }

}
