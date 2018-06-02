package com.synseaero.fpv;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.WaveView;

public class BluetoothActivity extends DJIActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = BluetoothActivity.class.getName();

    public static final int MSG_START_SCAN = 1;
    public static final int MSG_STOP_SCAN = 2;

    private ListView lvBluetooth;
    private BluetoothListAdapter mLeDeviceListAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    private WaveView wcConnect;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_START_SCAN:{
                    wcConnect.start();
                    break;
                }
                case MSG_STOP_SCAN:{
                    wcConnect.stop();
                    break;
                }
            }
        }
    };

    private HandlerThread mLeThread = new HandlerThread("LE_THREAD");

    private Handler mLeHandler = null;

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_bluetooth);
        lvBluetooth = (ListView) findViewById(R.id.lv_bluetooth);

        View llEmpty = findViewById(R.id.ll_bluetooth_empty);
        lvBluetooth.setEmptyView(llEmpty);

        mLeDeviceListAdapter = new BluetoothListAdapter();
        lvBluetooth.setAdapter(mLeDeviceListAdapter);
        lvBluetooth.setOnItemClickListener(this);
        findViewById(R.id.tv_search_bluetooth).setOnClickListener(this);

        wcConnect = (WaveView) findViewById(R.id.wv_connect);
        wcConnect.setInitialRadius(150);
        wcConnect.setMaxRadiusRate(1);
        wcConnect.setDuration(5000);
        wcConnect.setStyle(Paint.Style.FILL);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mLeThread.start();
        mLeHandler = new Handler(mLeThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_START_SCAN: {
                        if (mBluetoothAdapter != null) {
                            mBluetoothAdapter.enable();
                            mBluetoothAdapter.startLeScan(mLeScanCallback);
                            mHandler.sendEmptyMessage(MSG_START_SCAN);

                            mLeHandler.removeMessages(MSG_STOP_SCAN);
                            mLeHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, 15000);
                        }
                        break;
                    }
                    case MSG_STOP_SCAN: {

                        mLeHandler.removeMessages(MSG_STOP_SCAN);
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mHandler.sendEmptyMessage(MSG_STOP_SCAN);
                        break;
                    }
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "自Android 6.0开始需要打开位置权限才可以搜索到Ble设备", Toast.LENGTH_SHORT).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }


    }

    public static boolean isGpsEnable(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
                //判断gps权限
                if(isGpsEnable(this)) {
                    mLeHandler.sendEmptyMessage(MSG_START_SCAN);
                } else {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent,requestCode);
                }

            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onStart() {
        super.onStart();
        mLeDeviceListAdapter.clear();
        mLeDeviceListAdapter.notifyDataSetChanged();

        mLeHandler.sendEmptyMessage(MSG_START_SCAN);

    }

    protected void onStop() {
        super.onStop();

        mLeHandler.sendEmptyMessage(MSG_STOP_SCAN);

    }

//    public void delayStopScan() {
//        mHandler.removeCallbacks(stopScanRunnable);
//        mHandler.postDelayed(stopScanRunnable, 15000);
//    }
//
//    private Runnable stopScanRunnable = new Runnable() {
//        @Override
//        public void run() {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            wcConnect.stopImmediately();
//        }
//    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_bluetooth:
                mLeHandler.sendEmptyMessage(MSG_START_SCAN);
                break;
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Device Name:" + device.getName());
                    mLeDeviceListAdapter.addDevice(device);
//                            mHandler.sendEmptyMessage(1);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    wcConnect.stopImmediately();
                }
            });
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) {
            return;
        }
        FPVApplication app = ((FPVApplication)getApplication());
        app.setBluetoothDevice(device);
        app.bindBleService();

//        String deviceName = device.getName();
//        String deviceAddress = device.getAddress();
//        if (deviceName == null || deviceAddress == null) {
//            return;
//        }
        Intent intent = new Intent(this, FPVActivity.class);
//        intent.putExtra(FPVActivity.EXTRAS_DEVICE_NAME, deviceName);
//        intent.putExtra(FPVActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        lvBluetooth.setBackground(null);
    }
}
