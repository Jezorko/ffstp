package jezorko.ffstp;

import jezorko.ffstp.exception.RethrownException;
import jezorko.ffstp.serializers.StringSerializer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static jezorko.ffstp.Message.die;
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
                FriendlyServerTemplate<String> ffstpServer = new FriendlyServerTemplate<>(clientSocket, new StringSerializer())
        ) {
            log.info("Server opened connection");
            AtomicBoolean shouldDie = new AtomicBoolean(false);

            while (!shouldDie.get()) {
                ffstpServer.waitForRequestAndReply(request -> {
                    log.info("Server received " + request);
                    if (request.getStatusAsEnum() != DIE) {
                        return Message.ok("Bless you!");
                    }
                    else {
                        shouldDie.set(true);
                        return Message.ok("x_x");
                    }
                });
            }

            log.info("Dying... ");
        } catch (Exception e) {
            log.log(SEVERE, "Server dies because of an exception", e);
            throw new RethrownException(e);
        }
    }

    private static void runClient() {
        try (
                Socket serverSocket = new Socket("localhost", TESTING_PORT);
                FriendlyClientTemplate<String> ffstpClient = new FriendlyClientTemplate<>(serverSocket, new StringSerializer())
        ) {
            log.info("Client opened connection");
            for (int i = 0; i < 5; ++i) {
                final Message<String> response = ffstpClient.sendAndAwaitResponse(Message.ok("Ahooo!"));
                log.info("Client received " + response);
            }
            log.info("Sending self-kill request");
            final Message<String> response = ffstpClient.sendAndAwaitResponse(die());
            log.info("Client received " + response);
        } catch (Exception e) {
            log.log(SEVERE, "Client dies because of an exception", e);
            throw new RethrownException(e);
        }
    }

}
