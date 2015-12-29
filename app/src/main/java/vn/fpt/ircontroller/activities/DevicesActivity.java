package vn.fpt.ircontroller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.adapters.CustomButtonGridAdapter;
import vn.fpt.ircontroller.adapters.DeviceListAdapter;
import vn.fpt.ircontroller.adapters.RoomListAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.customizes.MyAnimations;
import vn.fpt.ircontroller.interfaces.DialogAddCustomButtonListener;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.CustomButton;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceType;
import vn.fpt.ircontroller.models.Room;

public class DevicesActivity extends CoreActivity {

    private FloatingActionButton mAddDevice;
    private ImageView mSetting, mNaviagate;
    private TextView mTitle;
    public LinearLayout mEmptyView;

    public UltimateRecyclerView mListView;
    private DeviceListAdapter mListAdapter;
    private GridView mGrid;
    private CustomButtonGridAdapter mGridAdapter;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        initViews();
        initModels();
        initListeners();
        initAnimations();
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
    }

    @Override
    protected void initModels() {
        mPosition = Integer.parseInt(getIntent().getStringExtra("Position"));
        mTitle.setText(IRApplication.mRoomList.get(mPosition).getName());
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
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.label)).getText(), Toast.LENGTH_SHORT).show();
                if(position == customButtonsList.size()-1) {
                    showAddCustomButtonDialog(new DialogAddCustomButtonListener() {
                        @Override
                        public void onYes(CustomButton c) {
                            mGridAdapter.addItem(c);
                            mGrid.setAdapter(mGridAdapter);
                        }
                        @Override
                        public void onNo() {

                        }
                    });
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
//                scanBLE();
                break;
            default:
                break;
        }
    }
}
