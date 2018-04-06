package com.synseaero.fpv;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.synseaero.dji.MessageType;
import com.synseaero.fpv.bluetooth.BluetoothLeService;
import com.synseaero.fpv.fragment.FPVFragment;
import com.synseaero.fpv.fragment.MenuFragment;
import com.synseaero.fpv.fragment.OneLevelMenuFragment;
import com.synseaero.fpv.fragment.TwoLevelMenuFragment;
import com.synseaero.fpv.model.FlightHeightMenuItem;
import com.synseaero.fpv.model.FlightRadiusMenuItem;
import com.synseaero.fpv.model.GoHomeHeightMenuItem;
import com.synseaero.fpv.model.LEDMenuItem;
import com.synseaero.fpv.model.Menu;
import com.synseaero.fpv.model.MenuItem;
import com.synseaero.fpv.model.PhotoFormatMenuItem;
import com.synseaero.fpv.model.PhotoRatioMenuItem;
import com.synseaero.fpv.model.ScreenMenuItem;
import com.synseaero.fpv.model.ShutterMenuItem;
import com.synseaero.fpv.model.VolumeMenuItem;
import com.synseaero.fpv.model.WhiteBalanceMenuItem;

import java.util.Vector;

import static com.synseaero.fpv.fragment.FPVFragment.MODE_FPV;
import static com.synseaero.fpv.fragment.FPVFragment.MODE_MENU;
import static com.synseaero.fpv.fragment.FPVFragment.MODE_TPV;

public class FPVActivity extends DJIActivity {

    private static final String TAG = FPVActivity.class.getName();

    private FPVFragment mFPVFragment;

    private MenuFragment mCurMenuFragment;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    //private BluetoothLeService mBluetoothLeService;

//    private String mDeviceName;
//    private String mDeviceAddress;

    private Vector<MenuFragment> mMenuFragments = new Vector<>();

    //c2按钮长按0.5s有效
    private static final int C2BUTTON_PRESS_DURATION = 500;

    private long c2DownTime;
    private long c2UpTime;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case MessageType.MSG_GET_RC_HARDWARE_STATE_RESPONSE:
                    boolean c2Present = bundle.getBoolean("c2Present");
                    boolean c2Down = bundle.getBoolean("c2Down");
                    if (c2Present) {
                        //记录c2按下时间
                        if (c2Down) {
                            c2DownTime = System.currentTimeMillis();
                        }
                        //记录c2弹起时间
                        else {
                            c2UpTime = System.currentTimeMillis();
                            if (c2UpTime - c2DownTime >= C2BUTTON_PRESS_DURATION && c2DownTime > 0) {
                                c2DownTime = 0;
                                int mode = mFPVFragment.getMode();
                                FPVApplication app = (FPVApplication) getApplication();
                                //切换模式
                                if (mode == MODE_FPV) {
                                    app.writeBleValue("FLAG-TPV");
                                    mFPVFragment.setMode(MODE_TPV);
                                } else {
                                    app.writeBleValue("FLAG-FPV");
                                    mFPVFragment.setMode(MODE_FPV);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_fpv);

        mFPVFragment = new FPVFragment();
        mFPVFragment.setActivity(this);

        //mMenuFragment = new TwoLevelMenuFragment();
        //mMenuFragment.setMenuData(menuData);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layout_fpv_root, mFPVFragment);
        transaction.commit();

//        final Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//
//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        initMenuData();
    }

    protected void onStart() {
        super.onStart();
        //注册大疆响应消息
        //遥控器按键状态
        registerDJIMessenger(MessageType.MSG_GET_RC_HARDWARE_STATE_RESPONSE, messenger);

        //watch rc按键状态变化
        sendWatchDJIMessage(MessageType.MSG_WATCH_RC_HARDWARE_STATE, 0);


        Message getGoHomeThresholdMsg = Message.obtain();
        getGoHomeThresholdMsg.what = MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD;
        sendDJIMessage(getGoHomeThresholdMsg);

    }

    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //遥控器按键状态
        unregisterDJIMessenger(MessageType.MSG_GET_RC_HARDWARE_STATE_RESPONSE, messenger);

        //停止watch rc按键状态变化
        sendWatchDJIMessage(MessageType.MSG_WATCH_RC_HARDWARE_STATE, 1);

    }

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
//            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                mBluetoothLeService.writeValue("FLAG-TPV");
//            }
            //重连
//            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mBluetoothLeService.connect(mDeviceAddress);
//            }
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null) {
                    //Log.i(TAG, "EXTRA_DATA:" + data);
                    if (data.equals("FLAG-TPV")) {
                        mFPVFragment.setMode(MODE_TPV);
                    } else if (data.equals("FLAG-FPV")) {
                        mFPVFragment.setMode(MODE_FPV);
                    }
                    //确认键
                    else if (data.equals("ET")) {
                        //切换到菜单模式
                        if (mFPVFragment.getMode() != MODE_MENU) {
                            mFPVFragment.setMode(MODE_MENU);
                        }
                        //展示菜单
                        else if (mCurMenuFragment.isHidden()) {
                            showMenu();
                        }
                        //展示子菜单或者进行操作
                        else {
                            mCurMenuFragment.onConfirmPressed();
                        }
                    }
                    //返回键
                    else if (data.equals("RT")) {
                        hideMenu();
                    }
                    //逆时针
                    else if (data.equals("CC")) {
                        if (!mCurMenuFragment.isHidden()) {
                            mCurMenuFragment.onUpPressed();
                        } else {
                            mFPVFragment.onUpPressed();
                        }
                    }
                    //顺时针
                    else if (data.equals("CW")) {
                        if (!mCurMenuFragment.isHidden()) {
                            mCurMenuFragment.onDownPressed();
                        } else {
                            mFPVFragment.onDownPressed();
                        }
                    }
                    //头盔电量
                    else if (data.contains("SOC")) {
                        //Log.i(TAG, "SOC:" + data);
                        String helmetEnergy = data.substring(3);
                        int energy = 0;
                        try {
                            energy = Integer.parseInt(helmetEnergy);
                        } catch (Exception e) {

                        }
                        mFPVFragment.setHelmetEnergy(energy);
                    }
                }
            }
        }
    };

