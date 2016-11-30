package aryanware.air;

import aryanware.net.BroadcastServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ryanafrish7 on 11/10/16.
 */
public class ServiceManager {

    static final String EXTRA_LAST_BROADCAST = "Last-Broadcast-At";
    static final long BROADCAST_QUANTUM = 2000;
    static final String PUBLISH_SERVICES = "PUBLISH_SERVICES";

    final Set<ServiceInfo> serviceInfos = new HashSet<>();

    private BroadcastServer broadcastServer;

    public void addService(ServiceInfo info) {
        synchronized (serviceInfos) {
            info.extras.put(EXTRA_LAST_BROADCAST, String.valueOf("0"));
            serviceInfos.add(info);
            serviceInfos.notifyAll();
        }
    }

    public void start() throws SocketException {
        broadcastServer = new BroadcastServer(AirProtocol.HOST_PORT) {

            @Override
            public DatagramPacket streamPacket() throws InterruptedException {
                AirPacket packet;

                synchronized (serviceInfos) {
                    while (serviceInfos.isEmpty()) serviceInfos.wait();

                    packet = new AirPacket(AirPacket.Type.BROADCAST);
                    packet.setObjective(PUBLISH_SERVICES);

                    StringWriter sw = new StringWriter();
                    PrintWriter out = new PrintWriter(sw);
                    for (ServiceInfo serviceInfo : serviceInfos) {
                        for (Map.Entry<String, String> entry : serviceInfo.map.entrySet())
                            out.println(entry.getKey() + ": " + entry.getValue());
                        out.println();
                        out.print(serviceInfo.getData());
                        out.println();
                    }
                    String data = sw.toString();
                    packet.addMention(ServiceInfo.DATA_LENGTH_BYTES, String.valueOf(data.length()));
                    packet.setData(data);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                packet.send(out);

                return new DatagramPacket(out.toByteArray(), out.size());
            }
        };
        broadcastServer.start(1000);
    }

}
