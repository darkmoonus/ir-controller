package vn.fpt.ircontroller.models;

/**
 * Created by hunter on 12/18/2015.
 */
public class Device {
    private String name;
    private DeviceType type;
    private String address;

    public Device(String name, DeviceType type, String address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public DeviceType getType() {
        return type;
    }
    public void setType(DeviceType type) {
        this.type = type;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
