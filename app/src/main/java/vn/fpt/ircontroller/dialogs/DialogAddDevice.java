package vn.fpt.ircontroller.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.adapters.SpinnerBrandAdapter;
import vn.fpt.ircontroller.adapters.SpinnerTypeAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.cores.CoreBLEActivity;
import vn.fpt.ircontroller.customizes.MyEditText;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceRemote;

public class DialogAddDevice extends DialogFragment implements OnClickListener {
    public CoreBLEActivity mContext;
    public LayoutInflater mInflater;
    protected Dialog mDialog;
    public DialogAddDeviceListener mListener;
    protected MyEditText mEditText;
    public Button mBtYes;
    public Button mBtNo;

    private Spinner mTypeSpinner, mBrandSpinner;
    private String mTypeSelected, mBrandSelected;

    private TextView mTips;
    private LinearLayout mChoosePower, mLayout;
    private DeviceRemote mChosenDevice;

    public DialogAddDevice(CoreBLEActivity context, DialogAddDeviceListener listener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    public static DialogAddDevice newInstance(CoreBLEActivity context, DialogAddDeviceListener listener) {
        DialogAddDevice dialog = new DialogAddDevice(context, listener);
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
        View view = mInflater.inflate(R.layout.dialog_add_device, null);

        initViews(view);
        setViews();
        initModels();
        initOnClick();
        initAnimation();

        mDialog.setContentView(view);
        return mDialog;
    }

    public void reloadPowerButtons() {
        mTips.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
        mChoosePower.removeAllViews();
        if(mTypeSelected != getResources().getString(R.string.chooseType) && mBrandSelected != getResources().getString(R.string.chooseBrand)) {
            final ArrayList<DeviceRemote> deviceRemoteList = new ArrayList<>();
            final ArrayList<ImageView> powerList = new ArrayList<>();
            for(DeviceRemote d : IRApplication.mDeviceRemoteList) {
                if(d.getType().toString().equals(mTypeSelected) && d.getBrand().equals(mBrandSelected)) {
                    deviceRemoteList.add(d);
                    ImageView image = new ImageView(mContext);
                    image.setImageResource(R.mipmap.power);
                    LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(150, 150);
                    l.setMargins(10, 10, 10, 10);
                    image.setLayoutParams(l);
                    powerList.add(image);
                }
            }
            if(powerList.size() != 0) {
                mChosenDevice = deviceRemoteList.get(0);
            }
            for(int i=0; i<powerList.size(); i++) {
                final int ii = i;
                if(ii == 0 ) {
                    powerList.get(ii).setImageResource(R.mipmap.power);
                } else {
                    powerList.get(ii).setImageResource(R.mipmap.power_gray);
                }
                powerList.get(ii).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        powerList.get(ii).setImageResource(R.mipmap.power);
                        mChosenDevice = deviceRemoteList.get(ii);
                        mContext.showToastLong("Send: " + mChosenDevice.getCommandMap().get("KEY_POWER"));
                        mContext.sendMessageToBLEDevice(mChosenDevice.getCommandMap().get("KEY_POWER"));
                        for (int j = 0; j < powerList.size(); j++) {
                            if (j != ii) {
                                powerList.get(j).setImageResource(R.mipmap.power_gray);
                            }
                        }
                    }
                });
                mChoosePower.addView(powerList.get(i));
            }
            if(deviceRemoteList.size() != 0) {
                mTips.setVisibility(View.VISIBLE);
                mLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initModels() {
        final ArrayList<String> typeArr = new ArrayList<>();
        for(DeviceRemote d : IRApplication.mDeviceRemoteList) {
            if(!typeArr.contains(d.getType().toString())) {
                typeArr.add(d.getType().toString());
            }
        }
        typeArr.add(mContext.getResources().getString(R.string.chooseType));
        ArrayAdapter<String> typeSpinnerAdaper = new SpinnerTypeAdapter(mContext, R.layout.row_spinner_type, typeArr);
        mTypeSpinner.setAdapter(typeSpinnerAdaper);
        mTypeSpinner.setSelection(typeArr.size()-1);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mTypeSelected = mTypeSpinner.getSelectedItem().toString();
                reloadPowerButtons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        ArrayList<String> brandArr = new ArrayList<>();
        for(DeviceRemote d : IRApplication.mDeviceRemoteList) {
            if(!brandArr.contains(d.getBrand())) {
                brandArr.add(d.getBrand());
            }
        }
        brandArr.add(mContext.getResources().getString(R.string.chooseBrand));
        ArrayAdapter<String> brandSpinnerAdaper = new SpinnerBrandAdapter(mContext, R.layout.row_spinner_brand, brandArr);
        mBrandSpinner.setAdapter(brandSpinnerAdaper);
        mBrandSpinner.setSelection(brandArr.size()-1);
        mBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mBrandSelected = mBrandSpinner.getSelectedItem().toString();
                reloadPowerButtons();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void initViews(View view) {
        mEditText = (MyEditText) view.findViewById(R.id.device);
        mBtYes = (Button) view.findViewById(R.id.add);
        mBtNo = (Button) view.findViewById(R.id.cancel);
        mTypeSpinner = (Spinner) view.findViewById(R.id.type);
        mBrandSpinner = (Spinner) view.findViewById(R.id.brand);
        mTips = (TextView) view.findViewById(R.id.tips);
        mLayout = (LinearLayout) view.findViewById(R.id.layout);
        mChoosePower = (LinearLayout) view.findViewById(R.id.choose_power_button_block);
    }

    public void setViews() {

    }

    public void initOnClick() {
        mBtYes.setOnClickListener(this);
        mBtNo.setOnClickListener(this);
    }

    public void initAnimation() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                String s = mEditText.getText().toString().trim();
                if(!s.equals("") && mChosenDevice != null) {
                    mListener.onYes(new Device(s, mChosenDevice.getType(), mChosenDevice.getCommandMap()));
                    dismiss();
                } else {
                    mContext.showToastLong(getResources().getString(R.string.deviceNameEmpty));
                }
                break;
            case R.id.cancel:
                mListener.onNo();
                dismiss();
                break;
            default:
                break;
        }
    }
}
