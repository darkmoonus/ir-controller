package vn.fpt.ircontroller.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreActivity;

public class ControlActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initModels();
        initListeners();
        initAnimations();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initModels() {
        if(IRApplication.mService == null) {
            scanBLE();
        }
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
