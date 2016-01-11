package vn.fpt.ircontroller.adapters;

/**
 * Created by hunter on 10/23/2015.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.activities.DevicesActivity;
import vn.fpt.ircontroller.activities.HomeActivity;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.AddCustomButtonModel;
import vn.fpt.ircontroller.models.Device;
import vn.fpt.ircontroller.models.DeviceRemote;
import vn.fpt.ircontroller.models.Room;

public class AddCustomButtonListAdapter extends RecyclerView.Adapter<AddCustomButtonListAdapter.DataObjectHolder> {
    public ArrayList<AddCustomButtonModel> dataSet;
    public DevicesActivity mActivity;

    public AddCustomButtonListAdapter(ArrayList<AddCustomButtonModel> myDataSet, DevicesActivity mActivity) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_custom_button, parent, false);
        return new DataObjectHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        final AddCustomButtonModel r = dataSet.get(position);
        holder.info.setText(r.getDeviceName() + " | " + r.getCommand());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void addItem(AddCustomButtonModel dataObj, int index) {
        dataSet.add(index, dataObj);
        notifyItemInserted(index);
        mActivity.checkEmptyList();
        mActivity.saveRoomListSharedPreference();
    }

    public void addItem(AddCustomButtonModel dataObj) {
        dataSet.add(0, dataObj);
        notifyItemInserted(0);
    }

    public void deleteItem(int index) {
        if (index >= 0 && index <= dataSet.size() - 1) {
            dataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void clearData() {
        dataSet.clear();
    }

    public AddCustomButtonModel getItem(int index) {
        return dataSet.get(index);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        private TextView info;
        private ImageView edit;

        public DataObjectHolder(View itemView, int type) {
            super(itemView);
            info = (TextView) itemView.findViewById(R.id.info);
            edit = (ImageView) itemView.findViewById(R.id.edit);
        }
    }
}
