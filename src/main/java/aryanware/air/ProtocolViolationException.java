package aryanware.air;

import java.io.IOException;

/**
 * @author ryanafrish7
 * @since 29/09/16
 */
public class ProtocolViolationException extends IOException {
    public ProtocolViolationException() {
        super();
    }

    public ProtocolViolationException(String message) {
        super(message);
    }

    public ProtocolViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
