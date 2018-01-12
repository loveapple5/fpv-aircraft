package com.synseaero.fpv;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.synseaero.dji.MessageType;
import com.synseaero.view.WaveView;

public class ConnectActivity extends DJIActivity implements View.OnClickListener {

    private static final String TAG = ConnectActivity.class.getName();

    private WaveView wcConnect;
    private ImageView ivLauncher;
    private View btnSetting;
    private View btnPrepareFlight;
    private TextView tvModel;

//    private static final String SKIN_NAME = "blackfantacy";
//    private static final String SKIN_DIR = Environment
//            .getExternalStorageDirectory() + File.separator + SKIN_NAME;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            //识别出当前产品
            if (what == MessageType.MSG_PRODUCT_CHANGED) {
                Bundle bundle = msg.getData();
                boolean hasProduct = bundle.getBoolean("hasProduct", false);
                String productName = bundle.getString("productName", "");
                if (!hasProduct) {
                    btnSetting.setEnabled(false);
                    btnPrepareFlight.setEnabled(false);
                    tvModel.setText(R.string.connecting);
                } else {
                    btnSetting.setEnabled(true);
                    btnPrepareFlight.setEnabled(true);
                    tvModel.setText(productName);
                }
            }
            //产品连接状态改变
            else if (what == MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED) {
                Bundle bundle = msg.getData();
                String productName = bundle.getString("productName", "");
                boolean isConnected = bundle.getBoolean("isConnected");
                if (!isConnected) {
                    btnSetting.setEnabled(false);
                    btnPrepareFlight.setEnabled(false);
                    tvModel.setText(R.string.connecting);
                } else {
                    btnSetting.setEnabled(true);
                    btnPrepareFlight.setEnabled(true);
                    tvModel.setText(productName);
                }
            }

        }
    };

    private Messenger messenger = new Messenger(handler);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_connect);
        ivLauncher = (ImageView) findViewById(R.id.iv_launcher);
        wcConnect = (WaveView) findViewById(R.id.wv_connect);
        wcConnect.setInitialRadius(150);
        wcConnect.setMaxRadiusRate(1);
        wcConnect.setDuration(5000);
        wcConnect.setStyle(Paint.Style.FILL);
        wcConnect.start();

        btnSetting = findViewById(R.id.tv_setting);
        btnSetting.setOnClickListener(this);

        btnPrepareFlight = findViewById(R.id.tv_prepare_flight);
        btnPrepareFlight.setOnClickListener(this);

        tvModel = (TextView) findViewById(R.id.tv_craft_model);
//        tvModel.setOnClickListener(this);

        handler.postDelayed(hideLauncher, 3000);

        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVApplication.DJI_SERVICE_CONNECTED);
        registerReceiver(mReceiver, filter);
        //((FPVDemoApplication) getApplication()).notifyStatusChange();

        registerDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
        registerDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);

    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //onCreate的时候注册有可能失败
            registerDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
            registerDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);

            Message message = Message.obtain();
            message.what = MessageType.MSG_REGISTER_SDK;
            sendDJIMessage(message);

        }
    };

    private Runnable hideLauncher = new Runnable() {
        @Override
        public void run() {
            ivLauncher.setImageDrawable(null);
            ivLauncher.setVisibility(View.GONE);
        }
    };

    private int i = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting: {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.tv_prepare_flight: {
//                Intent fpvIntent = new Intent(this, BluetoothActivity.class);
//                startActivity(fpvIntent);
//                Intent fpvIntent = new Intent(this, FPVActivity.class);
//                startActivity(fpvIntent);
                Intent prepareIntent = new Intent(this, PrepareActivity.class);
                startActivity(prepareIntent);
                break;
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
        unregisterDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);
    }

}
