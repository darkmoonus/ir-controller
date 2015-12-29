package vn.fpt.ircontroller.adapters;

/**
 * Created by hunter on 10/23/2015.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.activities.ControlActivity;
import vn.fpt.ircontroller.activities.ControlAirConditionerActivity;
import vn.fpt.ircontroller.activities.DevicesActivity;
import vn.fpt.ircontroller.activities.HomeActivity;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceType;
import vn.fpt.ircontroller.models.Room;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DataObjectHolder> {
    private ArrayList<Device> dataSet;
    private DevicesActivity mActivity;
    private int mDevicePosition;
    public DeviceListAdapter(int pos, ArrayList<Device> myDataSet, DevicesActivity mActivity) {
        this.mActivity = mActivity;
        this.dataSet = myDataSet;
        this.mDevicePosition = pos;
    }

    @Override
    public int getItemViewType(int position) {
        if(dataSet.get(position).getType().equals(DeviceType.AIR_CONDITIONER)) return 2;
        if(dataSet.get(position).getType().equals(DeviceType.TV)) return 1;
        return -1;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_list, parent, false);
        return new DataObjectHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        final Device d = dataSet.get(position);
        holder.name.setText(d.getName());
        DeviceType type = d.getType();
        if(type.equals(DeviceType.AIR_CONDITIONER)) {
            holder.avatar.setBackgroundResource(R.mipmap.air_conditioner);
        } else {
            holder.avatar.setBackgroundResource(R.mipmap.tv);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Device del = dataSet.get(position);
                deleteItem(position);
                mActivity.showSnackBar(v, mActivity.getResources().getString(R.string.delete) + " " + d.getName() + " " +
                                mActivity.getResources().getString(R.string.successful), mActivity.getResources().getString(R.string.undo),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addItem(del, position);
                            }
                        });
            }
        });
    }

    public void addItem(Device dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
        mActivity.checkEmptyList();
        mActivity.saveRoomListSharedPreference();
    }

    public void addItem(Device dataObj) {
        dataSet.add(0, dataObj);
        notifyItemInserted(0);
        mActivity.checkEmptyList();
        mActivity.saveRoomListSharedPreference();
    }

    public void deleteItem(int index) {
        if (index >= 0 && index <= dataSet.size()-1) {
            dataSet.remove(index);
            notifyItemRemoved(index);
            mActivity.checkEmptyList();
            mActivity.saveRoomListSharedPreference();
        }
    }

    public void clearData() {
        dataSet.clear();
        mActivity.checkEmptyList();
        mActivity.saveRoomListSharedPreference();
    }
    public Device getItem(int index) {
        return dataSet.get(index);
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView delete, avatar;
        public DataObjectHolder(View itemView, int type) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            switch (type) {
                case 1:
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mActivity, ControlActivity.class);
                            i.putExtra("Position", mDevicePosition + "_" + getPosition());
                            mActivity.startActivity(i);
                        }
                    });
                    break;
                case 2:
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mActivity, ControlAirConditionerActivity.class);
                            i.putExtra("Position", mDevicePosition + "_" + getPosition());
                            mActivity.startActivity(i);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
}
