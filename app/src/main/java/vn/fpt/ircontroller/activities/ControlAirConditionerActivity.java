package vn.fpt.ircontroller.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.ble.UartService;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.cores.CoreBLEActivity;
import vn.fpt.ircontroller.interfaces.DialogScanBLEListener;
import vn.fpt.ircontroller.models.Device;

public class ControlAirConditionerActivity extends CoreBLEActivity {
    private String TAG = getClass().getSimpleName();
    private ImageView power;
    private ImageView fanUp, fanDown, tempUp, tempDown;
    private Device mRemoteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_air_conditioner);

        initViews();
        initModels();
        initListeners();
        initAnimations();

    }

    @Override
    protected void initViews() {
        power = (ImageView) findViewById(R.id.power);
        fanDown = (ImageView) findViewById(R.id.fan_down);
        fanUp = (ImageView) findViewById(R.id.fan_up);
        tempDown = (ImageView) findViewById(R.id.temp_down);
        tempUp = (ImageView) findViewById(R.id.temp_up);
    }

    @Override
    protected void initModels() {
        Intent i = getIntent();
        String[] arr = i.getStringExtra("Position").split("_");
        mRemoteDevice = IRApplication.mRoomList.get(Integer.parseInt(arr[0])).getDeviceList().get(Integer.parseInt(arr[1]));
//        if (IRApplication.mService == null) {
//            scanBLE();
//        }
    }

    @Override
    protected void initListeners() {
        power.setOnClickListener(this);
        fanDown.setOnClickListener(this);
        fanUp.setOnClickListener(this);
        tempUp.setOnClickListener(this);
        tempDown.setOnClickListener(this);
    }

    @Override
    protected void initAnimations() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.power:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_POWER"));
                break;
            case R.id.fan_up:
//                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_FAN_UP"));
                break;
            case R.id.fan_down:
//                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_FAN_DOWN"));
                break;
            case R.id.temp_up:
//                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_TEMP_UP"));
                break;
            case R.id.temp_down:
//                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_TEMP_DOWN"));
                break;
            default:
                break;
        }
    }
}