//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//            if (!mBluetoothLeService.initialize()) {
////                Log.e(TAG, "Unable to initialize Bluetooth");
//                finish();
//            }
//            boolean result = mBluetoothLeService.connect(mDeviceAddress);
////            Log.e(TAG, "mBluetoothLeService is okay");
//            // Automatically connects to the device upon successful start-up initialization.
//            //mBluetoothLeService.connect(mDeviceAddress);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mBluetoothLeService = null;
//        }
//    };

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fpv, menu);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mGattUpdateReceiver);
        //Log.d(TAG, "onDestroy");
        //unbindService(mServiceConnection);
//        unregisterReceiver(mReceiver);

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        //FragmentManager fm = getSupportFragmentManager();
        //FragmentTransaction transaction = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_fpv: {
                mFPVFragment.setMode(MODE_FPV);
                break;
            }
            case R.id.menu_tpv: {
                mFPVFragment.setMode(MODE_TPV);
                break;
            }
            case R.id.menu_menu: {
                mFPVFragment.setMode(MODE_MENU);
                break;
            }
            case R.id.menu_map: {

                mFPVFragment.saveViewMode();

                Intent intent = new Intent(this, MapActivity.class);
//                intent.putExtra(FPVActivity.EXTRAS_DEVICE_NAME, mDeviceName);
//                intent.putExtra(FPVActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(intent);
                finish();
                break;
            }
//            case R.id.menu_style_1: {
//                ((FPVApplication) getApplication()).changeSkin(1);
//                break;
//            }
//            case R.id.menu_style_2: {
//                ((FPVApplication) getApplication()).changeSkin(2);
//                break;
//            }
//            case R.id.menu_style_3: {
//                ((FPVApplication) getApplication()).changeSkin(3);
//                break;
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMenuData() {
        //地图菜单
        //返航
        //MenuItem backMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.back), new String[]{getString(R.string.back)}, null);
        //显示航线
        MenuItem showPathSubMenu = new MenuItem(100, MenuItem.TYPE_SWITCH, getString(R.string.open),
                new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItem showPathMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.show_path), null, showPathSubMenu);
        //清除航线
        MenuItem clearPathSubMenu = new MenuItem(101, MenuItem.TYPE_SWITCH, getString(R.string.open),
                new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItem clearPathMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.clear_path), null, clearPathSubMenu);
        Menu mapMenu = new Menu();
        //mapMenu.items.add(backMenu);
        mapMenu.items.add(showPathMenu);
        mapMenu.items.add(clearPathMenu);
        MenuFragment mapMenuFragment = new TwoLevelMenuFragment();
        mapMenuFragment.setMenuData(mapMenu);
        mMenuFragments.add(mapMenuFragment);
        //屏幕菜单
        //亮度
        ScreenMenuItem lightSubMenu = new ScreenMenuItem(100, MenuItem.TYPE_PROGRESS, "30", null, null);
        lightSubMenu.setActivity(this);
        //MenuItem lightMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.brightness), null, lightSubMenu);
        Menu screenMenu = new Menu();
        screenMenu.items.add(lightSubMenu);
        MenuFragment screenMenuFragment = new OneLevelMenuFragment();
        screenMenuFragment.setMenuData(screenMenu);
        mMenuFragments.add(screenMenuFragment);
        //头盔菜单
        //音量
        VolumeMenuItem volumeSubMenu = new VolumeMenuItem(100, MenuItem.TYPE_PROGRESS, "50", null, null);
        volumeSubMenu.setActivity(this);
        MenuItem volumeMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.volume), null, volumeSubMenu);
        //通风
        MenuItem fanSubMenu = new MenuItem(101, MenuItem.TYPE_SELECT, getString(R.string.auto),
                new String[]{getString(R.string.auto), getString(R.string.force_open), getString(R.string.force_close)}, null);
        MenuItem fanMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.fan), null, fanSubMenu);
