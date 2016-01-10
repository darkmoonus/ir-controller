package vn.fpt.ircontroller.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.interfaces.DialogScanBLEListener;

public class DialogScanBLE extends DialogFragment implements OnClickListener {
    public CoreActivity mContext;
    public LayoutInflater mInflater;
    protected Dialog mDialog;
    public DialogScanBLEListener mListener;


    private static final long serialVersionUID = 1L;
    private BluetoothAdapter mBluetoothAdapter;
    public static final String TAG = "DeviceListDialog";
    List<BluetoothDevice> deviceList;
    private DeviceAdapter deviceAdapter;
    Map<String, Integer> devRssiValues;
    private static final long SCAN_PERIOD = 3000;
    private Handler mHandler;
    private boolean mScanning;

    public Button mBtCancel;
    public ListView newDevicesListView;

    public DialogScanBLE() {

    }

    public static DialogScanBLE newInstance(CoreActivity context, DialogScanBLEListener listener) {
        DialogScanBLE dialog = new DialogScanBLE();
        dialog.mContext = context;
        dialog.mInflater = LayoutInflater.from(context);
        dialog.mListener = listener;
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(mContext);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = mInflater.inflate(R.layout.dialog_scan_ble, null);

        mHandler = new Handler();
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mContext.showToastLong(R.string.ble_not_support);
            dismiss();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            mContext.showToastLong(R.string.ble_not_support);
            dismiss();
        }

        initViews(view);
        initModels();
        initListener();
        initAnimation();

        mDialog.setContentView(view);
        return mDialog;
    }

    public void initModels() {
        deviceList = new ArrayList<BluetoothDevice>();
        deviceAdapter = new DeviceAdapter(mContext, deviceList);
        devRssiValues = new HashMap<String, Integer>();
        newDevicesListView.setAdapter(deviceAdapter);
        newDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = deviceList.get(position);
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mListener.onConnect(deviceList.get(position).getAddress());
                dismiss();
            }
        });
        scanLeDevice(true);
    }

    public void initViews(View view) {
        mBtCancel = (Button) view.findViewById(R.id.device_list_dialog_cancel);
        newDevicesListView = (ListView) view.findViewById(R.id.new_devices);
    }

    public void initListener() {
        mBtCancel.setOnClickListener(this);
    }


    public void initAnimation() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_list_dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addDevice(device, rssi);
                }
            });
        }
    };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;
        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
            deviceAdapter.notifyDataSetChanged();
        }
    }

    class DeviceAdapter extends BaseAdapter {
        public Context context;
        public List<BluetoothDevice> devices;
        public LayoutInflater inflater;

        public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewGroup vg;
            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.row_scan_ble, null);
            }

            BluetoothDevice device = devices.get(position);
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

            tvrssi.setVisibility(View.VISIBLE);
            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
            if (rssival != 0) {
                tvrssi.setText("Rssi = " + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.i(TAG, "device::" + device.getName());
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText(R.string.ble_paired);
                tvrssi.setVisibility(View.VISIBLE);
            } else {
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
            }
            return vg;
        }
    }
}
