package org.kubachrabanski.proxyhttp.server;

import org.kubachrabanski.proxyhttp.Main;
import org.kubachrabanski.proxyhttp.protocol.HTTPProtocol;
import org.kubachrabanski.proxyhttp.protocol.component.message.HTTPRequest;
import org.kubachrabanski.proxyhttp.protocol.component.message.HTTPResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class Connection extends Thread {

    private static final Logger logger = Logger.getLogger(Connection.class.getName());

    private final Socket client;

    public Connection(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            HTTPProtocol<HTTPRequest, HTTPResponse> ingress = new HTTPProtocol<>(client, HTTPRequest::new);
            HTTPRequest request = ingress.receive();

            logger.info(String.format(
                    "Received request from client: %s", request
            ));

            if (request.getMethod() == HTTPRequest.Method.CONNECT) {
                logger.warning(String.format(
                        "Skipping HTTPS CONNECT request: %s", request
                ));
                return;
            }

            final int port = request.getPort() > -1 ? request.getPort() : 80;
            Socket server = new Socket(request.getHost(), port);

            HTTPProtocol<HTTPResponse, HTTPRequest> egress = new HTTPProtocol<>(server, HTTPResponse::new);
            egress.send(request);

            logger.info(String.format(
                    "Sent request to server: %s", request
            ));

            HTTPResponse response = egress.receive();

            logger.info(String.format(
                    "Received response from server: %s", response
            ));

            ingress.send(response);

            logger.info(String.format(
                    "Sent response to client: %s", response
            ));

            ingress.close();
            egress.close();
        }
        catch (IOException exception) {
            logger.warning(String.format(
                    "Failed to handle connection from %s:\n%s",
                    client.getInetAddress(),
                    Main.toString(exception)
            ));
        }
    }
}
