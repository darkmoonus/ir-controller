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
import vn.fpt.ircontroller.activities.DevicesActivity;
import vn.fpt.ircontroller.activities.HomeActivity;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.Room;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.DataObjectHolder> {
    private ArrayList<Room> dataSet;
    private HomeActivity mActivity;
    public RoomListAdapter(ArrayList<Room> myDataSet, HomeActivity mActivity) {
        this.mActivity = mActivity;
        this.dataSet = myDataSet;
    }

    @Override
    public int getItemViewType(int position) {
//        if(type.equals(DashboardDataType.ADS)) return 5;
        return -1;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_list, parent, false);
        return new DataObjectHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        final Room r = dataSet.get(position);
        holder.name.setText(r.getName());
        holder.deviceQuantity.setText(r.getDeviceList().size() + " " + mActivity.getResources().getString(R.string.devices));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Room delRoom = dataSet.get(position);
                deleteItem(position);
                mActivity.showSnackBar(v, mActivity.getResources().getString(R.string.delete) + " " + r.getName() + " " +
                                mActivity.getResources().getString(R.string.successful), mActivity.getResources().getString(R.string.undo),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addItem(delRoom, position);
                            }
                        });
            }
        });
    }

    public void addItem(Room dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
        mActivity.checkEmptyList();
        mActivity.saveRoomListSharedPreference();
    }

    public void addItem(Room dataObj) {
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
    public Room getItem(int index) {
        return dataSet.get(index);
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder{
        private TextView name, deviceQuantity;
        private ImageView delete;
        public DataObjectHolder(View itemView, int type) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            deviceQuantity = (TextView) itemView.findViewById(R.id.device_quantity);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, DevicesActivity.class);
                    i.putExtra("Position", getPosition() + "");
                    mActivity.startActivity(i);
                }
            });
        }
    }
}
