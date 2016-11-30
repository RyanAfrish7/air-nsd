package aryanware.air;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

/**
 * Created by ryanafrish7 on 4/10/16.
 */
public class AirInterface {

    private Socket socket;

    private InputStream rawIn;
    private OutputStream rawOut;

    private Thread thread;

    private AirInterface() {
    }

    static AirInterface getInstance() throws IOException {

        AirInterface instance = new AirInterface();

        instance.socket = new Socket(InetAddress.getLocalHost(), AirProtocol.HOST_PORT);
        instance.rawIn = instance.socket.getInputStream();
        instance.rawOut = instance.socket.getOutputStream();

        instance.boot();

        return instance;
    }

    private void boot() throws IOException {
        new AirPacket(AirPacket.Type.REQUEST).setObjective(AirProtocol.INTENT_HELLO_AIR).send(rawOut);

        if (!AirProtocol.RESPONSE_OK.equals(AirPacket.readFromStream(rawIn).getResult()))
            throw new IOException("Bad response");
    }

    void registerService(ServiceInfo info) throws IOException {
        AirPacket packet = new AirPacket(AirPacket.Type.REQUEST).setObjective(AirProtocol.INTENT_REGISTER_SERVICE);

        for (Map.Entry<String, String> entry : info.map.entrySet())
            packet.addMention(entry.getKey(), entry.getValue());
        packet.send(rawOut);

        if (!AirProtocol.RESPONSE_OK.equals(AirPacket.readFromStream(rawIn).getResult()))
            throw new IOException("Bad response");
    }

    void startDiscovery(OnDiscoveryListener listener) {
        thread = new Thread() {
            @Override
            public void run() {

            }
        };
    }

    void stopDiscovery() {

    }

    void disconnect() throws IOException {
        new AirPacket(AirPacket.Type.REQUEST).setObjective(AirProtocol.INTENT_BYE_AIR).send(rawOut);
        socket.close();
    }

    interface OnDiscoveryListener {
        boolean filter(ServiceInfo serviceInfo);

        void onDiscoveryListener();

        void onFoundListener();
    }

}