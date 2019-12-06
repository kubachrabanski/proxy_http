package org.kubachrabanski.proxyhttp.protocol.component.message;

import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

public class HTTPRequest extends HTTPMessage {

    private static final Logger logger = Logger.getLogger(HTTPRequest.class.getName());

    @Override
    public void receive(HTTPTransport transport) throws IOException { // throw
        String requestText = transport.readLine();

        if (requestText == null) {
            logger.warning(
                    "Failed to received request text"
            );
            return;
        }

        logger.info(String.format(
                "Received request text: %s", requestText
        ));

        String[] requestParts = requestText.split("\\s+");

        if (requestParts.length != 3) {
            logger.warning(String.format(
                    "Skipping malformatted request text: %s",
                    Arrays.toString(requestParts)
            ));
            return;
        }

        this.method = Method.valueOf(requestParts[0]);
        this.address = new URL(requestParts[1]);
        this.version = Version.getVersion(requestParts[2]);

        super.receive(transport);
    }

    @Override
    public void send(HTTPTransport transport) throws IOException {
        // check not initialized
        String requestText = toString();
        transport.writeLine(requestText);

        logger.info(String.format(
                "Sent request text: %s", requestText
        ));

        super.send(transport);
    }

    private Method method;
    private URL address;
    private Version version;

    public Method getMethod() {
        return method;
    }

    public URL getAddress() {
        return address;
    }

    public Version getVersion() {
        return version;
    }

    public String getHost() {
        return address.getHost();
    }

    public String getPath() {
        return address.getPath();
    }

    public int getPort() {
        return address.getPort();
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", method, getPath(), version);
    }

    public enum Method {

        GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE;
    }
}
