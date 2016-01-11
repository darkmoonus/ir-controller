package vn.fpt.ircontroller.models;

/**
 * Created by darkmoonus on 1/11/16.
 */
public class AddCustomButtonModel {
    private String deviceName;
    private String command;

    public AddCustomButtonModel(String deviceName, String command) {
        this.deviceName = deviceName;
        this.command = command;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
