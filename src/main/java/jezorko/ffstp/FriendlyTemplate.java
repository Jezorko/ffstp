package jezorko.ffstp;

import jezorko.ffstp.exception.ProtocolReaderInitializationException;
import jezorko.ffstp.exception.ProtocolWriterInitializationException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Function;

import static jezorko.ffstp.Constants.DEFAULT_CHARSET;

/**
 * A wrapper class for {@link FriendlyForkedSocketTransferProtocolReader} and {@link FriendlyForkedSocketTransferProtocolWriter}.
 * Makes things more convenient by creating a more friendly interface for inter-socket communication.
 * Two types of communication are handled by two method pairs.<br/><br/>
 * <h3>Two-way communication methods:</h3>
 * <li>{@link #sendAndAwaitResponse(Message, Class)}</li>
 * <li>{@link #waitForRequestAndReply(Class, Function)}</li>
 * <h3>One-way communication methods:</h3>
 * <li>{@link #writeMessage(Message)}</li>
 * <li>{@link #readMessage(Class)}</li>
 *
 * @param <T> defines the lower-bound type allowed as a message payload
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
            PrintWriter outputWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), DEFAULT_CHARSET), true);
            writer = new FriendlyForkedSocketTransferProtocolWriter(outputWriter);
        } catch (Exception e) {
            throw new ProtocolWriterInitializationException(e);
        }
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET), 32768);
            reader = new FriendlyForkedSocketTransferProtocolReader(inputReader);
        } catch (Exception e) {
            throw new ProtocolReaderInitializationException(e);
        }
        this.serializer = serializer;
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
        final Message<String> serializedRequest = new Message<>(requestMessage.getStatus(), serializer.serialize(requestMessage.getData()));
        writer.writeMessage(serializedRequest);
        final Message<String> serializedResponse = reader.readMessageRethrowErrors();
        final Y response = serializer.deserialize(serializedResponse.getData(), responseClass);
        return new Message<>(serializedResponse.getStatus(), response);
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
    public <Y extends T> void waitForRequestAndReply(Class<Y> requestClass, Function<Message<? extends Y>, Message<? extends T>> requestHandler) {
        final Message<String> serializedRequest = reader.readMessageRethrowErrors();
        final Message<? extends Y> request = new Message<>(serializedRequest.getStatus(), serializer.deserialize(serializedRequest.getData(), requestClass));
        final Message<? extends T> response = requestHandler.apply(request);
        final String serializedResponse = serializer.serialize(request.getData());
        writer.writeMessage(new Message<>(response.getStatus(), serializedResponse));
    }

    /**
     * Use only if you intend to implement a one-way communication system.
     * Otherwise, use the {@link #waitForRequestAndReply(Class, Function)} method.
     * This method will block until there is enough data available in the socket.
     * For more details see {@link FriendlyForkedSocketTransferProtocolReader#readMessage()}.
     *
     * @param messageClass to deserialize the message from
     * @param <Y>          expected type of the message
     *
     * @return incoming message
     */
    public <Y extends T> Message<Y> readMessage(Class<Y> messageClass) {
        final Message<String> serializedMessage = reader.readMessageRethrowErrors();
        final Y message = serializer.deserialize(serializedMessage.getData(), messageClass);
        return new Message<>(serializedMessage.getStatus(), message);
    }

    /**
     * Use only if you intend to implement a one-way communication system.
     * Otherwise, use the {@link #sendAndAwaitResponse(Message, Class)} method.
     * This is a non-blocking method which will send the entire message to the socket at once.
     * For more details see {@link FriendlyForkedSocketTransferProtocolWriter#writeMessage(Message)}.
     *
     * @param message to be serialized and send through the socket
     */
    public void writeMessage(Message<? extends T> message) {
        final Message<String> serializedMessage = new Message<>(message.getStatus(), serializer.serialize(message.getData()));
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
