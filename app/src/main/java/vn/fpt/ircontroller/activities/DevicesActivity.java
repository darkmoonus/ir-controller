package vn.fpt.ircontroller.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.adapters.CustomButtonGridAdapter;
import vn.fpt.ircontroller.adapters.DeviceListAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreBLEActivity;
import vn.fpt.ircontroller.dialogs.DialogAddCustomButton;
import vn.fpt.ircontroller.interfaces.DialogAddCustomButtonListener;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.models.CustomButton;
import vn.fpt.ircontroller.models.Device;

public class DevicesActivity extends CoreBLEActivity {
    private String TAG = getClass().getSimpleName();
    private FloatingActionButton mAddDevice;
    private ImageView mSetting, mNaviagate;
    private TextView mTitle;
    public LinearLayout mEmptyView;

    public UltimateRecyclerView mListView;
    private DeviceListAdapter mListAdapter;
    private GridView mGrid;
    private CustomButtonGridAdapter mGridAdapter;
    private TextView mConnectedStatus;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        initViews();
        initModels();
        initListeners();
        initAnimations();

        // scna BLE saved address
        scanBLE(mConnectedStatus);
    }

    private int mScroll = -5;
    public void initListView() {
        mListAdapter = new DeviceListAdapter(mPosition, IRApplication.mRoomList.get(mPosition).getDeviceList(), DevicesActivity.this);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(DevicesActivity.this));
        mListView.setAdapter(mListAdapter);
        mListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                if (scrollY > mScroll) {
                    mAddDevice.hide();
                } else {
                    mAddDevice.show();
                }
                mScroll = scrollY;
            }
            @Override
            public void onDownMotionEvent() {

            }
            @Override
            public void onUpOrCancelMotionEvent(ObservableScrollState observableScrollState) {
                if (observableScrollState == ObservableScrollState.DOWN) {
                    loge("End Down");
                } else if (observableScrollState == ObservableScrollState.UP) {
                    loge("End Up");
                } else if (observableScrollState == ObservableScrollState.STOP) {
                    loge("Stop");
                }
            }
        });
    }


    @Override
    protected void initViews() {
        mAddDevice = (FloatingActionButton) findViewById(R.id.add);
        mSetting = (ImageView) findViewById(R.id.setting);
        mTitle = (TextView) findViewById(R.id.title);
        mNaviagate = (ImageView) findViewById(R.id.navigate);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mListView = (UltimateRecyclerView) findViewById(R.id.list);
        mGrid = (GridView) findViewById(R.id.grid);
        mConnectedStatus = (TextView) findViewById(R.id.connected_status);
    }

    @Override
    protected void initModels() {
        mPosition = Integer.parseInt(getIntent().getStringExtra("Position"));
        mTitle.setText(IRApplication.mRoomList.get(mPosition).getName());
        mConnectedStatus.setVisibility(View.VISIBLE);
        if(IRApplication.isConnected) {
            mConnectedStatus.setText(getResources().getString(R.string.connected));
        } else {
            mConnectedStatus.setText(getResources().getString(R.string.disconnected));
        }
        initListView();
        initGridView();
        checkEmptyList();
    }

    public void initGridView() {
        if(IRApplication.mRoomList.get(mPosition).getCustomButtonsList() == null ||
                IRApplication.mRoomList.get(mPosition).getCustomButtonsList().size() == 0 ) {
            IRApplication.mRoomList.get(mPosition).setCustomButtonsList(new ArrayList<CustomButton>());
            IRApplication.mRoomList.get(mPosition).getCustomButtonsList().add(new CustomButton("_@#@_",
                    new ArrayList<Device>(), new ArrayList<String>()));
        }
        final ArrayList<CustomButton> customButtonsList = IRApplication.mRoomList.get(mPosition).getCustomButtonsList();
        mGridAdapter = new CustomButtonGridAdapter(this, customButtonsList);
        mGrid.setAdapter(mGridAdapter);
        mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != mGridAdapter.getCount() - 1) {
                    showAddCustomButtonDialog(mPosition, mGridAdapter.getItem(position), true, new DialogAddCustomButtonListener() {
                        @Override
                        public void onYes(CustomButton c) {
                            mGridAdapter.getItem(position).setName(c.getName());
                            mGridAdapter.getItem(position).setCommandList(c.getCommandList());
                            mGridAdapter.getItem(position).setDevicesList(c.getDevicesList());
                            mGrid.setAdapter(mGridAdapter);
                            saveRoomListSharedPreference();
                        }

                        @Override
                        public void onNo() {

                        }

                        @Override
                        public void onDelete() {
                            mGridAdapter.removeItem(position);
                            mGrid.setAdapter(mGridAdapter);
                        }
                    });

                }
                return false;
            }
        });
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == customButtonsList.size() - 1) {
                    showAddCustomButtonDialog(mPosition, null, false, new DialogAddCustomButtonListener() {
                        @Override
                        public void onYes(CustomButton c) {
                            mGridAdapter.addItem(c);
                            mGrid.setAdapter(mGridAdapter);
                            saveRoomListSharedPreference();
                        }

                        @Override
                        public void onNo() {

                        }

                        @Override
                        public void onDelete() {

                        }
                    });
                } else {
                    if(IRApplication.isConnected) {
                        for (String s : mGridAdapter.getItem(position).getCommandList()) {
                            sendMessageToBLEDevice(s);
                        }
                    }
                }
            }
        });
    }

    public void checkEmptyList() {
        if(mListAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListeners() {
        mSetting.setOnClickListener(this);
        mNaviagate.setOnClickListener(this);
        mAddDevice.setOnClickListener(this);
    }

    @Override
    protected void initAnimations() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                showAddDeviceDialog(new DialogAddDeviceListener() {
                    @Override
                    public void onYes(Device d) {
                        mListAdapter.addItem(d);
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
            case R.id.navigate:
                finish();
                break;
            case R.id.setting:
                scanBLE(mConnectedStatus);
                break;
            default:
                break;
        }
    }

    private DialogFragment mDialog;
    public DialogFragment showAddCustomButtonDialog(final int mPos, final CustomButton c, final boolean canDelete, final DialogAddCustomButtonListener mListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removePreviousDialog();
                mDialog = DialogAddCustomButton.newInstance(mPos, c, canDelete, DevicesActivity.this, mListener);
                mDialog.show(getSupportFragmentManager(), TAG);
            }
        });
        return mDialog;
    }
}
