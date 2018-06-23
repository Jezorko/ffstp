package jezorko.ffstp.serializers;

import jezorko.ffstp.Serializer;
import jezorko.ffstp.serializers.ByteArraySerializer.ByteArray;

import java.util.Arrays;

import static java.util.Objects.*;

/**
 * Convenient class for when we don't really care about serialization.
 * Request data must be wrapped in a simple {@link ByteArray} object.
 */
public class ByteArraySerializer implements Serializer<ByteArray> {

    @Override
    public byte[] serialize(ByteArray data) {
        return requireNonNull(data, "byte array must not be null but may contain null bytes").bytes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y extends ByteArray> Y deserialize(byte[] data, Class<Y> clazz) {
        return (Y) deserialize(data);
    }

    @Override
    public ByteArray deserialize(byte[] data) {
        return new ByteArray(data);
    }

    /**
     * Simple wrapper for <b>byte[]</b> type.
     */
    public final static class ByteArray {

        private final byte[] bytes;

        public ByteArray(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            ByteArray byteArray = (ByteArray) other;
            return Arrays.equals(bytes, byteArray.bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }
}
