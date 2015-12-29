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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.adapters.RoomListAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.customizes.MyAnimations;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.CustomButton;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceType;
import vn.fpt.ircontroller.models.Room;

public class HomeActivity extends CoreActivity {

    private FloatingActionButton mAddRoom;
    private ImageView mSetting, mNaviagate;
    private TextView mTitle;
    public LinearLayout mEmptyView;

    public UltimateRecyclerView mListView;
    private RoomListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initModels();
        initListeners();
        initAnimations();
    }

    private int mScroll = -5;
    public void initListView() {
        mListAdapter = new RoomListAdapter(IRApplication.mRoomList, HomeActivity.this);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        mListView.setAdapter(mListAdapter);
        mListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                if (scrollY > mScroll) {
                    mAddRoom.hide();
                } else {
                    mAddRoom.show();
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
        mAddRoom = (FloatingActionButton) findViewById(R.id.add);
        mSetting = (ImageView) findViewById(R.id.setting);
        mNaviagate = (ImageView) findViewById(R.id.navigate);
        mTitle = (TextView) findViewById(R.id.title);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mListView = (UltimateRecyclerView) findViewById(R.id.room_list);
    }

    @Override
    protected void initModels() {
        mTitle.setText(getResources().getString(R.string.title_activity_home));
        mNaviagate.setImageResource(R.mipmap.menu);
        mSetting.setVisibility(View.GONE);
        readRoomListSharedPreference();
        initListView();
        checkEmptyList();
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
        mAddRoom.setOnClickListener(this);
    }

    @Override
    protected void initAnimations() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                showAddRoomDialog(new DialogAddRoomListener() {
                    @Override
                    public void onYes(String name) {
                        mListAdapter.addItem(new Room(name, new ArrayList<Device>(), new ArrayList<CustomButton>()));
                        mAddRoom.show();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
            case R.id.navigate:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAddRoom.show();
        mListAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
