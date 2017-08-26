package com.dji.FPVDemo;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.dji.FPVDemo.bluetooth.BluetoothLeService;
import com.dji.FPVDemo.fragment.FPVFragment;
import com.dji.FPVDemo.fragment.TPVFragment;

import java.util.List;

import dji.common.remotecontroller.DJIRCHardwareState;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

public class FPVActivity extends FragmentActivity {

    private static final String TAG = FPVActivity.class.getName();

    private TPVFragment mTPVFragment;
    private FPVFragment mFPVFragment;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothLeService mBluetoothLeService;

    private String mDeviceName;
    private String mDeviceAddress;

    private DJIAircraft djiAircraft;
    private DJIRemoteController djiRemoteController;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_fpv);

        mFPVFragment = new FPVFragment();
        mTPVFragment = new TPVFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layout_fpv_root, mTPVFragment);
        transaction.commit();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            djiRemoteController = djiAircraft.getRemoteController();
            djiRemoteController.setHardwareStateUpdateCallback(new HardwareStateCallback());
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {                        //ע����յ��¼�
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mBluetoothLeService.writeValue("FLAG-TPV");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null) {
                    Log.i(TAG, "EXTRA_DATA:" + data);
                    if (data.contains("FLAG-TPV")) {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        Log.i(TAG, "TPV:" + data);
                        transaction.replace(R.id.layout_fpv_root, mTPVFragment);
                        transaction.commit();
                    } else if (data.contains("FLAG-FPV")) {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        Log.i(TAG, "FPV:" + data);
                        transaction.replace(R.id.layout_fpv_root, mFPVFragment);
                        transaction.commit();
                    } else if (data.contains("SOC")) {
                        Log.i(TAG, "SOC:" + data);
                        String helmetEnergy = data.substring(3);
                        int energy = Integer.parseInt(helmetEnergy);
                        FragmentManager fm = getSupportFragmentManager();
                        List<Fragment> fragments = fm.getFragments();
                        if (fragments != null) {
                            if (fragments.contains(mTPVFragment)) {
                                mTPVFragment.setHelmetEnergy(energy);
                            } else if(fragments.contains(mFPVFragment)) {
                                mFPVFragment.setHelmetEnergy(energy);
                            }
                        }
                    }
                }
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.e(TAG, "mBluetoothLeService is okay");
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fpv, menu);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_fpv:
                transaction.replace(R.id.layout_fpv_root, mFPVFragment);
                transaction.commit();
                break;
            case R.id.menu_tpv:
                transaction.replace(R.id.layout_fpv_root, mTPVFragment);
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HardwareStateCallback implements DJIRemoteController.RCHardwareStateUpdateCallback {

        //c2按钮长按0.5s有效
        private static final int C2BUTTON_PRESS_DURATION = 500;

        private long buttonDownTime;
        private long buttonUpTime;

        @Override
        public void onHardwareStateUpdate(DJIRemoteController djiRemoteController, DJIRCHardwareState djircHardwareState) {
            if (djircHardwareState.customButton2.isPresent) {
                Log.i(TAG, "c2:" + djircHardwareState.customButton2.buttonDown);
                if (djircHardwareState.customButton2.buttonDown) {
                    buttonDownTime = System.currentTimeMillis();
                } else {
                    buttonUpTime = System.currentTimeMillis();
                    if (buttonUpTime - buttonDownTime >= C2BUTTON_PRESS_DURATION  && buttonDownTime > 0) {
                        buttonDownTime = 0;
                        FragmentManager fm = getSupportFragmentManager();
                        List<Fragment> fragments = fm.getFragments();

                        FragmentTransaction transaction = fm.beginTransaction();
                        if (fragments != null) {
                            if (fragments.contains(mTPVFragment)) {
                                transaction.replace(R.id.layout_fpv_root, mFPVFragment);
                                transaction.commit();
                                mBluetoothLeService.writeValue("FLAG-FPV");
                            } else {
                                transaction.replace(R.id.layout_fpv_root, mTPVFragment);
                                transaction.commit();
                                mBluetoothLeService.writeValue("FLAG-TPV");
                            }
                        }
                    }
                }
            }
        }
    }
}
