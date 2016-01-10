package vn.fpt.ircontroller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreBLEActivity;
import vn.fpt.ircontroller.models.Device;

public class ControlActivity extends CoreBLEActivity {
    private String TAG = getClass().getSimpleName();
    private ImageView power;
    private TextView code1, code2, code3, code4, code5, code6, code7, code8, code9, name;
    private ImageView volumeUp, volumeDown, channelUp, channelDown, up, down, left, right, enter, mute;
    private Device mRemoteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tv);

        initViews();
        initModels();
        initListeners();
        initAnimations();

    }

    @Override
    protected void initViews() {
        power = (ImageView) findViewById(R.id.power);
        code1 = (TextView) findViewById(R.id.code_1);
        code2 = (TextView) findViewById(R.id.code_2);
        code3 = (TextView) findViewById(R.id.code_3);
        code4 = (TextView) findViewById(R.id.code_4);
        code5 = (TextView) findViewById(R.id.code_5);
        code6 = (TextView) findViewById(R.id.code_6);
        code7 = (TextView) findViewById(R.id.code_7);
        code8 = (TextView) findViewById(R.id.code_8);
        code9 = (TextView) findViewById(R.id.code_9);
        mute = (ImageView) findViewById(R.id.mute);
        volumeUp = (ImageView) findViewById(R.id.volumn_up);
        volumeDown = (ImageView) findViewById(R.id.volumn_down);
        channelUp = (ImageView) findViewById(R.id.channel_up);
        channelDown = (ImageView) findViewById(R.id.channel_down);
        up = (ImageView) findViewById(R.id.up);
        down = (ImageView) findViewById(R.id.down);
        enter = (ImageView) findViewById(R.id.enter);
        left = (ImageView) findViewById(R.id.left);
        right = (ImageView) findViewById(R.id.right);
        name = (TextView) findViewById(R.id.device_name);
    }

    @Override
    protected void initModels() {
        Intent i = getIntent();
        String[] arr = i.getStringExtra("Position").split("_");
        mRemoteDevice = IRApplication.mRoomList.get(Integer.parseInt(arr[0])).getDeviceList().get(Integer.parseInt(arr[1]));
        name.setText(mRemoteDevice.getName());
//        if (IRApplication.mService == null) {
//            scanBLE();
//        }
    }

    @Override
    protected void initListeners() {
        power.setOnClickListener(this);
        code1.setOnClickListener(this);
        code2.setOnClickListener(this);
        code3.setOnClickListener(this);
        code4.setOnClickListener(this);
        code5.setOnClickListener(this);
        code6.setOnClickListener(this);
        code7.setOnClickListener(this);
        code8.setOnClickListener(this);
        code9.setOnClickListener(this);
        mute.setOnClickListener(this);
        volumeUp.setOnClickListener(this);
        volumeDown.setOnClickListener(this);
        channelUp.setOnClickListener(this);
        channelDown.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        enter.setOnClickListener(this);
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
            case R.id.code_1:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_1"));
                break;
            case R.id.code_2:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_2"));
                break;
            case R.id.code_3:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_3"));
                break;
            case R.id.code_4:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_4"));
                break;
            case R.id.code_5:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_5"));
                break;
            case R.id.code_6:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_6"));
                break;
            case R.id.code_7:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_7"));
                break;
            case R.id.code_8:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_8"));
                break;
            case R.id.code_9:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_9"));
                break;
            case R.id.mute:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_MUTE"));
                break;
            case R.id.volumn_up:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_VOLUMEUP"));
                break;
            case R.id.volumn_down:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_VOLUMEDOWN"));
                break;
            case R.id.channel_up:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_CHANNELUP"));
                break;
            case R.id.channel_down:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_CHANNELDOWN"));
                break;
            case R.id.up:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_UP"));
                break;
            case R.id.down:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_DOWN"));
                break;
            case R.id.left:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_LEFT"));
                break;
            case R.id.right:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_RIGHT"));
                break;
            case R.id.enter:
                sendMessageToBLEDevice(mRemoteDevice.getCommandMap().get("KEY_ENTER"));
                break;
            default:
                break;
        }
    }
}
