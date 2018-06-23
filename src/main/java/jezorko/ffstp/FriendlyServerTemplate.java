package jezorko.ffstp;

import java.net.Socket;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A decorator for {@link FriendlyTemplate}.
 * Can be used to implement a simple and (obviously) friendly server application.
 * Provides methods that allow for request handling flow:
 * <li>{@link #waitForRequestAndReply(Function)}</li>
 * <li>{@link #waitForRequestAndReply(Class, Function)}</li>
 *
 * @param <T> defines the lower-bound type allowed as a message payload
 */
public class FriendlyServerTemplate<T> implements AutoCloseable {

    private final FriendlyTemplate<T> friendlyTemplate;

    public FriendlyServerTemplate(Socket socket, Serializer<T> serializer) {
        this.friendlyTemplate = new FriendlyTemplate<>(socket, serializer);
    }

    /**
     * To be used for implementing two-way communication system.
     * This method will block until enough data is available in the socket.
     * A response produced by the handler will be sent back to the socket.
     *
     * @param requestClass   expected class of the incoming message
     * @param requestHandler that will be used to produce the response
     * @param <Y>            expected type of the request message
     */
    public <Y extends T> void waitForRequestAndReply(Class<Y> requestClass, Function<Message<? extends T>, Message<? extends T>> requestHandler) {
        waitForRequestAndReply(requestHandler, () -> friendlyTemplate.readMessage(requestClass));
    }

    /**
     * Same as {@link #waitForRequestAndReply(Class, Function)} but uses the
     * simple deserialization method {@link Serializer#deserialize(byte[])}.
     * Keep in mind that not every serializer will implement this method.
     */
    public void waitForRequestAndReply(Function<Message<? extends T>, Message<? extends T>> requestHandler) {
        waitForRequestAndReply(requestHandler, friendlyTemplate::readMessage);
    }

    private void waitForRequestAndReply(Function<Message<? extends T>, Message<? extends T>> requestHandler, Supplier<Message<? extends T>> messageReader) {
        final Message<? extends T> request = messageReader.get();
        final Message<? extends T> response = requestHandler.apply(request);
        friendlyTemplate.writeMessage(response);
    }

    @Override
    public void close() throws Exception {
        friendlyTemplate.close();
    }
}
