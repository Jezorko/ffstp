package jezorko.ffstp;

/**
 * Convenient statuses definitions.
 * You can define your own statuses and handle them however you like.
 * Those are just suggestions and doesn't have any specific meaning for the protocol.
 */
public enum Status {

    /**
     * Indicates that everything is a-okay.
     */
    OK,

    /**
     * Indicates that some error has occurred, but nobody knows what exactly went wrong.
     */
    ERROR,

    /**
     * Indicates that the status is different than the one expected.
     */
    ERROR_INVALID_STATUS,

    /**
     * Indicates that message payload is not as expected.
     */
    ERROR_INVALID_PAYLOAD,

    /**
     * Indicates that the receiver of the message should shop listening to new messages.
     */
    DIE,

    /**
     * Indicates that the received status is a custom one.
     */
    UNKNOWN;

    /**
     * Returns a status that has a name similar to the given string.
     * Statuses are compared with {@link String#equalsIgnoreCase(String)}.
     *
     * @param status to be looked up
     *
     * @return any status whose name is similar to the given string or {@link #UNKNOWN} if none is
     */
    public static Status fromString(String status) {
        for (Status s : Status.values()) {
            if (s.name()
                 .equalsIgnoreCase(status)) {
                return s;
            }
        }
        return UNKNOWN;
    }

}
