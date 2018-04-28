package jezorko.ffstp;

import jezorko.ffstp.exception.RethrownException;
import jezorko.ffstp.util.StringSerializer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.logging.Logger.getLogger;
import static jezorko.ffstp.Status.DIE;

public class MessagesExchangeExample {

    private static final Logger log = getLogger(MessagesExchangeExample.class.getName());

    private static final int TESTING_PORT = 8000;

    public static void main(String... args) throws InterruptedException {
        final ExecutorService threadPool = newFixedThreadPool(2);
        final Future<?> serverFuture = threadPool.submit(MessagesExchangeExample::runServer);
        final Future<?> clientFuture = threadPool.submit(MessagesExchangeExample::runClient);

        while (!serverFuture.isDone()) {
            sleep(1000);
        }
        log.info("Server died!");
        while (!clientFuture.isDone()) {
            sleep(1000);
        }
        log.info("Client died!");

        threadPool.shutdown();
    }

    private static void runServer() {
        try (
                ServerSocket serverSocket = new ServerSocket(TESTING_PORT);
                Socket clientSocket = serverSocket.accept();
                FriendlyTemplate<String> ffstp = new FriendlyTemplate<>(clientSocket, new StringSerializer())
        ) {
            log.info("Server opened connection");
            Message<String> message;
            do {
                message = ffstp.readMessage(String.class);
                log.info("Server received " + message);
                ffstp.writeMessage(Message.ok("Bless you!"));
            } while (message.getStatusAsEnum() != DIE);

            log.info("Dying... ");
            ffstp.writeMessage(Message.ok("x_x"));
        } catch (Exception e) {
            throw new RethrownException(e);
        }
    }

    private static void runClient() {
        try (
                Socket serverSocket = new Socket("localhost", TESTING_PORT);
                FriendlyTemplate<String> ffstp = new FriendlyTemplate<>(serverSocket, new StringSerializer())
        ) {
            log.info("Client opened connection");
            for (int i = 0; i < 5; ++i) {
                final Message<String> response = ffstp.sendAndAwaitResponse(Message.ok("Ahooo!"),
                                                                            String.class);
                log.info("Client received " + response);
            }
            log.info("Sending self-kill request");
            final Message<String> response = ffstp.sendAndAwaitResponse(Message.die("DIE DIE DIE"), String.class);
            log.info("Client received " + response);
        } catch (Exception e) {
            throw new RethrownException(e);
        }
    }

}
