package jezorko.ffstp;

import java.util.Objects;

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

    /**
     * Returns a message instance that has both status and payload set to null.
     */
    @SuppressWarnings("unchecked")
    public final static Message EMPTY = new Message((String) null, null);

    private final T data;
    private final String status;

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
    public static <T> Message<T> ok(T data) {
        return new Message<>(OK, data);
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
    public static <T> Message<T> errorInvalidStatus(T data) {
        return new Message<>(ERROR_INVALID_STATUS, data);
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
    public static <T> Message<T> die(T data) {
        return new Message<>(DIE, data);
    }

    public Message(Status status, T data) {
        this(status.name(), data);
    }

    public Message(Enum<?> status, T data) {
        this(status.name(), data);
    }

    public Message(String status, T data) {
        this.status = status;
        this.data = data;
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
     * @return a {@link String} representation of this message
     */
    @Override
    public String toString() {
        return "Message(" + String.valueOf(data).length() + ")[" + status + ";" + data + "]";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Message &&
               Objects.equals(status, ((Message) other).status) &&
               Objects.equals(data, ((Message) other).data);
    }
}
