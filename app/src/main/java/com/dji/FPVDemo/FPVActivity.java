package com.dji.FPVDemo;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.dji.FPVDemo.bluetooth.BluetoothLeService;
import com.dji.FPVDemo.fragment.FPVFragment;
import com.dji.FPVDemo.fragment.MenuFragment;
import com.dji.FPVDemo.model.MenuData;
//import com.dji.FPVDemo.model.MenuItemData;

import java.util.Vector;

import dji.common.remotecontroller.DJIRCHardwareState;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

import static com.dji.FPVDemo.fragment.FPVFragment.MODE_FPV;
import static com.dji.FPVDemo.fragment.FPVFragment.MODE_TPV;
import static com.dji.FPVDemo.fragment.FPVFragment.MODE_MENU;

public class FPVActivity extends FragmentActivity {

    private static final String TAG = FPVActivity.class.getName();

    //    private TPVFragment mTPVFragment;
    private FPVFragment mFPVFragment;

    private Fragment mCurMenuFragment;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothLeService mBluetoothLeService;

    private String mDeviceName;
    private String mDeviceAddress;

    private DJIAircraft djiAircraft;
    private DJIRemoteController djiRemoteController;

    private Vector<MenuData> menuData = new Vector<>();
    private Vector<MenuFragment> mMenuFragments = new Vector<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_fpv);

        mFPVFragment = new FPVFragment();
        //mTPVFragment = new TPVFragment();

        //mMenuFragment = new MenuFragment();
        //mMenuFragment.setMenuData(menuData);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layout_fpv_root, mFPVFragment);
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

        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);

        initMenuData();
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(FPVActivity.this, "设备无连接，请检测并重启飞机", Toast.LENGTH_LONG).show();
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
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
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mBluetoothLeService.writeValue("FLAG-TPV");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null) {
                    Log.i(TAG, "EXTRA_DATA:" + data);
                    if (data.contains("FLAG-TPV")) {
                        mFPVFragment.setMode(MODE_TPV);
                    } else if (data.contains("FLAG-FPV")) {
                        mFPVFragment.setMode(MODE_FPV);
                    } else if (data.contains("SOC")) {
                        Log.i(TAG, "SOC:" + data);
                        String helmetEnergy = data.substring(3);
                        int energy = Integer.parseInt(helmetEnergy);
                        mFPVFragment.setHelmetEnergy(energy);
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
        //FragmentManager fm = getSupportFragmentManager();
        //FragmentTransaction transaction = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_fpv:
                mFPVFragment.setMode(MODE_FPV);
                break;
            case R.id.menu_tpv:
                mFPVFragment.setMode(MODE_TPV);
                break;
            case R.id.menu_menu:
                mFPVFragment.setMode(MODE_MENU);
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
                    if (buttonUpTime - buttonDownTime >= C2BUTTON_PRESS_DURATION && buttonDownTime > 0) {
                        buttonDownTime = 0;
                        int mode = mFPVFragment.getMode();
                        if (mode == MODE_FPV) {
                            mBluetoothLeService.writeValue("FLAG-FPV");
                        } else {
                            mBluetoothLeService.writeValue("FLAG-TPV");
                        }
                    }
                }
            }
        }
    }

    private void initMenuData() {
        //地图菜单
        //返航
//        MenuItemData backMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.back), new String[]{getString(R.string.back)}, null);
//        //显示航线
//        MenuItemData showPathSubMenu = new MenuItemData(100, MenuItemData.TYPE_SWITCH, getString(R.string.open),
//                new String[]{getString(R.string.open), getString(R.string.close)}, null);
//        MenuItemData showPathMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.show_path), null, showPathSubMenu);
//        MenuData mapMenu = new MenuData();
//        mapMenu.items.add(backMenu);
//        mapMenu.items.add(showPathMenu);
//        MenuFragment mapMenuFragment = new MenuFragment();
//        mapMenuFragment.setMenuData(mapMenu);
//        mMenuFragments.add(mapMenuFragment);
//        //屏幕菜单
//        //亮度
//        MenuItemData lightMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, "30", null, null);
//        MenuData screenMenu = new MenuData();
//        screenMenu.items.add(lightMenu);
//        MenuFragment screenMenuFragment = new MenuFragment();
//        screenMenuFragment.setMenuData(screenMenu);
//        mMenuFragments.add(screenMenuFragment);
//        //头盔菜单
//        //音量
//        MenuItemData volumeMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, "50", null, null);
//        MenuData helmetMenu = new MenuData();
//        helmetMenu.items.add(volumeMenu);
//        MenuFragment helmetMenuFragment = new MenuFragment();
//        helmetMenuFragment.setMenuData(helmetMenu);
//        mMenuFragments.add(helmetMenuFragment);
//        //拍照菜单
//        MenuItemData shutterSubMenu = new MenuItemData(100, MenuItemData.TYPE_SELECT, getString(R.string.auto),
//                new String[]{getString(R.string.auto), getString(R.string.manual)}, null);
//        MenuItemData shutterMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.shutter_speed), null, shutterSubMenu);
//
//        MenuItemData ratioSubMenu = new MenuItemData(101, MenuItemData.TYPE_SELECT, getString(R.string.ratio43),
//                new String[]{getString(R.string.ratio43), getString(R.string.ratio169)}, null);
//        MenuItemData ratioMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.camera_mode), null, ratioSubMenu);
//        MenuData photoMenu = new MenuData();
//        photoMenu.items.add(shutterMenu);
//        photoMenu.items.add(ratioMenu);
//        MenuFragment photoMenuFragment = new MenuFragment();
//        photoMenuFragment.setMenuData(photoMenu);
//        mMenuFragments.add(photoMenuFragment);
//        //录像菜单
//        //尺寸
//        MenuItemData sizeMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, "4K", null, null);
//        MenuData videoMenu = new MenuData();
//        videoMenu.items.add(sizeMenu);
//        MenuFragment videoMenuFragment = new MenuFragment();
//        videoMenuFragment.setMenuData(videoMenu);
//        mMenuFragments.add(videoMenuFragment);
//        //云台菜单
//        //头部跟踪
//        MenuItemData headMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, "打开", null, null);
//        MenuData panMenu = new MenuData();
//        panMenu.items.add(headMenu);
//        MenuFragment panMenuFragment = new MenuFragment();
//        panMenuFragment.setMenuData(panMenu);
//        mMenuFragments.add(panMenuFragment);
//        //飞控菜单
//        MenuItemData LEDMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, "关闭", null, null);
//        MenuData RCMenu = new MenuData();
//        RCMenu.items.add(LEDMenu);
//        MenuFragment RCMenuFragment = new MenuFragment();
//        RCMenuFragment.setMenuData(RCMenu);
//        mMenuFragments.add(RCMenuFragment);
//        //设置初始菜单
//        mCurMenuFragment = mMenuFragments.get(3);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.layout_fpv_menu, mCurMenuFragment).hide(mCurMenuFragment).commit();

    }

    public void switchMenu(int index) {
//        Fragment targetFragment = mMenuFragments.get(index);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (mCurMenuFragment != targetFragment) {
//            if (!targetFragment.isAdded()) {
//                transaction.hide(mCurMenuFragment).add(R.id.layout_fpv_menu, targetFragment).hide(targetFragment).commit();
//            } else {
//                transaction.hide(mCurMenuFragment).commit();
//            }
//        } else {
//            transaction.hide(mCurMenuFragment).commit();
//        }
//        mCurMenuFragment = targetFragment;

    }

    public void showMenu() {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.show(mCurMenuFragment).commit();
    }

}
