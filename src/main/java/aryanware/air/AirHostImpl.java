package aryanware.air;

import java.io.IOException;

/**
 * @author ryanafrish7
 * @since 5/10/16
 */
public class AirHostImpl {

    aryanware.air.ServiceManager serviceManager = new ServiceManager();
    AirHostInterface I;

    AirHostImpl() throws IOException {
        I = new AirHostInterface(new AirHostInterface.RequestHandler() {
            @Override
            public void handle(AirPacket req, AirPacket res, AirHostInterface.AirConnection conn) {
                res.setObjective(AirProtocol.RESPONSE_UNRECOGNIZED);
            }
        });

        I.setRequestHandler(AirProtocol.INTENT_HELLO_AIR, new AirHostInterface.RequestHandler() {
            @Override
            public void handle(AirPacket req, AirPacket res, AirHostInterface.AirConnection conn) {
                res.setObjective(AirProtocol.RESPONSE_OK);
            }
        });

        I.setRequestHandler(AirProtocol.INTENT_REGISTER_SERVICE, new AirHostInterface.RequestHandler() {

            ServiceInfo getServiceInfo(AirPacket packet) {
                ServiceInfo info = new ServiceInfo(
                        packet.mentions.get(ServiceInfo.NAME),
                        Service.ServiceType.valueOf(packet.mentions.get(ServiceInfo.TYPE))
                );

                info.map.put(ServiceInfo.LAST_AVAILABLE_AT, String.valueOf(System.currentTimeMillis()));
                info.setData(packet.data);

                return info;
            }

            @Override
            public void handle(AirPacket req, AirPacket res, AirHostInterface.AirConnection conn) {
                serviceManager.addService(getServiceInfo(req));
                res.setObjective(AirProtocol.RESPONSE_OK);
            }
        });

        I.setRequestHandler(AirProtocol.INTENT_BYE_AIR, new AirHostInterface.RequestHandler() {
            @Override
            public void handle(AirPacket req, AirPacket res, AirHostInterface.AirConnection conn) {
                conn.close();
            }
        });

        serviceManager.start();

        I.listen();
    }

    public static void main(String[] args) throws Exception {
        new AirHostImpl();
    }
}
