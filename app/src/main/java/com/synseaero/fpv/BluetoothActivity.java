package com.synseaero.fpv;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.synseaero.view.WaveView;

import java.util.ArrayList;

public class BluetoothActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = BluetoothActivity.class.getName();

    private ListView lvBluetooth;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private LinearLayout llEmpty;
    private View btnSearch;

    private BluetoothAdapter mBluetoothAdapter;

    private WaveView wcConnect;

    private Handler mHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_bluetooth);
        lvBluetooth = (ListView) findViewById(R.id.lv_bluetooth);

        llEmpty = (LinearLayout) findViewById(R.id.ll_bluetooth_empty);
        lvBluetooth.setEmptyView(llEmpty);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        lvBluetooth.setAdapter(mLeDeviceListAdapter);
        lvBluetooth.setOnItemClickListener(this);
        btnSearch = findViewById(R.id.tv_search_bluetooth);
        btnSearch.setOnClickListener(this);

        wcConnect = (WaveView) findViewById(R.id.wv_connect);
        wcConnect.setInitialRadius(150);
        wcConnect.setMaxRadiusRate(1);
        wcConnect.setDuration(5000);
        wcConnect.setStyle(Paint.Style.FILL);
        wcConnect.setColor(getResources().getColor(R.color.blue));
        wcConnect.setInterpolator(new LinearOutSlowInInterpolator());

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
        mHandler.postDelayed(stopScanRunnable, 5000);
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
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("Unknown Service");
            }

            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
