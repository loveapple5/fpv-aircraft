package com.dji.FPVDemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.products.DJIAircraft;

/**
 * Created by ZXW on 2017/2/26.
 */

public class WiFiLinkSettingFragment extends Fragment {

    protected static final int GET_WiFiSSID = 1;
    protected static final int GET_WiFiPassword=2;

    private DJIAircraft djiAircraft;
    private DJIWiFiLink djiWiFiLink;
    private DJIAirLink djiAirLink;

    private EditText etWiFiSSID;
    private EditText etWiFiPassword;
    private Button btWiFi;

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_WiFiSSID:
                    String WiFiSSID = msg.getData().getString("WiFiSSID");
                    etWiFiSSID.setText(WiFiSSID);
                    break;
                case GET_WiFiPassword:
                    String WiFiPassword=msg.getData().getString("WiFiPassword");
                    etWiFiPassword.setText(WiFiPassword);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_wifilink_setting, container, false);
        InitUI(view);
        InitDJI();
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    };

    private void InitUI(View view){
        etWiFiSSID = (EditText)view.findViewById(R.id.et_wifiSSID);
        etWiFiPassword=(EditText)view.findViewById(R.id.et_wifiPassword);
        btWiFi=(Button)view.findViewById(R.id.btn_submit_wifi);
        btWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                djiWiFiLink.setWiFiSSID(etWiFiSSID.getText().toString(),new setWiFiSSIDcallback());
                djiWiFiLink.setWiFiPassword(etWiFiPassword.getText().toString(),new setWiFiPasswordcallback());
            };
        });
//        etWiFiPassword.setText();

    }
    private void InitDJI(){

        //----------------------------B设置菜单--------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        //-------------------------B4数据连接设置------------------------------------
        djiAirLink=djiAircraft.getAirLink();
        djiWiFiLink=djiAirLink.getWiFiLink();
        djiWiFiLink.getWiFiSSID(new getWiFiSSIDcallback());
        djiWiFiLink.getWiFiPassword(new getWiFiPasswordCallback());

    }
    class getWiFiSSIDcallback implements DJICommonCallbacks.DJICompletionCallbackWith<String>{
        @Override
        public void onSuccess(String djiWiFiSSID) {
            StringBuilder sb = new StringBuilder();
            sb.append(djiWiFiSSID);
            Bundle bundle = new Bundle();
            bundle.putString("WiFiSSID", sb.toString());
            Message msg = Message.obtain();
            msg.what = GET_WiFiSSID;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };
    class getWiFiPasswordCallback implements DJICommonCallbacks.DJICompletionCallbackWith<String>{
        @Override
        public void onSuccess(String djiWiFiPassword) {
            StringBuilder sb = new StringBuilder();
            sb.append(djiWiFiPassword);
            Bundle bundle = new Bundle();
            bundle.putString("WiFiPassword", sb.toString());
            Message msg = Message.obtain();
            msg.what = GET_WiFiPassword;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };
    class setWiFiSSIDcallback implements DJICommonCallbacks.DJICompletionCallback{

        @Override
        public void onResult(DJIError djiError) {

        }
    };
    class setWiFiPasswordcallback implements DJICommonCallbacks.DJICompletionCallback{
        @Override
        public void onResult(DJIError djiError) {

        }
    }
};