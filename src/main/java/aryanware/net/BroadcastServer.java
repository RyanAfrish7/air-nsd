package aryanware.net;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ryanafrish7
 * @since 20/09/16
 */
public abstract class BroadcastServer implements Runnable {

    private int rPort;
    private boolean isBroadcasting;
    private long millis;

    private DatagramSocket s;
    private Thread thread;

    protected BroadcastServer(int port, int receiverPort) throws SocketException {
        s = new DatagramSocket(port);
        s.setBroadcast(true);
        rPort = receiverPort;
    }

    protected BroadcastServer(int receiverPort) throws SocketException {
        s = new DatagramSocket();
        s.setBroadcast(true);
        rPort = receiverPort;
    }

    public void start(long waitingTimeMillis) {
        millis = waitingTimeMillis;
        if (!isBroadcasting) {
            isBroadcasting = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void start() {
        start(0);
    }

    public void stop(long millis) {
        try {
            if (isBroadcasting) {
                isBroadcasting = false;
                thread.join(millis);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void nextBroadcast() throws IOException, InterruptedException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        sendPacket(InetAddress.getLocalHost());

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            List<InterfaceAddress> addressList = ni.getInterfaceAddresses();

            for (InterfaceAddress i : addressList) {
                InetAddress address = i.getBroadcast();
                if (address != null) sendPacket(address);
            }
        }
    }

    private void sendPacket(InetAddress address) throws IOException, InterruptedException {
        DatagramPacket packet = streamPacket();
        packet.setAddress(address);
        packet.setPort(rPort);
        s.send(packet);
    }

    @Override
    public void run() {
        try {
            while (isBroadcasting) {
                nextBroadcast();
                Thread.sleep(millis);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract DatagramPacket streamPacket() throws InterruptedException;
}
