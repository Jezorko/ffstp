package jezorko.ffstp;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Provides constant values found in the protocol structure.
 */
final class Constants {

    /**
     * Header with which each message must begin.
     */
    final static byte[] PROTOCOL_HEADER = {'F', 'F', 'S'};

    /**
     * Message fields separator.
     */
    final static byte MESSAGE_DELIMITER = ';';

    /**
     * Charset in which data will be encoded.
     */
    final static Charset DEFAULT_CHARSET = US_ASCII;

    /**
     * Used in {@link Message#toString()} to determine how many characters of the
     * {@link String} representation of data should be included in the {@link String}
     * representation of the message.
     */
    final static int MAX_DATA_LENGTH_REASONABLY_PRINTABLE = 30;

}
