package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.fpv.dji.MessageType;

public class WifiActivity extends DJIActivity implements View.OnClickListener {

    private View btnBack;

    private EditText etWifiName;
    private EditText etWifiPassword;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("WifiActivity", msg.what + "");
            String errDesc;
            Bundle bundle = msg.getData();
            switch(msg.what) {
                case MessageType.MSG_GET_WIFI_NAME_RESPONSE:

                    errDesc = bundle.getString("DJI_DESC", "");
                    if (errDesc.isEmpty()) {
                        String wifiSSId = bundle.getString("wifiSSId");
                        etWifiName.setText(wifiSSId);
                    } else {
                        Toast.makeText(WifiActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MessageType.MSG_SET_WIFI_NAME_RESPONSE:

                    errDesc = bundle.getString("DJI_DESC", "");
                    if (errDesc.isEmpty()) {
                        String wifiSSId = bundle.getString("wifiSSId");
                        etWifiName.setText(wifiSSId);
                    } else {
                        Toast.makeText(WifiActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE:

                    errDesc = bundle.getString("DJI_DESC", "");
                    if (errDesc.isEmpty()) {
                        String wifiPassword = bundle.getString("wifiPassword");
                        Log.d("WifiActivity", wifiPassword);
                        etWifiPassword.setText(wifiPassword);
                    } else {
                        Toast.makeText(WifiActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MessageType.MSG_SET_WIFI_PASSWORD_RESPONSE:

                    errDesc = bundle.getString("DJI_DESC", "");
                    if (errDesc.isEmpty()) {
                        String wifiPassword = bundle.getString("wifiPassword");
                        etWifiPassword.setText(wifiPassword);
                    } else {
                        Toast.makeText(WifiActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
    };

    private Messenger messenger = new Messenger(handler);


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

        registerDJIMessenger(MessageType.MSG_GET_WIFI_NAME_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_WIFI_NAME_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_WIFI_PASSWORD_RESPONSE, messenger);

        Message message = Message.obtain();
        message.what = MessageType.MSG_GET_WIFI_NAME;
        sendDJIMessage(message);

        Message message2 = Message.obtain();
        message2.what = MessageType.MSG_GET_WIFI_PASSWORD;
        sendDJIMessage(message2);

    }
    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_WIFI_NAME_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_WIFI_NAME_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_WIFI_PASSWORD_RESPONSE, messenger);
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
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_WIFI_NAME;
                    Bundle data = new Bundle();
                    data.putString("wifiSSId", v.getText().toString());
                    message.setData(data);
                    sendDJIMessage(message);
                } else if (id == R.id.et_wifi_password) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_WIFI_PASSWORD;
                    Bundle data = new Bundle();
                    data.putString("wifiPassword", v.getText().toString());
                    message.setData(data);
                    sendDJIMessage(message);
                }
            }
            return false;
        }
    };

}
