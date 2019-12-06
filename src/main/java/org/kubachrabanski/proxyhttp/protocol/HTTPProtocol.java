package org.kubachrabanski.proxyhttp.protocol;

import org.kubachrabanski.proxyhttp.protocol.component.message.HTTPMessage;
import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Supplier;

public class HTTPProtocol<A extends HTTPMessage, B extends HTTPMessage> implements Protocol<A, B> {

    private final HTTPTransport transport;
    private final Supplier<A> supplier;

    public HTTPProtocol(HTTPTransport transport, Supplier<A> supplier) {
        this.transport = transport;
        this.supplier = supplier;
    }

    public HTTPProtocol(Socket socket, Supplier<A> supplier) throws IOException {
        this(new HTTPTransport(socket), supplier);
    }

    @Override
    public void send(B response) throws IOException {
        response.send(transport);
        transport.flush();
    }

    @Override
    public A receive() throws IOException {
        A request = supplier.get();
        request.receive(transport);
        return request;
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }
}
