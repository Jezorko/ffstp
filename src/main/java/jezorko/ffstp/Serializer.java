package jezorko.ffstp;

import jezorko.ffstp.exception.ComplexSerializerException;

/**
 * Used for serializing messages.
 * Can be anything, really. JSON, binary, even XML.
 * If you can make <b>byte[]</b> out of it, we'll take it.
 * Must be reversible, therefore expressions:
 * <pre>
 *     T variable = ...
 *     variable.equals(deserialize(serialize(variable), T.class));
 * </pre>
 * and
 * <pre>
 *     byte[] serializedVariable = ...
 *     serializedVariable.equals(serialize(deserialize(serializedVariable, T.class)));
 * </pre>
 * must evaluate to <b>true</b>.
 *
 * @param <T> defines the lower-bound type allowed for serialization
 */
public interface Serializer<T> {

    /**
     * Used to serialize the given payload.
     *
     * @param data to be serialized
     *
     * @return a <b>byte[]</b> representation of given payload
     */
    byte[] serialize(T data);

    /**
     * Used to deserialize the given payload.
     *
     * @param data  to be deserialized
     * @param clazz type to be deserialized to
     * @param <Y>   type of deserialized object
     *
     * @return a deserialized representation of data
     */
    <Y extends T> Y deserialize(byte[] data, Class<Y> clazz);

    /**
     * Used to deserialize simple payloads that do not require class information.
     * Not every serializer implementation is required to implement this method.
     *
     * @param data to be deserialized
     *
     * @return a deserialized representation of data
     */
    default T deserialize(byte[] data) {
        throw new ComplexSerializerException(this.getClass());
    }

}