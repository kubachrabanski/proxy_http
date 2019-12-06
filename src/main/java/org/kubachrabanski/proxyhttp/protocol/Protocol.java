package org.kubachrabanski.proxyhttp.protocol;

import java.io.IOException;

public interface Protocol<A, B> extends AutoCloseable {

    void send(B response) throws IOException;
    A receive() throws IOException;
}
