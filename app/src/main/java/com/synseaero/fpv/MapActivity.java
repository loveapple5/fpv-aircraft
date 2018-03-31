package com.synseaero.fpv;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.WindowManager;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.synseaero.dji.MessageType;
import com.synseaero.fpv.bluetooth.BluetoothLeService;

import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends DJIActivity {

    public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private MapView mapView;
    private AMap aMap;

    private Marker homeMarker;
    private Marker aircraftMarker;

    private Timer timer;

    //private BluetoothLeService mBluetoothLeService;

//    private String mDeviceName;
//    private String mDeviceAddress;

//    private LatLng TH = new LatLng(39.926516, 116.389366);
//    private int index = 0;


    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                //重新定义锚点且绘制home点
                case MessageType.MSG_GET_HOME_LOCATION_RESPONSE: {

                    double longitude = bundle.getDouble("longitude");
                    double latitude = bundle.getDouble("latitude");
                    LatLng home = new LatLng(latitude, longitude);
                    LatLng transHome = convert(home, CoordinateConverter.CoordType.GPS);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(transHome).zoom(18).bearing(0).tilt(0).build();
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    aMap.moveCamera(update);

                    //aMap.clear();
                    if (homeMarker != null) {
                        homeMarker.remove();
                    }
                    homeMarker = aMap.addMarker(new MarkerOptions().position(transHome)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    break;
                }
                case MessageType.MSG_GET_FC_STATE_RESPONSE: {
                    double latA = bundle.getDouble("latA");
                    double longA = bundle.getDouble("longA");
                    double heading = bundle.getDouble("Head");
                    LatLng aircraftPosition = new LatLng(latA, longA);
                    LatLng transAircraftPosition = convert(aircraftPosition, CoordinateConverter.CoordType.GPS);

                    CameraPosition cameraPosition = aMap.getCameraPosition();
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(transAircraftPosition, cameraPosition.zoom));

                    if (aircraftMarker != null) {
                        aircraftMarker.remove();
                    }
                    aircraftMarker = aMap.addMarker(new MarkerOptions().position(transAircraftPosition)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

//                    aircraftMarker = aMap.addMarker(new MarkerOptions().position(transAircraftPosition)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bg_vspd)));

                    aircraftMarker.setRotateAngle(-(float)heading);


//                    if (aircraftMarker != null) {
//                        aircraftMarker.remove();
//                    }
//                    LatLng TA = new LatLng(TH.latitude + 0.00001 * index++, TH.longitude + 0.0001);
//                    aircraftMarker = aMap.addMarker(new MarkerOptions().position(TA)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    aircraftMarker.setRotateAngle(index++);
                    break;
                }
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.fpv_full_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

//        final Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//
//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        registerDJIMessenger(MessageType.MSG_GET_HOME_LOCATION_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);


        Message message = Message.obtain();
        message.what = MessageType.MSG_GET_HOME_LOCATION;
        sendDJIMessage(message);


//        CameraPosition cameraPosition = new CameraPosition.Builder().target(TH).zoom(18).bearing(0).tilt(0).build();
//        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
//        aMap.moveCamera(update);
//        if (homeMarker != null) {
//            homeMarker.remove();
//        }
//        homeMarker = aMap.addMarker(new MarkerOptions().position(TH)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message2 = Message.obtain();
                message2.what = MessageType.MSG_GET_FC_STATE;
                sendDJIMessage(message2);

//                handler.sendEmptyMessage(MessageType.MSG_GET_FC_STATE_RESPONSE);
            }
        }, 1000, 1000);

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //重连
//            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mBluetoothLeService.connect(mDeviceAddress);
//            }
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (data != null) {
                    //Log.i(TAG, "EXTRA_DATA:" + data);

                    switch(data) {
                        //确认键
                        case "ET":{
                            finish();
                            break;
                        }
                        //返回键
                        case "RT":{
                            finish();
                            break;
                        }
                        //逆时针
                        case "CC":{
                            aMap.moveCamera(CameraUpdateFactory.zoomIn());
                            break;
                        }
                        //顺时针
                        case "CW":{
                            aMap.moveCamera(CameraUpdateFactory.zoomOut());
                            break;
                        }
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);

        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_HOME_LOCATION_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);
        timer.cancel();

        unregisterReceiver(mGattUpdateReceiver);
        //unbindService(mServiceConnection);
//        unregisterReceiver(mReceiver);

    }

    public void finish() {
        super.finish();
        Intent fpvIntent = new Intent(this, FPVActivity.class);
//        fpvIntent.putExtra(FPVActivity.EXTRAS_DEVICE_NAME, mDeviceName);
//        fpvIntent.putExtra(FPVActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        startActivity(fpvIntent);
    }

    private LatLng convert(LatLng sourceLatLng, CoordinateConverter.CoordType coordType ) {
        CoordinateConverter converter  = new CoordinateConverter();
        // CoordType.GPS 待转换坐标类型
        converter.from(coordType);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
}
