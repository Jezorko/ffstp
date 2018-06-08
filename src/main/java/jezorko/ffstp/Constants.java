package jezorko.ffstp;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_16;

/**
 * Provides constant values found in the protocol structure.
 */
final class Constants {

    /**
     * Header with which each message must begin.
     */
    final static String PROTOCOL_HEADER = "FFS";

    /**
     * Message fields separator.
     */
    final static char MESSAGE_DELIMITER = ';';

    /**
     * Charset in which data will be encoded.
     */
    final static Charset DEFAULT_CHARSET = UTF_16;

}
