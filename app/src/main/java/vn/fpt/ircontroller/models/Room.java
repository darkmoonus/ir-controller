package vn.fpt.ircontroller.models;

import android.bluetooth.BluetoothClass;

import java.util.ArrayList;

/**
 * Created by hunter on 12/18/2015.
 */
public class Room {
    private String name;
    private ArrayList<Device> deviceList;
    public Room(String name, ArrayList<Device> deviceList) {
        this.name = name;
        this.deviceList = deviceList;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }
    public void setDeviceList(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
