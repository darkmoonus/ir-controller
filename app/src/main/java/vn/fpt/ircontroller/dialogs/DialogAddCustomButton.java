package vn.fpt.ircontroller.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.activities.DevicesActivity;
import vn.fpt.ircontroller.adapters.AddCustomButtonListAdapter;
import vn.fpt.ircontroller.adapters.DeviceListAdapter;
import vn.fpt.ircontroller.adapters.SpinnerBrandAdapter;
import vn.fpt.ircontroller.adapters.SpinnerTextOnlyAdapter;
import vn.fpt.ircontroller.adapters.SpinnerTypeAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.customizes.MyEditText;
import vn.fpt.ircontroller.interfaces.DialogAddCustomButtonListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.AddCustomButtonModel;
import vn.fpt.ircontroller.models.CustomButton;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceRemote;

public class DialogAddCustomButton extends DialogFragment implements OnClickListener {
    public DevicesActivity mContext;
    public LayoutInflater mInflater;
    protected Dialog mDialog;
    public DialogAddCustomButtonListener mListener;
    protected MyEditText mEditText;
    public Button mBtYes, mBtNo, mDelete;

    private TextView mTitle;
    public CustomButton customButton;
    public boolean canDelete = false;

    public UltimateRecyclerView mListView;
    private AddCustomButtonListAdapter mListAdapter;

    private Spinner deviceName, command;
    private ImageView addItem;
    private int mPos;

    public DialogAddCustomButton() {

    }

    public DialogAddCustomButton(int mPos, CustomButton customButton, boolean canDelete, DevicesActivity context, DialogAddCustomButtonListener listener) {
        this.canDelete = canDelete;
        this.customButton = customButton;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
        this.mPos = mPos;
    }

    public static DialogAddCustomButton newInstance(int mPos, CustomButton customButton, boolean canDelete, DevicesActivity context, DialogAddCustomButtonListener listener) {
        DialogAddCustomButton dialog = new DialogAddCustomButton(mPos, customButton, canDelete, context, listener);
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
        View view = mInflater.inflate(R.layout.dialog_add_custom_button, null);

        initViews(view);
        setViews();
        initOnClick();
        initAnimation();

        initListView();
        initSpinner();

        mDialog.setContentView(view);
        return mDialog;
    }


    public void initListView() {
        ArrayList<AddCustomButtonModel> arr = new ArrayList<AddCustomButtonModel>();
        if (customButton != null && customButton.getDevicesList() != null) {
            for (int i = 0; i < customButton.getDevicesList().size(); i++) {
                arr.add(new AddCustomButtonModel(customButton.getDevicesList().get(i).getName(),
                        customButton.getCommandList().get(i)));
            }
        }
        mListAdapter = new AddCustomButtonListAdapter(arr, mContext);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setAdapter(mListAdapter);
    }

    public void initViews(View view) {
        mEditText = (MyEditText) view.findViewById(R.id.room);
        mBtYes = (Button) view.findViewById(R.id.add);
        mBtNo = (Button) view.findViewById(R.id.cancel);
        mDelete = (Button) view.findViewById(R.id.delete);
        mTitle = (TextView) view.findViewById(R.id.title);
        mListView = (UltimateRecyclerView) view.findViewById(R.id.list);
        deviceName = (Spinner) view.findViewById(R.id.device_name);
        command = (Spinner) view.findViewById(R.id.command);
        addItem = (ImageView) view.findViewById(R.id.addItem);

        if (canDelete) {
            mDelete.setVisibility(View.VISIBLE);
            mDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDelete();
                    dismiss();
                }
            });
            mTitle.setText(mContext.getResources().getString(R.string.change) + " " + customButton.getName());
            mBtYes.setText(mContext.getResources().getString(R.string.save));
            mEditText.setText(customButton.getName());
        }
    }

    public void setViews() {

    }

    public void initOnClick() {
        mBtYes.setOnClickListener(this);
        mBtNo.setOnClickListener(this);
        addItem.setOnClickListener(this);
    }

    public void initAnimation() {

    }

    private ArrayList<Device> deviceList = new ArrayList<>();
    private ArrayList<String> commandArr = new ArrayList<>();

    public void loadCommandFromDevice(String deivceName) {
        for (Device d : deviceList) {
            if (d.getName().equals(deivceName)) {
                for (HashMap.Entry<String, String> entry : d.getCommandMap().entrySet()) {
                    commandArr.add(entry.getKey());
                }
                ArrayAdapter<String> commandAdapter = new SpinnerTextOnlyAdapter(mContext, R.layout.row_spinner_text_only, commandArr);
                command.setAdapter(commandAdapter);
                break;
            }
        }
    }

    public void initSpinner() {
        deviceList = IRApplication.mRoomList.get(mPos).getDeviceList();
        final ArrayList<String> deviceArr = new ArrayList<>();
        for (Device d : deviceList) {
            deviceArr.add(d.getName());
        }
        if(deviceList.size() != 0) {
            loadCommandFromDevice(deviceList.get(0).getName());
        }

        ArrayAdapter<String> typeSpinnerAdaper = new SpinnerTextOnlyAdapter(mContext, R.layout.row_spinner_text_only, deviceArr);
        deviceName.setAdapter(typeSpinnerAdaper);
        deviceName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                loadCommandFromDevice(deviceName.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        ArrayAdapter<String> commandAdapter = new SpinnerTextOnlyAdapter(mContext, R.layout.row_spinner_text_only, commandArr);
        command.setAdapter(commandAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addItem:
                String dName = deviceName.getSelectedItem().toString();
                String cmd = command.getSelectedItem().toString();
                mListAdapter.addItem(new AddCustomButtonModel(dName, cmd));
                break;
            case R.id.add:
                String s = mEditText.getText().toString().trim();
                if (!s.equals("")) {
                    ArrayList<Device> deviceListX = new ArrayList<Device>();
                    ArrayList<String> commandListX = new ArrayList<String>();
                    for (AddCustomButtonModel a : mListAdapter.dataSet) {
                        for (Device dd : deviceList) {
                            if (dd.getName().equals(a.getDeviceName())) {
                                deviceListX.add(dd);
                                break;
                            }
                        }
                        commandListX.add(a.getCommand());
                    }
                    mListener.onYes(new CustomButton(s, deviceListX, commandListX));
                    dismiss();
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
