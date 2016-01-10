package vn.fpt.ircontroller.models;

import java.util.HashMap;

/**
 * Created by hunter on 12/22/2015.
 */
public class DeviceRemote {
    private String brand;
    private DeviceType type;
    private HashMap<String, String> commandMap;

    public DeviceRemote(String brand, DeviceType type, HashMap<String, String> commandMap) {
        this.brand = brand;
        this.type = type;
        this.commandMap = commandMap;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public HashMap<String, String> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(HashMap<String, String> commandMap) {
        this.commandMap = commandMap;
    }
}
