package vn.fpt.ircontroller.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import vn.fpt.ircontroller.R;
import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.cores.CoreActivity;
import vn.fpt.ircontroller.models.DeviceRemote;
import vn.fpt.ircontroller.models.DeviceType;

public class SplashActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        initModels();
        initListeners();
        initAnimations();

        getSupportActionBar().hide();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initDevicesData();
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }).start();
    }

    private HashMap<String, DeviceType> typeMap = new HashMap<>();
    public void initDevicesData() {
        typeMap.put("TV", DeviceType.TV);
        typeMap.put("AIR", DeviceType.AIR_CONDITIONER);

        try {
            String[] fileList = getAssets().list("data");
            for(String s : fileList) {
                initCmdMap("data/" + s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String removeBlank(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            if(i==0 || s.charAt(i) != ' ' || (s.charAt(i) == ' ' && s.charAt(i-1) != ' ')) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString().trim();
    }
    public String getDataFromHex(String data) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.parseInt(data.substring(4, 6), 16));
        sb.append(",");
        sb.append(Integer.parseInt(data.substring(2, 4), 16));
        return sb.toString();
    }
    public void initCmdMap(String path) {
        String brand = "UNDEFINE";
        DeviceType type = typeMap.get(path.split("_")[0]);
        HashMap<String, String> cmdMap = new HashMap<>();
        int vendorId = 0;
        int preDataBit = 0;
        int bits = 0;
        String predata = null;
        boolean beginCode = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(path), "UTF-8"));
            String mLine = reader.readLine();
            while (mLine != null) {
                if(mLine.contains("pre_data")) {
                    predata = removeBlank(mLine).split(" ")[1];
                }
                // vendor
                if(mLine.contains("name")) {
                    String name = removeBlank(mLine).split(" ")[1].toLowerCase();
                    if(name.contains("samsung")) {
                        brand = "SAMSUNG";
                        vendorId = 11;
                    } else
                    if(name.contains("lg")) {
                        brand = "LG";
                        vendorId = 12;
                    } else
                    if(name.contains("panasonic")) {
                        brand = "PANASONIC";
                        vendorId = 13;
                    }
                } else
                if(mLine.contains("pre_data_bits")) {
                    preDataBit = Integer.parseInt(removeBlank(mLine).split(" ")[1]);
                } else
                if(mLine.contains("bits")) {
                    bits = Integer.parseInt(removeBlank(mLine).split(" ")[1]);
                } else
                if(mLine.contains("begin codes")) {
                    beginCode = true;
                } else
                if(mLine.contains("end codes")) {
                    beginCode = false;
                } else
                if(beginCode) {
                    String mCode = removeBlank(mLine);
                    String[] arr = mCode.split(" ");
                    String codeName = arr[0];
                    String code = arr[1];

                    int sum = preDataBit + bits;
                    String cmd = vendorId + "," + sum + "," + getDataFromHex(code) + "," + getDataFromHex(predata) + "\n";
                    loge(codeName + "|" + cmd);
                    cmdMap.put(codeName, cmd);
                }
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        IRApplication.mDeviceRemoteList.add(new DeviceRemote(brand, type, cmdMap));
        loge("Load successful: " + vendorId + "|" + preDataBit + "|" + bits + "|" + predata);
    }

    @Override
    protected void initViews() {

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
