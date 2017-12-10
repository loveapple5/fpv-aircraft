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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.dji.FPVDemo.bluetooth.BluetoothLeService;
import com.dji.FPVDemo.fragment.FPVFragment;
import com.dji.FPVDemo.fragment.MenuFragment;
import com.dji.FPVDemo.model.MenuData;
import com.dji.FPVDemo.model.MenuItemData;

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

    private MenuFragment mCurMenuFragment;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothLeService mBluetoothLeService;

    private String mDeviceName;
    private String mDeviceAddress;

    private DJIAircraft djiAircraft;
    private DJIRemoteController djiRemoteController;

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
                    if (data.equals("FLAG-TPV")) {
                        mFPVFragment.setMode(MODE_TPV);
                    }
                    else if (data.equals("FLAG-FPV")) {
                        mFPVFragment.setMode(MODE_FPV);
                    }
                    //确认键
                    else if(data.equals("ET")) {
                        //切换到菜单模式
                        if(mFPVFragment.getMode() != MODE_MENU) {
                            mFPVFragment.setMode(MODE_MENU);
                        }
                        //展示菜单
                        else if(mCurMenuFragment.isHidden()) {
                            showMenu();
                        }
                        //展示子菜单或者进行操作
                        else {
                            mCurMenuFragment.onConfirmPressed();
                        }
                    }
                    //返回键
                    else if(data.equals("RT")) {
                        hideMenu();
                    }
                    //逆时针
                    else if(data.equals("CC")) {
                        if(!mCurMenuFragment.isHidden()) {
                            mCurMenuFragment.onUpPressed();
                        } else {
                            mFPVFragment.onUpPressed();
                        }
                    }
                    //顺时针
                    else if(data.equals("CW")) {
                        if(!mCurMenuFragment.isHidden()) {
                            mCurMenuFragment.onDownPressed();
                        } else {
                            mFPVFragment.onDownPressed();
                        }
                    }
                    //头盔电量
                    else if (data.contains("SOC")) {
                        Log.i(TAG, "SOC:" + data);
                        String helmetEnergy = data.substring(3);
                        int energy = 0;
                        try {
                            energy = Integer.parseInt(helmetEnergy);
                        }catch(Exception e) {

                        }
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
        //MenuItemData backMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.back), new String[]{getString(R.string.back)}, null);
        //显示航线
        MenuItemData showPathSubMenu = new MenuItemData(100, MenuItemData.TYPE_SWITCH, getString(R.string.open),
                new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItemData showPathMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.show_path), null, showPathSubMenu);
        //清除航线
        MenuItemData clearPathSubMenu = new MenuItemData(101, MenuItemData.TYPE_SWITCH, getString(R.string.open),
                new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItemData clearPathMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.clear_path), null, clearPathSubMenu);
        MenuData mapMenu = new MenuData();
        //mapMenu.items.add(backMenu);
        mapMenu.items.add(showPathMenu);
        mapMenu.items.add(clearPathMenu);
        MenuFragment mapMenuFragment = new MenuFragment();
        mapMenuFragment.setMenuData(mapMenu);
        mMenuFragments.add(mapMenuFragment);
        //屏幕菜单
        //亮度
        MenuItemData lightSubMenu = new MenuItemData(100, MenuItemData.TYPE_PROGRESS, "30", null, null);
        MenuItemData lightMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.brightness), null, lightSubMenu);
        MenuData screenMenu = new MenuData();
        screenMenu.items.add(lightMenu);
        MenuFragment screenMenuFragment = new MenuFragment();
        screenMenuFragment.setMenuData(screenMenu);
        mMenuFragments.add(screenMenuFragment);
        //头盔菜单
        //音量
        MenuItemData volumeSubMenu = new MenuItemData(100, MenuItemData.TYPE_PROGRESS, "50", null,  null);
        MenuItemData volumeMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.volume), null, volumeSubMenu);
        //通风
        MenuItemData fanSubMenu = new MenuItemData(101, MenuItemData.TYPE_SELECT, getString(R.string.auto),
                new String[]{getString(R.string.auto), getString(R.string.force_open), getString(R.string.force_close)}, null);
        MenuItemData fanMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.fan), null, fanSubMenu);
        //找回
        MenuItemData findBackSubMenu = new MenuItemData(102, MenuItemData.TYPE_SWITCH, getString(R.string.auto),
                new String[]{getString(R.string.open), getString(R.string.close) }, null);
        MenuItemData findBackMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.find_back), null, findBackSubMenu);
        //风格
        MenuItemData styleSubMenu = new MenuItemData(103, MenuItemData.TYPE_SELECT, getString(R.string.style_1),
                new String[]{getString(R.string.style_1), getString(R.string.style_2), getString(R.string.style_3) }, null);
        MenuItemData styleMenu = new MenuItemData(3, MenuItemData.TYPE_TEXT, getString(R.string.style), null, styleSubMenu);
        MenuData helmetMenu = new MenuData();
        helmetMenu.items.add(volumeMenu);
        helmetMenu.items.add(fanMenu);
        helmetMenu.items.add(findBackMenu);
        helmetMenu.items.add(styleMenu);
        MenuFragment helmetMenuFragment = new MenuFragment();
        helmetMenuFragment.setMenuData(helmetMenu);
        mMenuFragments.add(helmetMenuFragment);
        //拍照菜单
        //快门
        MenuItemData shutterSubMenu = new MenuItemData(100, MenuItemData.TYPE_SELECT, getString(R.string.auto),
                new String[]{getString(R.string.auto), getString(R.string.manual)}, null);
        MenuItemData shutterMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.shutter_speed), null, shutterSubMenu);
        //拍照模式
        MenuItemData photoModeSubMenu = new MenuItemData(101, MenuItemData.TYPE_SELECT, getString(R.string.single_photo),
                new String[]{getString(R.string.single_photo), getString(R.string.hdr), getString(R.string.series_photo), getString(R.string.aeb_series_photo),getString(R.string.internal_photo)}, null);
        MenuItemData photoMode = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.photo_mode), null, photoModeSubMenu);
        //照片比例
        MenuItemData ratioSubMenu = new MenuItemData(102, MenuItemData.TYPE_SELECT, getString(R.string.ratio43),
                new String[]{getString(R.string.ratio43), getString(R.string.ratio169)}, null);
        MenuItemData ratioMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.photo_resolve), null, ratioSubMenu);
        //照片格式
        MenuItemData photoFormatSubMenu = new MenuItemData(103, MenuItemData.TYPE_SELECT, getString(R.string.jpeg),
                new String[]{getString(R.string.jpeg), getString(R.string.raw)}, null);
        MenuItemData photoFormatMenu = new MenuItemData(3, MenuItemData.TYPE_TEXT, getString(R.string.photo_format), null, photoFormatSubMenu);
        //白平衡
        MenuItemData whiteBalanceSubMenu = new MenuItemData(104, MenuItemData.TYPE_SELECT, getString(R.string.cloudy),
                new String[]{getString(R.string.auto), getString(R.string.sunny), getString(R.string.cloudy)}, null);
        MenuItemData whiteBalanceMenu = new MenuItemData(4, MenuItemData.TYPE_TEXT, getString(R.string.white_balance), null, whiteBalanceSubMenu);
        MenuData photoMenu = new MenuData();
        photoMenu.items.add(shutterMenu);
        photoMenu.items.add(photoMode);
        photoMenu.items.add(ratioMenu);
        photoMenu.items.add(photoFormatMenu);
        photoMenu.items.add(whiteBalanceMenu);
        MenuFragment photoMenuFragment = new MenuFragment();
        photoMenuFragment.setMenuData(photoMenu);
        mMenuFragments.add(photoMenuFragment);
        //录像菜单
        //尺寸
        MenuItemData sizeSubMenu = new MenuItemData(100, MenuItemData.TYPE_SELECT, "4K", new String[] {"4K", "1080P", "720P"}, null);
        MenuItemData sizeMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.video_resolve), null, sizeSubMenu);
        //帧率
        MenuItemData frameRateSubMenu = new MenuItemData(101, MenuItemData.TYPE_SELECT, "24", new String[] {"24", "30", "60"}, null);
        MenuItemData frameRateMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.frame_rate), null, frameRateSubMenu);
        //视频格式
        MenuItemData formatSubMenu = new MenuItemData(102, MenuItemData.TYPE_SELECT, "MOV", new String[] {"MOV", "MP4"}, null);
        MenuItemData formatMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.video_format), null, formatSubMenu);
        MenuData videoMenu = new MenuData();
        videoMenu.items.add(sizeMenu);
        videoMenu.items.add(frameRateMenu);
        videoMenu.items.add(formatMenu);
        MenuFragment videoMenuFragment = new MenuFragment();
        videoMenuFragment.setMenuData(videoMenu);
        mMenuFragments.add(videoMenuFragment);
        //云台菜单
        //头部跟踪
        MenuItemData headSubMenu = new MenuItemData(100, MenuItemData.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItemData headMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.head_follow), null, headSubMenu);
        //航向跟踪
        MenuItemData courseSubMenu = new MenuItemData(101, MenuItemData.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItemData courseMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.course_follow), null, courseSubMenu);
        MenuData panMenu = new MenuData();
        panMenu.items.add(headMenu);
        panMenu.items.add(courseMenu);
        MenuFragment panMenuFragment = new MenuFragment();
        panMenuFragment.setMenuData(panMenu);
        mMenuFragments.add(panMenuFragment);
        //飞控菜单
        //led开关
        MenuItemData LEDSubMenu = new MenuItemData(100, MenuItemData.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItemData LEDMenu = new MenuItemData(0, MenuItemData.TYPE_TEXT, getString(R.string.led_switch), null, LEDSubMenu);
        //返航高度
        MenuItemData homeHeightSubMenu = new MenuItemData(101, MenuItemData.TYPE_TEXT, "300", null, null);
        MenuItemData homeHeightMenu = new MenuItemData(1, MenuItemData.TYPE_TEXT, getString(R.string.home_height), null, homeHeightSubMenu);
        //最大升距
        MenuItemData maxHeightSubMenu = new MenuItemData(102, MenuItemData.TYPE_TEXT, "300", null, null);
        MenuItemData maxHeightMenu = new MenuItemData(2, MenuItemData.TYPE_TEXT, getString(R.string.max_flight_height), null, maxHeightSubMenu);
        //最大距离
        MenuItemData maxRadiusSubMenu = new MenuItemData(103, MenuItemData.TYPE_TEXT, "300", null, null);
        MenuItemData maxRadiusMenu = new MenuItemData(3, MenuItemData.TYPE_TEXT, getString(R.string.max_flight_radius), null, maxRadiusSubMenu);

        MenuData RCMenu = new MenuData();
        RCMenu.items.add(LEDMenu);
        RCMenu.items.add(homeHeightMenu);
        RCMenu.items.add(maxHeightMenu);
        RCMenu.items.add(maxRadiusMenu);
        MenuFragment RCMenuFragment = new MenuFragment();
        RCMenuFragment.setMenuData(RCMenu);
        mMenuFragments.add(RCMenuFragment);

        //设置初始菜单
        mCurMenuFragment = mMenuFragments.get(3);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_fpv_menu, mCurMenuFragment).hide(mCurMenuFragment).commit();

    }

    public void switchMenu(int index) {
        MenuFragment targetFragment = mMenuFragments.get(index);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurMenuFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                transaction.hide(mCurMenuFragment).add(R.id.layout_fpv_menu, targetFragment).hide(targetFragment).commit();
            } else {
                transaction.hide(mCurMenuFragment).commit();
            }
        } else {
            transaction.hide(mCurMenuFragment).commit();
        }
        mCurMenuFragment = targetFragment;
    }

    public void showMenu() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(mCurMenuFragment).commit();
    }

    public void onBackPressed() {
        if(hideMenu()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean hideMenu() {
        //收起二级菜单
        boolean result = mCurMenuFragment.onBackPressed();
        if(result) {
            return true;
        }
        //收起一级菜单
        if(!mCurMenuFragment.isHidden()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(mCurMenuFragment).commit();
            return true;
        }
        //收起滑动菜单
        if(mFPVFragment.getMode() == MODE_MENU) {
            mFPVFragment.setMode(mFPVFragment.getLastMode());
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(!mCurMenuFragment.isHidden()) {
                    mCurMenuFragment.onDownPressed();
                } else {
                    mFPVFragment.onDownPressed();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(!mCurMenuFragment.isHidden()) {
                    mCurMenuFragment.onUpPressed();
                } else {
                    mFPVFragment.onUpPressed();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}
