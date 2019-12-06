package org.kubachrabanski.proxyhttp.protocol.component;

import org.kubachrabanski.proxyhttp.protocol.transport.Transport;

import java.io.IOException;

public interface Component<T extends Transport> {

    void receive(T transport) throws IOException;
    void send(T transport) throws IOException;
}
