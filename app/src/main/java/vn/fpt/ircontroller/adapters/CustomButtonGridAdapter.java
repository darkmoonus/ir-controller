package vn.fpt.ircontroller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.CustomButton;

/**
 * Created by hunte on 12/29/2015.
 */
public class CustomButtonGridAdapter extends BaseAdapter {
    private CoreActivity mActivity;
    private ArrayList<CustomButton> dataSet;

    public CustomButtonGridAdapter(CoreActivity mActivity, ArrayList<CustomButton> dataSet) {
        this.mActivity = mActivity;
        this.dataSet = dataSet;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(mActivity);
            gridView = inflater.inflate(R.layout.item_custom_button, null);

            // set value into textview
            TextView textView = (TextView) gridView.findViewById(R.id.label);
            String txt = dataSet.get(position).getName().trim();
            textView.setText(txt.equals("_@#@_") ? "ADD" : txt);
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
    public void removeItem(int index) {
        dataSet.remove(index);
        notifyDataSetChanged();
        mActivity.saveRoomListSharedPreference();
    }
    public void addItem(CustomButton c) {
        dataSet.add(0, c);
        notifyDataSetChanged();
        mActivity.saveRoomListSharedPreference();
    }
    @Override
    public int getCount() {
        return dataSet.size();
    }
    @Override
    public CustomButton getItem(int position) {
        return dataSet.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
}
