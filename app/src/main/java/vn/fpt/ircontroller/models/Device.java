package vn.fpt.ircontroller.models;

import java.util.HashMap;

/**
 * Created by hunter on 12/18/2015.
 */
public class Device {
    private String name;
    private DeviceType type;
    private HashMap<String, String> commandMap;

    public Device(String name, DeviceType type, HashMap<String, String> commandMap) {
        this.name = name;
        this.type = type;
        this.commandMap = commandMap;
    }
    public HashMap<String, String> getCommandMap() {
        return commandMap;
    }
    public void setCommandMap(HashMap<String, String> commandMap) {
        this.commandMap = commandMap;
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
}
