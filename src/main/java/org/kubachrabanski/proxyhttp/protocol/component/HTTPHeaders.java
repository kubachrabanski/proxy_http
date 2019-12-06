package org.kubachrabanski.proxyhttp.protocol.component;

import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class HTTPHeaders implements Component<HTTPTransport> {

    private static final Logger logger = Logger.getLogger(HTTPHeaders.class.getName());

    @Override
    public void receive(HTTPTransport transport) throws IOException {
        Map<Field, String[]> recognizedHeaders = new LinkedHashMap<>();
        Map<String, String> unrecognizedHeaders = new LinkedHashMap<>();

        String headerText;

        while ((headerText = transport.readLine()) != null && !headerText.isBlank()) {
            logger.info(String.format(
                    "Received header text: %s", headerText
            ));

            String[] headerParts = headerText.split(": ");

            if (headerParts.length != 2) {
                logger.warning(String.format(
                        "Skipping malformatted header text: %s",
                        Arrays.toString(headerParts)
                ));
                continue;
            }

            try {
                Field headerField = Field.getField(headerParts[0]);
                String[] headerContent = headerParts[1].split(", ");

                recognizedHeaders.putIfAbsent(headerField, headerContent);

                logger.info(String.format(
                        "Recognized header: %s, %s",
                        headerField,
                        Arrays.toString(headerContent)
                ));
            }
            catch (IllegalArgumentException exception) {
                unrecognizedHeaders.putIfAbsent(headerParts[0], headerParts[1]);
                logger.warning(String.format(
                        "Failed to recognize header: %s",
                        Arrays.toString(headerParts)
                ));
            }
        }

        this.recognizedHeaders = recognizedHeaders;
        this.unrecognizedHeaders = unrecognizedHeaders;
    }

    @Override
    public void send(HTTPTransport transport) throws IOException {
        if (recognizedHeaders == null || unrecognizedHeaders == null) {
            logger.warning(
                    "Skipping headers, were not received"
            );
            return;
        }

        for (var entry : recognizedHeaders.entrySet()) {
            StringJoiner joiner = new StringJoiner(", ");

            for (String part : entry.getValue()) {
                joiner.add(part);
            }

            String headerText = String.format(
                    "%s: %s", entry.getKey(), joiner.toString()
            );

            transport.writeLine(headerText);

            logger.info(String.format(
                    "Sent recognized header text: %s", headerText
            ));
        }

        for (var entry : unrecognizedHeaders.entrySet()) {
            String headerText = String.format(
                    "%s: %s", entry.getKey(), entry.getValue()
            );

            transport.writeLine(headerText);

            logger.info(String.format(
                    "Sent unrecognized header text: %s", headerText
            ));
        }

        transport.newLine();
        transport.newLine();
    }

    private Map<Field, String[]> recognizedHeaders;
    private Map<String, String> unrecognizedHeaders;

    public String[] getHeader(Field field) {
        return recognizedHeaders.get(field);
    }

    public void setHeader(Field field, String[] content) {
        recognizedHeaders.replace(field, content);
    }

    public enum Field {

        HOST ("Host"),
        USER_AGENT ("User-Agent"),
        CONTENT_LENGTH ("Content-Length"),
        TRANSFER_ENCODING ("Transfer-Encoding");

        private static final Map<String, Field> fields = new HashMap<>();

        static {
            for (Field field : values()) {
                fields.put(field.toString(), field);
            }
        }

        public static Field getField(String name) {
            Field field = fields.get(name);

            if (field == null) {
                throw new IllegalArgumentException(String.format(
                        "No enum constant for %s", name
                ));
            }

            return field;
        }

        private final String field;

        Field(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return field;
        }
    }
}
