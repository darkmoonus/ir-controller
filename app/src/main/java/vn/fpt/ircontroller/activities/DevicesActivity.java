package vn.fpt.ircontroller.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.cores.CoreActivity;

public class DevicesActivity extends CoreActivity {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
    }

    @Override
    protected void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    @Override
    protected void initModels() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initAnimations() {

    }

    @Override
    public void onClick(View v) {

    }
}
