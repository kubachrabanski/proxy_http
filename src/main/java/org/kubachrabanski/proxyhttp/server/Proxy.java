package org.kubachrabanski.proxyhttp.server;

import org.kubachrabanski.proxyhttp.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Proxy extends Thread implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(Proxy.class.getName());

    private final ServerSocket socket;

    public Proxy(final int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    @Override
    public void run() {
        logger.info(String.format(
                "Started listening on %s", socket.getInetAddress()
        ));

        while (true) {
            try {
                Socket client = socket.accept();

                logger.info(String.format(
                        "Accepted connection from: %s",
                        client.getInetAddress()
                ));

                Connection connection = new Connection(client);
                connection.start();
            }
            catch (IOException exception) {
                logger.warning(String.format(
                        "Failed to accept connection:\n%s",
                        Main.toString(exception)
                ));
            }
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        }
        catch (IOException exception) {
            logger.severe(String.format(
                    "Failed to close proxy:\n%s",
                    Main.toString(exception)
            ));
        }
    }
}
