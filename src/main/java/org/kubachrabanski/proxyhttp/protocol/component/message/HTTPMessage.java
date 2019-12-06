package org.kubachrabanski.proxyhttp.protocol.component.message;

import org.kubachrabanski.proxyhttp.protocol.component.Component;
import org.kubachrabanski.proxyhttp.protocol.component.HTTPHeaders;
import org.kubachrabanski.proxyhttp.protocol.component.HTTPPayload;
import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HTTPMessage implements Component<HTTPTransport> {

    private static final Logger logger = Logger.getLogger(HTTPMessage.class.getName());

    @Override
    public void receive(HTTPTransport transport) throws IOException {
        this.headers = new HTTPHeaders();
        this.headers.receive(transport);

        logger.info(
                "Received headers"
        );

        this.payload = new HTTPPayload();
        this.payload.receive(transport, this.headers);

        logger.info(
                "Received payload"
        );
    }

    @Override
    public void send(HTTPTransport transport) throws IOException {
        // check not initialized
        headers.send(transport);
        payload.send(transport);
    }

    private HTTPHeaders headers;
    private HTTPPayload payload;

    public HTTPHeaders getHeaders() {
        return headers;
    }

    public HTTPPayload getPayload() {
        return payload;
    }

    public enum Version {

        HTTP_10 ("HTTP/1.0"),
        HTTP_11 ("HTTP/1.1"),
        HTTP_20 ("HTTP/2.0");

        private static final Map<String, Version> versions = new HashMap<>();

        static {
            for (Version version : values()) {
                versions.put(version.toString(), version);
            }
        }

        public static Version getVersion(String name) {
            Version version = versions.get(name);

            if (version == null) {
                throw new IllegalArgumentException(String.format(
                        "No enum constant for %s", name
                ));
            }

            return version;
        }

        private final String version;

        Version(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return version;
        }
    }
}
