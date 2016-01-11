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
 * Created by hunte on 1/11/2016.
 */
public class SpinnerTextOnlyAdapter extends ArrayAdapter<String> {
    private ArrayList<String> objects;
    private CoreActivity context;

    public SpinnerTextOnlyAdapter(CoreActivity context, int resourceId, ArrayList<String> objects) {
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
        return objects.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_spinner_text_only, parent, false);

        TextView name = (TextView) row.findViewById(R.id.name);

        String d = objects.get(position);
        name.setText(d);
        return row;
    }
}
