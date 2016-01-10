package vn.fpt.ircontroller.interfaces;

/**
 * Created by hunter on 12/22/2015.
 */
public interface DialogScanBLEListener {
    public void onConnect(String address);
    public void onCancel();
}
