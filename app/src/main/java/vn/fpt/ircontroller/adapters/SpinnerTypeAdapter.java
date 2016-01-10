package vn.fpt.ircontroller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.cores.CoreActivity;

/**
 * Created by hunter on 12/22/2015.
 */
public class SpinnerTypeAdapter extends ArrayAdapter<String> {

    private ArrayList<String> objects;
    private CoreActivity context;

    public SpinnerTypeAdapter(CoreActivity context, int resourceId, ArrayList<String> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return objects.size()-1;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_spinner_type, parent, false);

        TextView name = (TextView) row.findViewById(R.id.name);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);

        String d = objects.get(position);
        name.setText(d);
        if(d.equals("TV")) icon.setImageResource(R.mipmap.tv); else
        if(d.equals("AIR_CONDITIONER")) icon.setImageResource(R.mipmap.air_conditioner);
        return row;
    }
}
