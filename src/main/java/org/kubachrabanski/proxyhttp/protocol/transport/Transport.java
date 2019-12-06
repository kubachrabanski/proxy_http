package org.kubachrabanski.proxyhttp.protocol.transport;

import java.io.IOException;

public interface Transport extends AutoCloseable {

    int read() throws IOException;
    int read(char[] buffer) throws IOException;
    String readLine() throws IOException;

    void write(String text) throws IOException;
    void write(char[] buffer) throws IOException;
    void writeLine(String text) throws IOException;
}
