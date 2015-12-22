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
import vn.fpt.ircontroller.adapters.DeviceListAdapter;
import vn.fpt.ircontroller.adapters.RoomListAdapter;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.customizes.MyAnimations;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceType;
import vn.fpt.ircontroller.models.Room;

public class DevicesActivity extends CoreActivity {

    private FloatingActionButton mAddDevice;
    private LinearLayout mNormalBlock, mSearchBlock;
    private ImageView mSetting, mSearch, mDelText;
    private TextView mCancelSearch, mTitle;
    private EditText mSearchEdit;
    public LinearLayout mEmptyView;

    public UltimateRecyclerView mListView;
    private DeviceListAdapter mListAdapter;

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
        mListAdapter = new DeviceListAdapter(IRApplication.mRoomList.get(mPosition).getDeviceList(), DevicesActivity.this);
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
        mNormalBlock = (LinearLayout) findViewById(R.id.normal_block);
        mSearchBlock = (LinearLayout) findViewById(R.id.search_block);
        mDelText = (ImageView) findViewById(R.id.del_text);
        mSearch = (ImageView) findViewById(R.id.search_icon);
        mSetting = (ImageView) findViewById(R.id.setting);
        mTitle = (TextView) findViewById(R.id.title);
        mSearchEdit = (EditText) findViewById(R.id.search_edittext);
        mCancelSearch = (TextView) findViewById(R.id.cancel_search);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mListView = (UltimateRecyclerView) findViewById(R.id.list);
    }

    @Override
    protected void initModels() {
        mPosition = Integer.parseInt(getIntent().getStringExtra("Position"));
        mTitle.setText(IRApplication.mRoomList.get(mPosition).getName());
        mSearchEdit.setHint(getResources().getString(R.string.search_hint_activity_devices));
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        mDelText.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mCancelSearch.setOnClickListener(this);
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
            case R.id.cancel_search:
                hideKeyboard();
                mCancelSearch.setVisibility(View.GONE);
                mSearchBlock.setVisibility(View.GONE);
                mSetting.setVisibility(View.VISIBLE);
                mNormalBlock.setVisibility(View.VISIBLE);
                mSearchBlock.clearAnimation();
                mCancelSearch.clearAnimation();
                mSearchEdit.setText("");
                break;
            case R.id.del_text:
                mSearchEdit.setText("");
                break;
            case R.id.search_icon:
                showKeyboard();
                mSearchBlock.startAnimation(MyAnimations.fromLeft(1000, 300));
                mCancelSearch.startAnimation(MyAnimations.fromDown(200, 150));
                mCancelSearch.setVisibility(View.VISIBLE);
                mSearchBlock.setVisibility(View.VISIBLE);
                mSetting.setVisibility(View.GONE);
                mNormalBlock.setVisibility(View.GONE);
                mSearchEdit.requestFocus();
                break;
            case R.id.setting:
                scanBLE();
                break;
            default:
                break;
        }
    }
}
