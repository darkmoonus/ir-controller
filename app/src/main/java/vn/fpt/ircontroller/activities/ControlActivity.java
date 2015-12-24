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
import vn.fpt.ircontroller.interfaces.DialogScanBLEListener;
import vn.fpt.ircontroller.models.Device;

public class ControlActivity extends CoreActivity {
    private String TAG = getClass().getSimpleName();


    private ImageView power;
    private TextView code1, code2, code3, code4, code5, code6, code7, code8, code9, mute, volumeUp, volumeDown, channelUp, channelDown;
    private Device mRemoteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tv);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        startBLEService();

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
        mute = (TextView) findViewById(R.id.mute);
        volumeUp = (TextView) findViewById(R.id.volumn_up);
        volumeDown = (TextView) findViewById(R.id.volumn_down);
        channelUp = (TextView) findViewById(R.id.channel_up);
        channelDown = (TextView) findViewById(R.id.channel_down);
    }

    @Override
    protected void initModels() {
        Intent i = getIntent();
        String[] arr = i.getStringExtra("Position").split("_");
        mRemoteDevice = IRApplication.mRoomList.get(Integer.parseInt(arr[0])).getDeviceList().get(Integer.parseInt(arr[1]));
        if (IRApplication.mService == null) {
            scanBLE();
        }
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
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        IRApplication.mService.stopSelf();
        IRApplication.mService = null;
    }

    // BLE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case REQUEST_SELECT_DEVICE:
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    String res = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
//                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(res);
//                    mBLEStatus = BLEStatus.CONNECTING;
//                    IRApplication.mService.connect(res);
//                    connectDisconnectTag = "Disconnect";
//                    showToastLong("Connected to: " + res);
//                }
//                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    showToastLong("Bluetooth has turned on ");
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    showToastLong("Problem in BT Turning ON ");
                    finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    public enum BLEStatus {
        CONNECTED, NOTCONNECTED, CONNECTING;
    }

    public BLEStatus mBLEStatus = BLEStatus.NOTCONNECTED;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;
    public String connectDisconnectTag = "Connect";
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showToastLong("Response: " + msg);
        }
    };

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            IRApplication.mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + IRApplication.mService);
            if (!IRApplication.mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            IRApplication.mService = null;
        }
    };
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_CONNECT_MSG");
                        mBLEStatus = BLEStatus.CONNECTED;
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mBLEStatus = BLEStatus.NOTCONNECTED;
                        mState = UART_PROFILE_DISCONNECTED;
                        IRApplication.mService.close();
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                if (IRApplication.mService != null)
                    IRApplication.mService.enableTXNotification();
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
//                         	showToast("Respones: " + currentDateTimeString + " " + text);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            // TO DO: ?????????????????
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showToastLong("Device doesn't support UART. Disconnecting");
                IRApplication.mService.disconnect();
            }
        }
    };

    public void sendMessageToBLEDevice(String msg) {
        if (IRApplication.mService == null) {
            showToastLong("Service not available");
            return;
        }
        try {
            IRApplication.mService.writeRXCharacteristic(msg.getBytes("UTF-8"));
            showToastLong("Send message " + msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    public void startBLEService() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            showToastLong("Bluetooth is not available");
            finish();
            return;
        }
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    public void scanBLE() {
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (connectDisconnectTag.equals("Connect")) {
//                Intent newIntent = new Intent(this, ChooseDeviceActivity.class);
//                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
//                connectDisconnectTag = "Disconnect";
				showScanBLEDialog(new DialogScanBLEListener() {
					@Override
					public void onConnect(String address) {
						mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
						mBLEStatus = BLEStatus.CONNECTING;
						IRApplication.mService.connect(address);
						connectDisconnectTag = "Disconnect";
						showToastLong("Connected to: " + address);
					}
					@Override
					public void onCancel() {

					}
				});
            } else {
                connectDisconnectTag = "Connect";
                if (mDevice != null) {
                    IRApplication.mService.disconnect();
                }
            }
        }
    }
}
