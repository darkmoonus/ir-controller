package vn.fpt.ircontroller.cores;

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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.UartService;
import vn.fpt.ircontroller.dialogs.DialogAddDevice;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.interfaces.DialogScanBLEListener;

/**
 * Created by hunter on 12/29/2015.
 */
public class CoreBLEActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        startBLEService();
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

    private String TAG = getClass().getSimpleName();
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
        if(IRApplication.mService != null) {
            IRApplication.mService.stopSelf();
            IRApplication.mService = null;
        }
        IRApplication.isConnected = false;
    }

    // BLE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    showToastLong("Bluetooth has turned on ");
                } else {
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

    @Override
    public void onClick(View v) {

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
                IRApplication.isConnected = false;
                TextView t = (TextView) findViewById(R.id.connected_status);
                if(t != null) {
                    t.setText(getResources().getString(R.string.disconnected));
                }
            }
        }
    };

    public void sendMessageToBLEDevice(String msg) {
        if (IRApplication.mService == null || !IRApplication.isConnected) {
            scanBLE(null);
        } else {
            try {
                IRApplication.mService.writeRXCharacteristic(msg.getBytes("UTF-8"));
                showToastLong("Send message " + msg);
                loge("Send message " + msg);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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

    public void scanBLE(final TextView status) {
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (!IRApplication.isConnected) {
                showScanBLEDialog(new DialogScanBLEListener() {
                    @Override
                    public void onConnect(String address) {
                        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                        mBLEStatus = BLEStatus.CONNECTING;
                        IRApplication.mService.connect(address);
                        IRApplication.isConnected = true;
                        if(status != null) {
                            status.setText(getResources().getString(R.string.connected));
                        }
                        showToastLong("Connected to: " + address);
                    }
                    @Override
                    public void onCancel() {

                    }
                });
            } else {
                if (mDevice != null) {
                    IRApplication.mService.disconnect();
                }
                if(status != null) {
                    status.setText(getResources().getString(R.string.disconnected));
                }
                IRApplication.isConnected = false;
                showToastLong("disconnected");
            }
        }
    }


    private DialogFragment mDialog;
    public DialogFragment showAddDeviceDialog(final DialogAddDeviceListener mListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removePreviousDialog();
                mDialog = DialogAddDevice.newInstance(CoreBLEActivity.this, mListener);
                mDialog.show(getSupportFragmentManager(), TAG);
            }
        });
        return mDialog;
    }
}
