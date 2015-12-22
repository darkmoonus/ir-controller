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
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.ble.UartService;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.DeviceRemote;
import vn.fpt.ircontroller.models.DeviceType;

public class SplashActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        initModels();
        initListeners();
        initAnimations();

        getSupportActionBar().hide();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initDevicesData();
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }).start();







        startBLEService();
    }

    private HashMap<String, DeviceType> typeMap = new HashMap<>();
    public void initDevicesData() {
        typeMap.put("TV", DeviceType.TV);
        typeMap.put("AIR", DeviceType.AIR_CONDITIONER);

        try {
            String[] fileList = getAssets().list("data");
            for(String s : fileList) {
                initCmdMap(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String removeBlank(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            if(i==0 || s.charAt(i) != ' ' || (s.charAt(i) == ' ' && s.charAt(i-1) != ' ')) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString().trim();
    }
    public String getDataFromHex(String data) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.parseInt(data.substring(4, 6), 16));
        sb.append(",");
        sb.append(Integer.parseInt(data.substring(2, 4), 16));
        return sb.toString();
    }
    public void initCmdMap(String path) {
        String brand = "UNDEFINE";
        DeviceType type = typeMap.get(path.split("_")[0]);
        HashMap<String, String> cmdMap = new HashMap<>();
        int vendorId = 0;
        int preDataBit = 0;
        int bits = 0;
        String predata = null;
        boolean beginCode = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("data/" + path), "UTF-8"));
            String mLine = reader.readLine();
            while (mLine != null) {
                if(mLine.contains("pre_data")) {
                    predata = removeBlank(mLine).split(" ")[1];
                }
                // vendor
                if(mLine.contains("name")) {
                    String name = removeBlank(mLine).split(" ")[1].toLowerCase();
                    if(name.contains("samsung")) {
                        brand = "SAMSUNG";
                        vendorId = 11;
                    } else
                    if(name.contains("lg")) {
                        brand = "LG";
                        vendorId = 12;
                    } else
                    if(name.contains("panasonic")) {
                        brand = "PANASONIC";
                        vendorId = 13;
                    } else
                    if(name.contains("huawei")) {
                        brand = "HUAWEI";
                        vendorId = 13;
                    }
                } else
                if(mLine.contains("pre_data_bits")) {
                    preDataBit = Integer.parseInt(removeBlank(mLine).split(" ")[1]);
                } else
                if(mLine.contains("bits")) {
                    bits = Integer.parseInt(removeBlank(mLine).split(" ")[1]);
                } else
                if(mLine.contains("begin codes")) {
                    beginCode = true;
                } else
                if(mLine.contains("end codes")) {
                    beginCode = false;
                } else
                if(beginCode) {
                    String mCode = removeBlank(mLine);
                    String[] arr = mCode.split(" ");
                    String codeName = arr[0];
                    String code = arr[1];

                    int sum = preDataBit + bits;
                    String cmd = vendorId + "," + sum + "," + getDataFromHex(code) + "," + getDataFromHex(predata) + "\n";
                    loge(codeName + "|" + cmd);
                    cmdMap.put(codeName, cmd);
                }
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        IRApplication.mDeviceRemoteList.add(new DeviceRemote(brand, type, cmdMap));
        loge("Load successful: " + vendorId + "|" + preDataBit + "|" + bits + "|" + predata);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initModels() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initAnimations() {

    }

    @Override
    public void onClick(View v) {

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
        mService.stopSelf();
        mService= null;
    }

    // BLE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String res = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(res);
                    mBLEStatus = BLEStatus.CONNECTING;
                    mService.connect(res);
                    showToastLong("Connected to: " + res);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
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
    public static final String TAG = "Main Activity";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;
    public String connectDisconnectTag = "Connect";
    protected UartService mService = null;
    private BluetoothDevice mDevice = null ;
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
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }
        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
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
//                         mRogobot = new Rogo(RogoActivity.this);
//                         onRogoConnected();
//                         Log.e(TAG, "Behavior loaded: " + mRogobot.toJSON());
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mBLEStatus = BLEStatus.NOTCONNECTED;
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
//                         onRogoDisconnected();
                        //setUiState();
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
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
//            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
//            	showToast("Device doesn't support UART. Disconnecting");
//            	mService.disconnect();
//            }
        }
    };
    public void sendMessageToBLEDevice(String msg) {
        if(mService == null) {
            showToastLong("Service not available");
            return;
        }
        try {
            mService.writeRXCharacteristic(msg.getBytes("UTF-8"));
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
            if (connectDisconnectTag.equals("Connect")){
                Intent newIntent = new Intent(SplashActivity.this, ChooseDeviceActivity.class);
                startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                connectDisconnectTag = "Disconnect";
            } else {
                connectDisconnectTag = "Connect";
                if (mDevice!=null) {
                    mService.disconnect();
                }
            }
        }
    }
}
