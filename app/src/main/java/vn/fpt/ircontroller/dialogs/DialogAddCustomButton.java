package vn.fpt.ircontroller.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.customizes.MyEditText;
import vn.fpt.ircontroller.interfaces.DialogAddCustomButtonListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.CustomButton;
import vn.fpt.ircontroller.models.Device;

public class DialogAddCustomButton extends DialogFragment implements OnClickListener {
    public Context mContext;
    public LayoutInflater mInflater;
    protected Dialog mDialog;
    public DialogAddCustomButtonListener mListener;
    protected MyEditText mEditText;
    public Button mBtYes;
    public Button mBtNo;

    public DialogAddCustomButton() {

    }

    public DialogAddCustomButton(Context context, DialogAddCustomButtonListener listener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    public static DialogAddCustomButton newInstance(Context context, DialogAddCustomButtonListener listener) {
        DialogAddCustomButton dialog = new DialogAddCustomButton(context, listener);
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

        mDialog.setContentView(view);
        return mDialog;
    }

    public void initViews(View view) {
        mEditText = (MyEditText) view.findViewById(R.id.room);
        mBtYes = (Button) view.findViewById(R.id.add);
        mBtNo = (Button) view.findViewById(R.id.cancel);
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
                if(!s.equals("")) {
                    mListener.onYes(new CustomButton(s, new ArrayList<Device>(), new ArrayList<String>()));
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
