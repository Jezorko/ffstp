package jezorko.ffstp;

import java.net.Socket;
import java.util.function.Supplier;

/**
 * A decorator for {@link FriendlyTemplate}.
 * Can be used to implement a simple and (obviously) friendly client application.
 * Provides methods that allow for send-and-receive flow:
 * <li>{@link #sendAndAwaitResponse(Message)}</li>
 * <li>{@link #sendAndAwaitResponse(Message, Class)}</li>
 *
 * @param <T> defines the lower-bound type allowed as a message payload
 */
public class FriendlyClientTemplate<T> implements AutoCloseable {

    private final FriendlyTemplate<T> friendlyTemplate;

    public FriendlyClientTemplate(Socket socket, Serializer<T> serializer) {
        this.friendlyTemplate = new FriendlyTemplate<>(socket, serializer);
    }

    /**
     * To be used for implementing two-way communication system.
     * First, a message is sent to the socket.
     * Then a blocking-read operation will await the response and deserialize it.
     *
     * @param requestMessage to be sent to the socket
     * @param responseClass  that response will be deserialized to
     * @param <Y>            type of the response message
     *
     * @return deserialized message
     */
    public <Y extends T> Message<Y> sendAndAwaitResponse(Message<? extends T> requestMessage, Class<Y> responseClass) {
        return sendAndAwaitResponse(requestMessage, () -> friendlyTemplate.readMessage(responseClass));
    }

    /**
     * Same as {@link #sendAndAwaitResponse(Message, Class)} but uses the
     * simple deserialization method {@link Serializer#deserialize(byte[])}.
     * Keep in mind that not every serializer will implement this method.
     */
    public Message<T> sendAndAwaitResponse(Message<? extends T> requestMessage) {
        return sendAndAwaitResponse(requestMessage, friendlyTemplate::readMessage);
    }

    private <Y extends T> Message<Y> sendAndAwaitResponse(Message<? extends T> requestMessage, Supplier<Message<Y>> messageReader) {
        friendlyTemplate.writeMessage(requestMessage);
        return messageReader.get();
    }

    @Override
    public void close() throws Exception {
        friendlyTemplate.close();
    }
}
