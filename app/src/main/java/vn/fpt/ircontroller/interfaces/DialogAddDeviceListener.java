package vn.fpt.ircontroller.interfaces;


import vn.fpt.ircontroller.cores.CoreInterface;

/**
 * Created by hunter on 12/19/2015.
 */
public interface DialogAddDeviceListener extends CoreInterface {
    public void onYes(String name);
    public void onNo();
}
