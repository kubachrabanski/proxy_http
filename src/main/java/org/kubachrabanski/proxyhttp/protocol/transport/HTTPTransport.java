package org.kubachrabanski.proxyhttp.protocol.transport;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTPTransport implements Transport {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public HTTPTransport(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        this.writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
        );
    }

    @Override
    public int read() throws IOException {
        return reader.read();
    }

    @Override
    public int read(char[] buffer) throws IOException {
        return reader.read(buffer);
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void write(String text) throws IOException {
        writer.write(text);
    }

    @Override
    public void write(char[] buffer) throws IOException {
        writer.write(buffer);
    }

    @Override
    public void writeLine(String text) throws IOException {
        write(text);
        newLine();
    }

    public void newLine() throws IOException {
        write("\r\n");
    }

    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