//        //找回
//        MenuItem findBackSubMenu = new MenuItem(102, MenuItem.TYPE_SWITCH, getString(R.string.auto),
//                new String[]{getString(R.string.open), getString(R.string.close)}, null);
//        MenuItem findBackMenu = new MenuItem(2, MenuItem.TYPE_TEXT, getString(R.string.find_back), null, findBackSubMenu);
//        //风格
//        MenuItem styleSubMenu = new MenuItem(103, MenuItem.TYPE_SELECT, getString(R.string.style_1),
//                new String[]{getString(R.string.style_1), getString(R.string.style_2), getString(R.string.style_3)}, null);
//        MenuItem styleMenu = new MenuItem(3, MenuItem.TYPE_TEXT, getString(R.string.style), null, styleSubMenu);
        Menu helmetMenu = new Menu();
        helmetMenu.items.add(volumeMenu);
        helmetMenu.items.add(fanMenu);
        //helmetMenu.items.add(findBackMenu);
        //helmetMenu.items.add(styleMenu);
        MenuFragment helmetMenuFragment = new TwoLevelMenuFragment();
        helmetMenuFragment.setMenuData(helmetMenu);
        mMenuFragments.add(helmetMenuFragment);
        //拍照菜单
        //快门
        ShutterMenuItem shutterSubMenu = new ShutterMenuItem(100, MenuItem.TYPE_SELECT, getString(R.string.auto),
                new String[]{getString(R.string.auto), getString(R.string.manual)}, null);
        MenuItem shutterMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.shutter_speed), null, shutterSubMenu);
//        //拍照模式
//        MenuItem photoModeSubMenu = new MenuItem(101, MenuItem.TYPE_SELECT, getString(R.string.single_photo),
//                new String[]{getString(R.string.single_photo), getString(R.string.hdr), getString(R.string.series_photo), getString(R.string.aeb_series_photo), getString(R.string.internal_photo)}, null);
//        MenuItem photoMode = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.photo_mode), null, photoModeSubMenu);
        //照片比例
        PhotoRatioMenuItem ratioSubMenu = new PhotoRatioMenuItem(102, MenuItem.TYPE_SELECT, getString(R.string.ratio43),
                new String[]{getString(R.string.ratio43), getString(R.string.ratio169)}, null);
        MenuItem ratioMenu = new MenuItem(2, MenuItem.TYPE_TEXT, getString(R.string.photo_resolve), null, ratioSubMenu);
        //照片格式
        PhotoFormatMenuItem photoFormatSubMenu = new PhotoFormatMenuItem(103, MenuItem.TYPE_SELECT, getString(R.string.jpeg),
                new String[]{getString(R.string.jpeg), getString(R.string.raw)}, null);
        MenuItem photoFormatMenu = new MenuItem(3, MenuItem.TYPE_TEXT, getString(R.string.photo_format), null, photoFormatSubMenu);
        //白平衡
        WhiteBalanceMenuItem whiteBalanceSubMenu = new WhiteBalanceMenuItem(104, MenuItem.TYPE_SELECT, getString(R.string.cloudy),
                new String[]{getString(R.string.auto), getString(R.string.sunny), getString(R.string.cloudy)}, null);
        MenuItem whiteBalanceMenu = new MenuItem(4, MenuItem.TYPE_TEXT, getString(R.string.white_balance), null, whiteBalanceSubMenu);
        Menu photoMenu = new Menu();
        photoMenu.items.add(shutterMenu);
        //photoMenu.items.add(photoMode);
        photoMenu.items.add(ratioMenu);
        photoMenu.items.add(photoFormatMenu);
        photoMenu.items.add(whiteBalanceMenu);
        MenuFragment photoMenuFragment = new TwoLevelMenuFragment();
        photoMenuFragment.setMenuData(photoMenu);
        mMenuFragments.add(photoMenuFragment);
