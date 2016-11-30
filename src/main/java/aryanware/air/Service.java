package aryanware.air;

import java.io.IOException;

/**
 * Created by ryanafrish7 on 1/10/16.
 */
public class Service {

    private AirInterface air;
    private ServiceInfo info;

    public Service(String name, ServiceType type) throws IOException {
        info = new ServiceInfo(name, type);
        air = AirInterface.getInstance();
        air.registerService(info);
        start();
    }

    public void start() {
    }

    public void stop() throws IOException {
        air.disconnect();
    }

    public void onRequested() {
    }

    public enum ServiceType {
        DIRECT_LOCAL
    }

}
