package com.synseaero.fpv;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.synseaero.view.WaveView;

public class BluetoothActivity extends DJIActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = BluetoothActivity.class.getName();

    private ListView lvBluetooth;
    private BluetoothListAdapter mLeDeviceListAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    private WaveView wcConnect;

    private Handler mHandler = new Handler();

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

    }

    protected void onStart() {
        super.onStart();
        mLeDeviceListAdapter.clear();
        mLeDeviceListAdapter.notifyDataSetChanged();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            delayStopScan();
            wcConnect.start();
        }
    }

    protected void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mHandler.removeCallbacks(stopScanRunnable);
    }

    public void delayStopScan() {
        mHandler.removeCallbacks(stopScanRunnable);
        mHandler.postDelayed(stopScanRunnable, 15000);
    }

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            wcConnect.stopImmediately();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_bluetooth:
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.enable();
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    delayStopScan();
                    wcConnect.start();
                }
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
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        if (deviceName == null || deviceAddress == null) {
            return;
        }
        Intent intent = new Intent(this, FPVActivity.class);
        intent.putExtra(FPVActivity.EXTRAS_DEVICE_NAME, deviceName);
        intent.putExtra(FPVActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        lvBluetooth.setBackground(null);
    }
}
