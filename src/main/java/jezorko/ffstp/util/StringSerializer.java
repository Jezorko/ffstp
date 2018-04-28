package jezorko.ffstp.util;

import jezorko.ffstp.Serializer;

/**
 * Convenient class for when we want to skip the serialization / deserialization completely.
 * All messages payloads are treated as a plain {@link String}.
 */
public final class StringSerializer implements Serializer<String> {

    @Override
    public String serialize(String data) {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y extends String> Y deserialize(String data, Class<Y> clazz) {
        return (Y) data;
    }
}
