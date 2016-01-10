package vn.fpt.ircontroller.interfaces;


import vn.fpt.ircontroller.cores.CoreInterface;
import vn.fpt.ircontroller.models.Device;

/**
 * Created by hunter on 12/19/2015.
 */
public interface DialogAddDeviceListener extends CoreInterface {
    public void onYes(Device d);
    public void onNo();
}
