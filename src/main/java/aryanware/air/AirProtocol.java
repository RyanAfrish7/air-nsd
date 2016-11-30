package aryanware.air;

/**
 * @author ryanafrish7
 * @since 4/10/16.
 */
class AirProtocol {
    static final int HOST_PORT = 7474;

    static final String PROTOCOL_NAME = "AIRNSD";
    static final String PROTOCOL_VERSION = "0.0.1";
    static final String PROTOCOL_SPECIFIER = PROTOCOL_NAME + " " + PROTOCOL_VERSION;

    static final String INTENT_SPECIFIER = "Intent";
    static final String INTENT_BROADCAST = "BROADCAST";
    static final String INTENT_BYE_AIR = "BYE";
    static final String INTENT_HELLO_AIR = "HELLO";
    static final String INTENT_REGISTER_SERVICE = "REGISTER";

    static final String RESPONSE_SPECIFIER = "Result";
    static final String RESPONSE_OK = "OK";
    static final String RESPONSE_UNRECOGNIZED = "UNKNOWN_INTENT";

    static final String BROADCAST_SPECIFIER = "Broadcast";
}
