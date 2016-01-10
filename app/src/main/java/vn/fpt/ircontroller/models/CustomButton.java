package vn.fpt.ircontroller.models;

import java.util.ArrayList;

/**
 * Created by hunter on 12/29/2015.
 */
public class CustomButton {
    private String name;
    private ArrayList<Device> devicesList;
    private ArrayList<String> commandList;

    public CustomButton(String name, ArrayList<Device> devicesList, ArrayList<String> commandList) {
        this.name = name;
        this.devicesList = devicesList;
        this.commandList = commandList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Device> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public ArrayList<String> getCommandList() {
        return commandList;
    }

    public void setCommandList(ArrayList<String> commandList) {
        this.commandList = commandList;
    }
}
