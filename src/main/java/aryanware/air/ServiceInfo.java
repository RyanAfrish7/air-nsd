package aryanware.air;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ryanafrish7
 * @since 20/9/16
 */
class ServiceInfo {

    static final String NAME = "Service-Name";
    static final String TYPE = "Service-Type";
    static final String LAST_AVAILABLE_AT = "Last-Available-At";
    static final String DATA_LENGTH_BYTES = "Data-Length";
    Map<String, String> map = new HashMap<>();
    Map<String, String> extras = new HashMap<>();
    String data;
    private long lastAvailableAt;

    ServiceInfo(String name, Service.ServiceType type) {
        map.put(NAME, name);
        map.put(TYPE, type.toString());
        map.put(LAST_AVAILABLE_AT, "0");
        map.put(DATA_LENGTH_BYTES, "0");
    }

    String getName() {
        return map.get(NAME);
    }

    void setName(String name) {
        map.put(NAME, name);
    }

    Service.ServiceType getType() {
        return Service.ServiceType.valueOf(map.get(TYPE));
    }

    void setType(Service.ServiceType type) {
        map.put(TYPE, type.toString());
    }

    long getLastAvailableAt() {
        return Long.parseLong(map.get(LAST_AVAILABLE_AT));
    }

    void setLastAvailableAt(long timeStamp) {
        map.put(LAST_AVAILABLE_AT, String.valueOf(timeStamp));
    }

    String getData() {
        return data;
    }

    void setData(String data) {
        this.data = data;
        map.put(DATA_LENGTH_BYTES, String.valueOf(data.length()));
    }

    @Override
    public int hashCode() {
        return map.get(NAME).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServiceInfo && this.hashCode() == obj.hashCode();
    }
}
