package jezorko.ffstp;

import jezorko.ffstp.exception.ProtocolReaderInitializationException;
import jezorko.ffstp.exception.ProtocolWriterInitializationException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.function.Function;

/**
 * A wrapper class for {@link FriendlyForkedSocketTransferProtocolReader} and {@link FriendlyForkedSocketTransferProtocolWriter}.
 * Makes things more convenient by creating a more friendly interface for inter-socket communication.
 * Two types of communication are handled by two method pairs.
 * This class is designed for low-level communication and provides simple read / write methods:
 * <li>{@link #writeMessage(Message)}</li>
 * <li>{@link #readMessage(Class)}</li>
 * <br>
 * Additionally, methods that require deserialization of data are provided
 * in two variants, with and without the response class.
 *
 * @param <T> defines the lower-bound type allowed as a message payload
 *
 * @see FriendlyClientTemplate
 * @see FriendlyServerTemplate
 */
public class FriendlyTemplate<T> implements AutoCloseable {

    private final FriendlyForkedSocketTransferProtocolReader reader;
    private final FriendlyForkedSocketTransferProtocolWriter writer;
    private final Serializer<T> serializer;

    /**
     * Creates instances of reader and writer classes.
     * It does not take the ownership over provided socket instance.
     * In case if reader or writer cannot be initialized, socket will not be closed.
     *
     * @param socket     to be used for communication
     * @param serializer to be used for serializing request and response messages
     */
    public FriendlyTemplate(Socket socket, Serializer<T> serializer) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            writer = new FriendlyForkedSocketTransferProtocolWriter(outputStream);
        } catch (Exception e) {
            throw new ProtocolWriterInitializationException(e);
        }
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            reader = new FriendlyForkedSocketTransferProtocolReader(inputStream);
        } catch (Exception e) {
            throw new ProtocolReaderInitializationException(e);
        }
        this.serializer = serializer;
    }

    /**
     * Use only if you intend to implement a one-way communication system.
     * This method will block until there is enough data available in the socket.
     * For more details see {@link FriendlyForkedSocketTransferProtocolReader#readMessage()}.
     *
     * @param messageClass to deserialize the message from
     * @param <Y>          expected type of the message
     *
     * @return incoming message
     */
    public <Y extends T> Message<Y> readMessage(Class<Y> messageClass) {
        return readMessage(data -> serializer.deserialize(data, messageClass));
    }

    /**
     * Same as {@link #readMessage(Class)} but uses
     * the simple deserialization method {@link Serializer#deserialize(byte[])}.
     * Keep in mind that not every serializer will implement this method.
     */
    public Message<T> readMessage() {
        return readMessage(serializer::deserialize);
    }

    private <Y extends T> Message<Y> readMessage(Function<byte[], Y> deserializationFunction) {
        final Message<byte[]> serializedMessage = reader.readMessageRethrowErrors();
        final Y message = deserializationFunction.apply(serializedMessage.getData());
        return new Message<>(serializedMessage.getStatus(), message, serializedMessage.getDataBytesLength());
    }

    /**
     * Use only if you intend to implement a one-way communication system.
     * This is a non-blocking method which will send the entire message to the socket at once.
     * For more details see {@link FriendlyForkedSocketTransferProtocolWriter#writeMessage(Message)}.
     *
     * @param message to be serialized and send through the socket
     */
    public void writeMessage(Message<? extends T> message) {
        final Message<byte[]> serializedMessage = new Message<>(message.getStatus(), serializer.serialize(message.getData()));
        writer.writeMessage(serializedMessage);
    }

    @Override
    public void close() throws Exception {
        try {
            reader.close();
        } finally {
            writer.close();
        }
    }
}
