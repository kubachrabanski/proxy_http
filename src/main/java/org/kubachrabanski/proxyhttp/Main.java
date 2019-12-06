package org.kubachrabanski.proxyhttp;

import org.kubachrabanski.proxyhttp.server.Proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public final class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static String toString(Exception exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    public static void main(String[] args) {
        int port;

        try {
            port = Integer.parseInt(args[0]);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            logger.severe(String.format(
                    "Failed to get port:\n%s",
                    Main.toString(exception)
            ));
            return;
        }

        try (Proxy proxy = new Proxy(port)){
            proxy.start();
        }
        catch (IOException exception) {
            logger.severe(String.format(
                    "Failed to start proxy:\n%s",
                    toString(exception)
            ));
        }
    }
}
