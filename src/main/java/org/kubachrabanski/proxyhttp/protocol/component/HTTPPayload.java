package org.kubachrabanski.proxyhttp.protocol.component;

import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

public class HTTPPayload implements Component<HTTPTransport> {

    private static final Logger logger = Logger.getLogger(HTTPPayload.class.getName());

    @Override
    public void receive(HTTPTransport transport) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = transport.readLine()) != null) {
            builder.append(line);
        }

        builder.getChars(0, builder.length(), this.contents, 0);

        logger.info(String.format(
                "Received payload contents: %d", builder.length()
        ));
    }

    public void receive(HTTPTransport transport, HTTPHeaders headers) throws IOException {
        // (...) the Content-Length header is omitted in this case (...)

        String[] transferEncoding = headers.getHeader(HTTPHeaders.Field.TRANSFER_ENCODING);

        if (transferEncoding != null) {
            for (String part : transferEncoding) {
                if (part.equals("chunked")) {
                    receiveChunked(transport);
                    return;
                }
            }

            logger.warning(String.format(
                    "Skipping transfer encoding, no supported specifier: %s",
                    Arrays.toString(transferEncoding)
            ));
        }

        String[] contentLength = headers.getHeader(HTTPHeaders.Field.CONTENT_LENGTH);

        if (contentLength != null && contentLength.length > 0) {
            try {
                final int length = Integer.parseInt(contentLength[0]);
                receiveContent(transport, length);
                return;
            }
            catch (NumberFormatException exception) {
                logger.warning(String.format(
                        "Skipping contents length, failed to parse length: %s",
                        Arrays.toString(contentLength)
                ));
            }
        }

        receive(transport);
    }

    private void receiveContent(HTTPTransport transport, int length) throws IOException {
        this.contents = new char[length];
        int state = transport.read(this.contents);

        logger.info(String.format(
                "Received payload contents: %d, using Content-Length: %d",
                state, length
        ));
    }

    private void receiveChunked(HTTPTransport transport) throws IOException {
        CharBuffer buffer = CharBuffer.allocate(64);
        int chunkSize;

        while ((chunkSize = transport.read()) != 0) {
            for (int i = 0; i < chunkSize; i++) { // log each chunk in level finest
                buffer.append((char) transport.read()); // check if reading with read() is not slower
            }
        }

        transport.readLine(); // read ending sequence \r\n

        this.contents = buffer.array();

        logger.info(String.format(
                "Received payload contents: %d, using Transfer-Encoding: chunked",
                this.contents.length
        ));
    }

    @Override
    public void send(HTTPTransport transport) throws IOException {
        if (contents != null) {
            transport.write(contents);

            logger.info(String.format(
                    "Sent payload contents: %d", contents.length
            ));
        } else {
            logger.warning(
                    "Skipping payload, was not received"
            );
        }
    }

    private char[] contents;

    public char[] getContents() {
        return contents;
    }
}
