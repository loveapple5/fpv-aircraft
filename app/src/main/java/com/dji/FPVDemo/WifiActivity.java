package com.dji.FPVDemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.products.DJIAircraft;

public class WifiActivity extends FragmentActivity implements View.OnClickListener {

    protected static final int MSG_GET_WIFI_SSID = 1;
    protected static final int MSG_GET_WIFI_PASSWORD = 2;

    private View btnBack;

    private EditText etWifiName;
    private EditText etWifiPassword;

    private DJIWiFiLink djiWiFiLink;

    protected Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_WIFI_SSID:
                    String WiFiSSID = msg.getData().getString("WiFiSSID");
                    etWifiName.setText(WiFiSSID);
                    break;
                case MSG_GET_WIFI_PASSWORD:
                    String WiFiPassword = msg.getData().getString("WiFiPassword");
                    etWifiPassword.setText(WiFiPassword);
                    break;
                default:
                    break;
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_wifi);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        etWifiName = (EditText) findViewById(R.id.et_wifi_name);
        etWifiName.setOnEditorActionListener(etListener);
        etWifiPassword = (EditText) findViewById(R.id.et_wifi_password);
        etWifiPassword.setOnEditorActionListener(etListener);

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        DJIAirLink djiAirLink = djiAircraft.getAirLink();
        if (djiAirLink != null && djiAirLink.getWiFiLink() != null) {
            djiWiFiLink = djiAirLink.getWiFiLink();
            djiWiFiLink.getWiFiSSID(new getWiFiSSIDcallback());
            djiWiFiLink.getWiFiPassword(new getWiFiPasswordCallback());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    private TextView.OnEditorActionListener etListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //按下确认的时候进行处理
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                int id = v.getId();
                if (id == R.id.et_wifi_name) {
                    djiWiFiLink.setWiFiSSID(v.getText().toString(), null);

                } else if (id == R.id.et_wifi_password) {
                    djiWiFiLink.setWiFiPassword(v.getText().toString(), null);
                }
            }
            return false;
        }
    };

    class getWiFiSSIDcallback implements DJICommonCallbacks.DJICompletionCallbackWith<String> {
        @Override
        public void onSuccess(String djiWiFiSSID) {
            StringBuilder sb = new StringBuilder();
            sb.append(djiWiFiSSID);
            Bundle bundle = new Bundle();
            bundle.putString("WiFiSSID", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_GET_WIFI_SSID;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    ;

    class getWiFiPasswordCallback implements DJICommonCallbacks.DJICompletionCallbackWith<String> {
        @Override
        public void onSuccess(String djiWiFiPassword) {
            StringBuilder sb = new StringBuilder();
            sb.append(djiWiFiPassword);
            Bundle bundle = new Bundle();
            bundle.putString("WiFiPassword", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_GET_WIFI_PASSWORD;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    ;
}