//        //录像菜单
//        //尺寸
//        VideoRatioMenuItem sizeSubMenu = new VideoRatioMenuItem(100, MenuItem.TYPE_SELECT, "4K", new String[]{"4K", "1080P", "720P"}, null);
//        MenuItem sizeMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.video_resolve), null, sizeSubMenu);
//        //帧率
//        VideoFrameRateMenuItem frameRateSubMenu = new VideoFrameRateMenuItem(101, MenuItem.TYPE_SELECT, "24", new String[]{"24", "30", "60"}, null);
//        MenuItem frameRateMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.frame_rate), null, frameRateSubMenu);
//        //视频格式
//        VideoFormatMenuItem formatSubMenu = new VideoFormatMenuItem(102, MenuItem.TYPE_SELECT, "MOV", new String[]{"MOV", "MP4"}, null);
//        MenuItem formatMenu = new MenuItem(2, MenuItem.TYPE_TEXT, getString(R.string.video_format), null, formatSubMenu);
//        Menu videoMenu = new Menu();
//        videoMenu.items.add(sizeMenu);
//        videoMenu.items.add(frameRateMenu);
//        videoMenu.items.add(formatMenu);
//        MenuFragment videoMenuFragment = new TwoLevelMenuFragment();
//        videoMenuFragment.setMenuData(videoMenu);
//        mMenuFragments.add(videoMenuFragment);
//        //云台菜单
//        //头部跟踪
//        MenuItem headSubMenu = new MenuItem(100, MenuItem.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
//        MenuItem headMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.head_follow), null, headSubMenu);
//        //航向跟踪
//        MenuItem courseSubMenu = new MenuItem(101, MenuItem.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
//        MenuItem courseMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.course_follow), null, courseSubMenu);
//        Menu panMenu = new Menu();
//        panMenu.items.add(headMenu);
//        panMenu.items.add(courseMenu);
//        MenuFragment panMenuFragment = new TwoLevelMenuFragment();
//        panMenuFragment.setMenuData(panMenu);
//        mMenuFragments.add(panMenuFragment);
        //飞控菜单
        //led开关
        LEDMenuItem LEDSubMenu = new LEDMenuItem(100, MenuItem.TYPE_SWITCH, "true", new String[]{getString(R.string.open), getString(R.string.close)}, null);
        MenuItem LEDMenu = new MenuItem(0, MenuItem.TYPE_TEXT, getString(R.string.led_switch), null, LEDSubMenu);
        //返航高度
        GoHomeHeightMenuItem homeHeightSubMenu = new GoHomeHeightMenuItem(101, MenuItem.TYPE_TEXT, "300", null, null);
        MenuItem homeHeightMenu = new MenuItem(1, MenuItem.TYPE_TEXT, getString(R.string.home_height), null, homeHeightSubMenu);
        //最大升距
        FlightHeightMenuItem maxHeightSubMenu = new FlightHeightMenuItem(102, MenuItem.TYPE_TEXT, "300", null, null);
        MenuItem maxHeightMenu = new MenuItem(2, MenuItem.TYPE_TEXT, getString(R.string.max_flight_height), null, maxHeightSubMenu);
        //最大距离
        FlightRadiusMenuItem maxRadiusSubMenu = new FlightRadiusMenuItem(103, MenuItem.TYPE_TEXT, "300", null, null);
        MenuItem maxRadiusMenu = new MenuItem(3, MenuItem.TYPE_TEXT, getString(R.string.max_flight_radius), null, maxRadiusSubMenu);

        Menu RCMenu = new Menu();
        RCMenu.items.add(LEDMenu);
        RCMenu.items.add(homeHeightMenu);
        RCMenu.items.add(maxHeightMenu);
        RCMenu.items.add(maxRadiusMenu);
        MenuFragment RCMenuFragment = new TwoLevelMenuFragment();
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
        if(mMenuFragments.get(0) == mCurMenuFragment) {

            mFPVFragment.saveViewMode();

            Intent mapIntent = new Intent(this, MapActivity.class);
//            mapIntent.putExtra(FPVActivity.EXTRAS_DEVICE_NAME, mDeviceName);
//            mapIntent.putExtra(FPVActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
            startActivity(mapIntent);
            finish();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.show(mCurMenuFragment).commit();
        }
    }

    public void onBackPressed() {
        if (hideMenu()) {
            return;
        }

        super.onBackPressed();
    }

    private boolean hideMenu() {
        //收起菜单内容
        boolean result = mCurMenuFragment.onBackPressed();
        if (result) {
            return true;
        }
        //收起菜单本身
        if (!mCurMenuFragment.isHidden()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(mCurMenuFragment).commit();
            return true;
        }
        //收起滑动菜单
        if (mFPVFragment.getMode() == MODE_MENU) {
            mFPVFragment.setMode(mFPVFragment.getLastMode());
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!mCurMenuFragment.isHidden()) {
                    mCurMenuFragment.onDownPressed();
                } else {
                    mFPVFragment.onDownPressed();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!mCurMenuFragment.isHidden()) {
                    mCurMenuFragment.onUpPressed();
                } else {
                    mFPVFragment.onUpPressed();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}
