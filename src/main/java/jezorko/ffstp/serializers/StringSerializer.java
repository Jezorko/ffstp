package jezorko.ffstp.serializers;

import jezorko.ffstp.Serializer;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_16;

/**
 * Convenient class for when we want to send only plain {@link String} messages.
 * The data will be encoded with {@link java.nio.charset.StandardCharsets#UTF_16}.
 */
public final class StringSerializer implements Serializer<String> {

    private final static Charset MESSAGE_ENCODING = UTF_16;

    @Override
    public byte[] serialize(String data) {
        return data != null ? data.getBytes(MESSAGE_ENCODING) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends String> T deserialize(byte[] data, Class<T> clazz) {
        return (T) deserialize(data);
    }

    @Override
    public String deserialize(byte[] data) {
        return data != null ? new String(data, MESSAGE_ENCODING) : null;
    }
}
