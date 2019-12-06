package org.kubachrabanski.proxyhttp.protocol.component.message;

import org.kubachrabanski.proxyhttp.protocol.transport.HTTPTransport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class HTTPResponse extends HTTPMessage {

    private static final Logger logger = Logger.getLogger(HTTPResponse.class.getName());

    @Override
    public void receive(HTTPTransport transport) throws IOException { // throw
        String responseText = transport.readLine();

        logger.info(String.format(
                "Received response text: %s", responseText
        ));

        Scanner responseReader = new Scanner(responseText);

        this.version = Version.getVersion(responseReader.next());
        this.code = Code.getCode(responseReader.nextInt());
        this.reason = responseReader.next("[a-zA-Z0-9\\s]+");

        super.receive(transport);
    }

    @Override
    public void send(HTTPTransport transport) throws IOException {
        // check not initialized
        String responseText = toString();
        transport.writeLine(responseText);

        logger.info(String.format(
                "Sent response text: %s", responseText
        ));

        super.send(transport);
    }

    private Version version;
    private Code code;
    private String reason;

    public Version getVersion() {
        return version;
    }

    public Code getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", version, code, reason);
    }

    public enum Code {

        OK (200),
        CREATED(201),
        ACCEPTED (202),
        NO_CONTENT (204),
        MOVED_PERMANENTLY (301),
        FOUND (302),
        SEE_OTHER (303),
        NOT_MODIFIED (304),
        TEMPORARY_REDIRECT (307),
        BAD_REQUEST (400),
        UNAUTHORIZED (401),
        FORBIDDEN (403),
        NOT_FOUND (404),
        METHOD_NOT_ALLOWED (405),
        NOT_ACCEPTABLE (406),
        PRECONDITION_FAILED (412),
        UNSUPPORTED_MEDIA_TYPE (415),
        INTERNAL_SERVER_ERROR (500),
        NOT_IMPLEMENTED (501);

        private static final Map<Integer, Code> codes = new HashMap<>();

        static {
            for (Code code : values()) {
                codes.put(code.getValue(), code);
            }
        }

        private final int value;

        Code(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Code getCode(int identifier) {
            Code code = codes.get(identifier);

            if (code == null) {
                throw new IllegalArgumentException(String.format(
                        "No enum constant for %d", identifier
                ));
            }

            return code;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }
}
