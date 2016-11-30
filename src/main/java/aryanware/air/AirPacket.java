package aryanware.air;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by ryanafrish7 on 14/10/16.
 */
public class AirPacket {

    Type type;
    String protocol, objective, data;
    Map<String, String> mentions = new HashMap<>();

    private AirPacket() {
    }

    AirPacket(Type type) {
        this.protocol = AirProtocol.PROTOCOL_SPECIFIER;
        this.type = type;
    }

    static AirPacket readFromStream(InputStream rawIn) throws ProtocolViolationException {
        AirPacket packet = new AirPacket();
        Scanner in = new Scanner(new BufferedInputStream(rawIn));

        // Parsing protocol specification and type
        String[] s = in.nextLine().split(" / ", 2);

        packet.protocol = s[0];
        packet.type = Type.valueOf(s[1]);

        if (!AirProtocol.PROTOCOL_SPECIFIER.equals(packet.protocol))
            throw new ProtocolViolationException("Unspecified protocol");

        // Blank line
        in.nextLine();

        // Objective mention
        switch (packet.type) {
            case REQUEST:
                packet.objective = readMention(in.nextLine(), AirProtocol.INTENT_SPECIFIER);
                break;

            case RESPONSE:
                packet.objective = readMention(in.nextLine(), AirProtocol.RESPONSE_SPECIFIER);
                break;

            case BROADCAST:
                packet.objective = readMention(in.nextLine(), AirProtocol.BROADCAST_SPECIFIER);
                break;
        }

        // Other mentions + Blank line
        String mention = in.nextLine();

        while (!mention.isEmpty()) {
            s = mention.split(": ", 2);
            packet.mentions.put(s[0], s[1]);
            mention = in.nextLine();
        }

        // Data
        String dataLengthStr = packet.mentions.get(ServiceInfo.DATA_LENGTH_BYTES);
        if (dataLengthStr != null) {
            int dataLength = Integer.parseInt(dataLengthStr);
            StringBuilder sb = new StringBuilder(dataLength);

            in.useDelimiter("");
            while (sb.length() < dataLength)
                sb.append(in.next());

            packet.data = sb.toString();
        }

        // Blank line
        in.nextLine();

        System.out.println("RCVD: " + packet.getObjective());

        return packet;
    }

    static String readMention(String mention, String key) throws ProtocolViolationException {
        String[] s = mention.split(": ", 2);
        if (!s[0].equals(key))
            throw new ProtocolViolationException("Unexpected mention: " + s[0] + " instead of " + s[1]);

        return s[1];
    }

    static String makeMention(String key, String value) {
        return key + ": " + value;
    }

    AirPacket addMention(String key, String value) {
        mentions.put(key, value);
        return this;
    }

    AirPacket removeMention(String key) {
        mentions.remove(key);
        return this;
    }

    AirPacket setData(String data) {
        this.data = data;
        return this;
    }

    String getObjective() {
        return objective;
    }

    AirPacket setObjective(String objective) {
        this.objective = objective;
        return this;
    }

    String getIntent() throws ProtocolViolationException {
        if (type != Type.REQUEST)
            throw new ProtocolViolationException("Packet is of type: " + type);
        return objective;
    }

    String getResult() throws ProtocolViolationException {
        if (type != Type.RESPONSE)
            throw new ProtocolViolationException("Packet is of type: " + type);
        return objective;
    }

    void send(OutputStream rawOut) {
        PrintWriter out = new PrintWriter(new BufferedOutputStream(rawOut));
        out.println(protocol + " / " + type.toString());
        out.println();

        switch (type) {
            case REQUEST:
                out.println(makeMention(AirProtocol.INTENT_SPECIFIER, objective));
                break;
            case RESPONSE:
                out.println(makeMention(AirProtocol.RESPONSE_SPECIFIER, objective));
                break;
            case BROADCAST:
                out.println(makeMention(AirProtocol.BROADCAST_SPECIFIER, objective));
                break;
        }

        for (Map.Entry<String, String> entry : mentions.entrySet())
            out.println(entry.getKey() + ": " + entry.getValue());

        out.println();

        out.print(data);

        out.println();

        System.out.println("SENT: " + getObjective());

        out.flush();
    }

    enum Type {
        REQUEST,
        RESPONSE,
        BROADCAST
    }
}
