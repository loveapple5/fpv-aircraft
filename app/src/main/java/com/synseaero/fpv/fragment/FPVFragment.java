package com.synseaero.fpv.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.synseaero.fpv.FPVActivity;
import com.synseaero.fpv.FPVDemoApplication;
import com.synseaero.fpv.opengl.MyGLSurfaceView;
import com.synseaero.fpv.util.DensityUtil;
import com.synseaero.fpv.util.StringUtils;
import com.synseaero.fpv.view.CircleMenuLayout;

import java.util.ArrayList;
import java.util.Vector;

import dji.common.airlink.DJISignalInformation;
import dji.common.airlink.SignalQualityUpdatedCallback;
import dji.common.battery.DJIBatteryState;
import dji.common.camera.DJICameraExposureParameters;
import dji.common.camera.DJICameraSettingsDef;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.product.Model;
import dji.common.remotecontroller.DJIRCBatteryInfo;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIAuxLink;
import dji.sdk.airlink.DJILBAirLink;
import dji.sdk.airlink.DJIOcuSyncLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


public class FPVFragment extends Fragment {

    private static final String TAG = FPVFragment.class.getName();

    public static final int MSG_FLIGHT_CONTROLLER_CURRENT_STATE = 1;
    public static final int MSG_REMOTE_CONTROLLER_BATTERY_STATE = 2;
    public static final int MSG_BATTERY_STATE = 3;
    public static final int MSG_CONTROL_SIGNAL = 4;
    public static final int MSG_CAMERA_INFO = 5;

    private DJIAircraft djiAircraft;
    private DJIFlightController djiFlightController;
    private DJICamera djiCamera;

    private int wWidth;
    private int wHeight;

    private RelativeLayout rlTop;
    private RelativeLayout rlLeft;
    private LinearLayout llBottom;
    private RelativeLayout rlRight;

    private RelativeLayout rlCraftSignal;
    private RelativeLayout rlControllerSignal;
    private TextView tvSafeInfo;

    private ImageView ivCraftSignal;
    private ImageView ivControllerSignal;

    private View rlHelmetEnergy;
    private View rlPhoneEnergy;
    private View rlRCEnergy;
    private View rlCraftEnergy;

    private TextView tvFlightHeight;
    private TextView tvFlightDistance;
    private TextView tvFlightSpeed;
    private TextView tvFlightVerticalSpeed;
    private TextView tvCameraShutterSpeed;
    private TextView tvCameraAperture;
    private TextView tvCameraISO;
    private TextView tvCameraEV;
    private ImageView ivFlightVerticalSpeed;
    private ImageView ivLeftBg;
    private ImageView ivSatellite;
    private ImageView ivRc;
    private ImageView ivDirection;
    private LinearLayout llFpvCamera;
    private RelativeLayout rlTpvCamera;
    private LinearLayout llPreview;


    private TextureView tvPreview;
    //private TextureView tvTpvPreview;

    private BatteryReceiver mReceiver;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    // Codec for video live view
    private PreviewSurfaceTextureListener fpvTextureListener = new PreviewSurfaceTextureListener();

    private MapView mapView;
    private AMap aMap;

    // OpenGL componets
    private MyGLSurfaceView mGLView;

    private CircleMenuLayout menuLayout;
    //private TextView tvMenuLabel;

    public static final int MODE_TPV = 1;
    public static final int MODE_FPV = 2;
    public static final int MODE_MENU = 3;

    private int mode = MODE_TPV;
    private int lastMode = MODE_TPV;

//    private static final int SIGNAL_ICON[] = {
//            R.drawable.signal_icon_0,
//            R.drawable.signal_icon_1,
//            R.drawable.signal_icon_2,
//            R.drawable.signal_icon_3,
//            R.drawable.signal_icon_4
//    };

    private int[] mItemImgs = new int[]{
            com.synseaero.fpv.R.drawable.menu_map, com.synseaero.fpv.R.drawable.menu_luminance, com.synseaero.fpv.R.drawable.menu_helmet,
            com.synseaero.fpv.R.drawable.menu_camera, com.synseaero.fpv.R.drawable.menu_video, com.synseaero.fpv.R.drawable.menu_pan, com.synseaero.fpv.R.drawable.menu_setting
    };

    private int[] mItemsText = new int[]{
            com.synseaero.fpv.R.string.map, com.synseaero.fpv.R.string.brightness, com.synseaero.fpv.R.string.helmet, com.synseaero.fpv.R.string.photo, com.synseaero.fpv.R.string.video, com.synseaero.fpv.R.string.gimbal,
            com.synseaero.fpv.R.string.fc
    };

    private static final int ENERGY_ICON[] = {
            com.synseaero.fpv.R.drawable.energy_icon_1,
            com.synseaero.fpv.R.drawable.energy_icon_2,
            com.synseaero.fpv.R.drawable.energy_icon_3,
            com.synseaero.fpv.R.drawable.energy_icon_4,
            com.synseaero.fpv.R.drawable.energy_icon_5,
            com.synseaero.fpv.R.drawable.energy_icon_6,
            com.synseaero.fpv.R.drawable.energy_icon_7,
            com.synseaero.fpv.R.drawable.energy_icon_8,
            com.synseaero.fpv.R.drawable.energy_icon_9,
            com.synseaero.fpv.R.drawable.energy_icon_10,
            com.synseaero.fpv.R.drawable.energy_icon_11,
    };

    private LocationManager locationManager;

    private MyLocationListener locationListener;

    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private MySensorEventListener sensorEventListener;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:
                    double speed = bundle.getDouble("speed");
                    float vSpeed = bundle.getFloat("vSpeed");
                    double altitude = bundle.getDouble("altitude");
                    int distance = (int) bundle.getDouble("distance");
                    int gpsSignalLevel = bundle.getInt("gpsSignalLevel");

                    double longA = bundle.getDouble("longA");
                    double longH = bundle.getDouble("longH");
                    double latA = bundle.getDouble("latA");
                    double latH = bundle.getDouble("latH");

                    String strSpeed = String.valueOf(speed);
                    if (speed < 2.001 && speed > 0.001) {
                        strSpeed = String.format("%.1f", speed);
                    }

                    if (vSpeed <= 0.001 && vSpeed >= -0.0001) {
                        vSpeed = 0;
                    }

                    int intVSpeed = (int) vSpeed;

                    if (gpsSignalLevel > 5 || gpsSignalLevel < 0) {
                        gpsSignalLevel = 0;
                    }

                    tvFlightSpeed.setText(strSpeed);
                    tvFlightVerticalSpeed.setText(intVSpeed + "");
                    tvFlightHeight.setText(Math.round(altitude) + "");
                    tvFlightDistance.setText(distance + "");

                    Double AircraftPitch = bundle.getDouble("AircraftPitch");
                    Double AircraftRoll = bundle.getDouble("AircraftRoll");
                    Double AircraftYaw = bundle.getDouble("AircraftYaw");
                    Double Heading = bundle.getDouble("Head"); // aircraft compass heading
                    String Fheading = String.format("%.0f", Heading);
                    String Sptich = String.format("%.0f", AircraftPitch);
                    String Sroll = String.format("%.0f", AircraftRoll);
                    mGLView.setHeadingAngle(Float.parseFloat(Fheading));
                    mGLView.setAttitude(Float.parseFloat(Sptich), Float.parseFloat(Sroll));

                    ivDirection.setRotation(-Heading.floatValue());
                    //ivCraftSignal.setImageResource(SIGNAL_ICON[gpsSignalLevel]);

                    Vector<Double> vPhone = new Vector<>();
                    vPhone.add(longH * Math.PI / 180);
                    vPhone.add(latH * Math.PI / 180);
                    vPhone.add(0d);
                    mGLView.setvPhoneLBH(vPhone);
                    Vector<Double> vAircraft = new Vector<>();
                    vAircraft.add(longA * Math.PI / 180);
                    vAircraft.add(latA * Math.PI / 180);
                    vAircraft.add(altitude);
                    mGLView.setvAircraftLBH(vAircraft);
                    break;
                case MSG_REMOTE_CONTROLLER_BATTERY_STATE:
                    int remainingPercent = bundle.getInt("remainingPercent");
                    int index = Math.round((float) remainingPercent / 8) - 1;
                    index = index < 0 ? 0 : (index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
                    rlRCEnergy.setBackgroundResource(ENERGY_ICON[index]);
                    break;
                case MSG_BATTERY_STATE:
                    int aircraftRemainingPercent = bundle.getInt("remainingPercent");
                    int aircraftIndex = Math.round((float) aircraftRemainingPercent / 8) - 1;
                    aircraftIndex = aircraftIndex < 0 ? 0 : (aircraftIndex > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : aircraftIndex);
                    rlCraftEnergy.setBackgroundResource(ENERGY_ICON[aircraftIndex]);
                    break;
                case MSG_CONTROL_SIGNAL:
                    int strengthPercent = bundle.getInt("strengthPercent");
                    int index2 = Math.round((float) strengthPercent / 25);

                    //ivControllerSignal.setImageResource(SIGNAL_ICON[index2]);
                    break;
                case MSG_CAMERA_INFO:
                    String shutterSpeed = bundle.getString("shutterSpeed");
                    if (shutterSpeed != null) {
                        tvCameraShutterSpeed.setText(shutterSpeed);
                    }
                    String aperture = bundle.getString("aperture");
                    if (aperture != null) {
                        tvCameraAperture.setText(aperture);
                    }
                    String ISO = bundle.getString("ISO");
                    if (ISO != null) {
                        tvCameraISO.setText(ISO);
                    }
                    String EV = bundle.getString("EV");
                    if (EV != null) {
                        tvCameraEV.setText(EV);
                    }
                    break;
            }

            return true;

        }

    });

    public void setMode(int mode) {
        this.lastMode = this.mode;
        this.mode = mode;
        if (this.mode == MODE_FPV) {
            this.rlCraftSignal.setRotation(8);
            this.rlCraftSignal.setRotationY(20);
            RelativeLayout.LayoutParams paramsCraft = (RelativeLayout.LayoutParams) this.rlCraftSignal.getLayoutParams();
            int marginTopCraft = DensityUtil.dip2px(getContext(), 8);
            paramsCraft.setMargins(0, marginTopCraft, 0, 0);
            this.rlCraftSignal.setLayoutParams(paramsCraft);
            this.rlControllerSignal.setRotation(-8);
            this.rlControllerSignal.setRotationY(-20);
            RelativeLayout.LayoutParams paramsController = (RelativeLayout.LayoutParams) this.rlControllerSignal.getLayoutParams();
            int marginTopController = DensityUtil.dip2px(getContext(), 8);
            paramsController.setMargins(0, marginTopController, 0, 0);
            this.rlControllerSignal.setLayoutParams(paramsController);
            this.rlLeft.setRotationY(20);
            this.rlRight.setRotationY(-20);
            this.llFpvCamera.setVisibility(View.VISIBLE);
            this.rlTpvCamera.setVisibility(View.GONE);
            this.ivDirection.setVisibility(View.VISIBLE);
            this.menuLayout.setVisibility(View.GONE);
            mGLView.setMode(MODE_FPV);

            rlTpvCamera.removeAllViews();
            tvPreview = new TextureView(getContext());
            fpvTextureListener = new PreviewSurfaceTextureListener();
            tvPreview.setSurfaceTextureListener(fpvTextureListener);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            llPreview.addView(tvPreview, param);

            menuLayout.setVisibility(View.GONE);
        } else if (this.mode == MODE_TPV) {
            this.rlCraftSignal.setRotation(0);
            this.rlCraftSignal.setRotationY(0);
            RelativeLayout.LayoutParams paramsCraft = (RelativeLayout.LayoutParams) this.rlCraftSignal.getLayoutParams();
            int marginTopCraft = DensityUtil.dip2px(getContext(), 15);
            paramsCraft.setMargins(0, marginTopCraft, 0, 0);
            this.rlCraftSignal.setLayoutParams(paramsCraft);
            this.rlControllerSignal.setRotation(0);
            this.rlControllerSignal.setRotationY(0);
            RelativeLayout.LayoutParams paramsController = (RelativeLayout.LayoutParams) this.rlControllerSignal.getLayoutParams();
            int marginTopController = DensityUtil.dip2px(getContext(), 15);
            paramsController.setMargins(0, marginTopController, 0, 0);
            this.rlControllerSignal.setLayoutParams(paramsController);
            this.rlLeft.setRotationY(0);
            this.rlRight.setRotationY(0);
            this.llFpvCamera.setVisibility(View.GONE);
            this.rlTpvCamera.setVisibility(View.VISIBLE);
            this.ivDirection.setVisibility(View.GONE);
            this.menuLayout.setVisibility(View.VISIBLE);
            mGLView.setMode(MODE_TPV);

            llPreview.removeAllViews();
            tvPreview = new TextureView(getContext());
            fpvTextureListener = new PreviewSurfaceTextureListener();
            tvPreview.setSurfaceTextureListener(fpvTextureListener);

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 100), DensityUtil.dip2px(getContext(), 55));
            rlTpvCamera.addView(tvPreview, param);
            menuLayout.setVisibility(View.GONE);
        } else if (this.mode == MODE_MENU) {
            this.ivDirection.setVisibility(View.GONE);
            menuLayout.setVisibility(View.VISIBLE);
            mGLView.setMode(MODE_MENU);
        }

    }

    public int getMode() {
        return this.mode;
    }

    public int getLastMode() {
        return this.lastMode;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();

        WindowManager wm = getActivity().getWindowManager();
        wWidth = wm.getDefaultDisplay().getWidth();
        wHeight = wm.getDefaultDisplay().getHeight();

        mReceivedVideoDataCallBack = new DJICamera.CameraReceivedVideoDataCallback() {

            @Override
            public void onResult(byte[] videoBuffer, int size) {
                fpvTextureListener.sendDataToDecoder(videoBuffer, size);
            }
        };

        mReceiver = new BatteryReceiver();

        // 实例化传感器管理者
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorEventListener = new MySensorEventListener();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        // criteria.setAltitudeRequired(false);//不要求海拔信息
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationListener = new MyLocationListener();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
                boolean netWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                Log.w(TAG, "netWork:" + netWork);
                boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                Log.w(TAG, "gps:" + gps);
            } else {
                Log.w(TAG, "checkSelfPermission failed");
            }
        } else {
            Log.w(TAG, "locationManager null");
        }

        mSensorManager.registerListener(sensorEventListener, accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, magnetic, Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void setHelmetEnergy(int percent) {
        int index = Math.round((float) percent / 8) - 1;
        index = index < 0 ? 0 : (index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
        rlHelmetEnergy.setBackgroundResource(ENERGY_ICON[index]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.synseaero.fpv.R.layout.fragment_fpv, container, false);
        rlTop = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_top);
        rlLeft = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_left);
        llBottom = (LinearLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_bottom);
        rlRight = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_right);
        rlCraftSignal = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_craft_signal);
        rlControllerSignal = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_controller_signal);
        tvSafeInfo = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_fpv_safe_info);
//        ivCraftSignal = (ImageView) view.findViewById(R.id.iv_craft_signal);
//        ivControllerSignal = (ImageView) view.findViewById(R.id.iv_controller_signal);
        rlHelmetEnergy = view.findViewById(com.synseaero.fpv.R.id.rl_fpv_helmet);
        rlPhoneEnergy = view.findViewById(com.synseaero.fpv.R.id.rl_fpv_phone);
        rlRCEnergy = view.findViewById(com.synseaero.fpv.R.id.rl_fpv_controller);
        rlCraftEnergy = view.findViewById(com.synseaero.fpv.R.id.rl_fpv_craft);
        tvFlightHeight = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_fpv_flight_height);
        tvFlightDistance = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_fpv_flight_distance);
        tvFlightSpeed = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_fpv_flight_speed);
        tvFlightVerticalSpeed = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_fpv_flight_vertical_speed);
        ivFlightVerticalSpeed = (ImageView) view.findViewById(com.synseaero.fpv.R.id.iv_fpv_flight_vertical_speed);
        //ivLeftBg = (ImageView) view.findViewById(R.id.iv_fpv_left_bg);
        //tvPreview = (TextureView) view.findViewById(R.id.tv_fpv_preview);

        //tvTpvPreview = (TextureView) view.findViewById(R.id.tv_tpv_preview);
        //tvPreview.setSurfaceTextureListener(new PreviewSurfaceTextureListener());
        mapView = (MapView) view.findViewById(com.synseaero.fpv.R.id.mv_fpv_map);

        ivSatellite = (ImageView) view.findViewById(com.synseaero.fpv.R.id.iv_fpv_sat);
        ivRc = (ImageView) view.findViewById(com.synseaero.fpv.R.id.iv_fpv_rc);

        int paddingText = (int) (wWidth * 0.013);
        tvSafeInfo.setPadding(paddingText, 0, paddingText, 0);

        tvCameraShutterSpeed = (TextView) view.findViewById(com.synseaero.fpv.R.id.fpv_camera_shutter_speed);
        tvCameraAperture = (TextView) view.findViewById(com.synseaero.fpv.R.id.fpv_camera_aperture);
        tvCameraISO = (TextView) view.findViewById(com.synseaero.fpv.R.id.fpv_camera_iso);
        tvCameraEV = (TextView) view.findViewById(com.synseaero.fpv.R.id.fpv_camera_ev);

        ivDirection = (ImageView) view.findViewById(com.synseaero.fpv.R.id.iv_fpv_direction);

        llFpvCamera = (LinearLayout) view.findViewById(com.synseaero.fpv.R.id.layout_fpv_camera);
        rlTpvCamera = (RelativeLayout) view.findViewById(com.synseaero.fpv.R.id.layout_tpv_camera);
        llPreview = (LinearLayout) view.findViewById(com.synseaero.fpv.R.id.ll_preview);

        menuLayout = (CircleMenuLayout) view.findViewById(com.synseaero.fpv.R.id.menu_tpv);
        menuLayout.setMenuItemCount(18);
        menuLayout.setMenuItemIcons(mItemImgs);
        String[] itemTexts = new String[mItemsText.length];
        for (int i = 0; i < mItemsText.length; i++) {
            itemTexts[i] = getString(mItemsText[i]);
        }
        menuLayout.setMenuItemTexts(itemTexts);
        menuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(int index) {
                //Toast.makeText(getContext(), pos + "", Toast.LENGTH_SHORT).show();
                FPVActivity activity = (FPVActivity) getActivity();
                activity.switchMenu(index);
            }

            @Override
            public void itemCenterClick(View view) {
                //int index = (int) view.getTag();
                FPVActivity activity = (FPVActivity) getActivity();
                activity.showMenu();
                //tvMenuLabel.setVisibility(View.VISIBLE);
                //Toast.makeText(getContext(), view.getTag().toString() + "", Toast.LENGTH_SHORT).show();
            }
        });


        mGLView = new MyGLSurfaceView(getActivity());
        mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLView.setZOrderOnTop(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ((RelativeLayout) view).addView(mGLView, params);

        tvFlightVerticalSpeed.addTextChangedListener(new VSpeedWatcher());

        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        //aMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng shenzhen = new LatLng(22.5362, 113.9454);
        aMap.addMarker(new MarkerOptions().position(shenzhen).title("Marker in Shenzhen"));
        aMap.moveCamera(CameraUpdateFactory.newLatLng(shenzhen));

        setMode(MODE_TPV);
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();

        ivDirection.setImageDrawable(null);

        mSensorManager.unregisterListener(sensorEventListener);

        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
        mGLView.onResume();
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();

        if (product != null && product.isConnected()) {
            if (!product.getModel().equals(Model.UnknownAircraft)) {
                DJICamera camera = product.getCamera();
                if (camera != null) {
                    // Set the callback
                    camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
                    //tvPreview.setSurfaceTextureListener(fpvTextureListener);
                    //tvTpvPreview.setSurfaceTextureListener(tpvTextureListener);
                }

            }
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);//注册BroadcastReceiver

        //Log.d("djiAircraft", djiAircraft.toString());

        if (djiAircraft != null) {
            //飞机电池电量
            DJIBattery djiBattery = djiAircraft.getBattery();
            djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());

            //遥控信号强度
            //上行信号强度
            DJIAirLink airLink = djiAircraft.getAirLink();
            Model model = djiAircraft.getModel();
            if (model == Model.Phantom_3_Standard || model == Model.Phantom_3_4K) {

                DJIAuxLink auxLink = airLink.getAuxLink();
                auxLink.setAuxLinkUpdatedRemoteControllerSignalInformationCallback(new AuxRCSignalCallback());

            } else if (model == Model.MavicPro) {
                DJIOcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                ocuSyncLink.setUplinkSignalQualityUpdatedCallback(new OcuSignalCallback());
                //ocuSyncLink.setDownlinkSignalQualityUpdatedCallback();
            } else {
                DJILBAirLink lbAirLink = airLink.getLBAirLink();
                //下行
                lbAirLink.setDJILBAirLinkUpdatedLightbridgeModuleSignalInformationCallback(new LBRCSignalCallback());
                //上行
                //lbAirLink.setLBAirLinkUpdatedRemoteControllerSignalInformationCallback();
            }

            djiFlightController = djiAircraft.getFlightController();
            djiFlightController.setUpdateSystemStateCallback(new FlightControllerCallback());
            DJIRemoteController remoteController = djiAircraft.getRemoteController();
            remoteController.setBatteryStateUpdateCallback(new RCBatteryStateCallback());

            djiCamera = djiAircraft.getCamera();
            djiCamera.setCameraUpdatedCurrentExposureValuesCallback(new DJICamera.CameraUpdatedCurrentExposureValuesCallback() {

                @Override
                public void onResult(DJICameraExposureParameters exposure) {
                    DJICameraSettingsDef.CameraAperture aperture = exposure.getAperture();
                    DJICameraSettingsDef.CameraShutterSpeed speed = exposure.getShutterSpeed();
                    DJICameraSettingsDef.CameraISO ISO = exposure.getISO();
                    DJICameraSettingsDef.CameraExposureCompensation compensation = exposure.getExposureCompensation();

                    if (aperture == null || speed == null || ISO == null || compensation == null) {
                        return;
                    }

                    String strSpeed = "";
                    float flSpeed = speed.value();

                    if (flSpeed >= 1) {
                        strSpeed = String.format("%.2f", flSpeed);
                    } else {
                        float denominator = 1 / flSpeed;
                        StringBuilder sb = new StringBuilder();
                        strSpeed = sb.append("1/").append(String.format("%.2f", denominator)).toString();
                    }

                    //Log.d("aperture", aperture.value() + "");
                    //Log.d("ISO", ISO.value() + "");

                    float flAperture = (float) aperture.value() / 100;
                    String strAperture = String.format("%.1f", flAperture);

                    String strISO = StringUtils.getISOString(ISO);
                    String EV = StringUtils.getEVString(compensation);

                    Message msg = Message.obtain();
                    msg.what = MSG_CAMERA_INFO;
                    Bundle bundle = new Bundle();
                    bundle.putString("shutterSpeed", strSpeed);
                    bundle.putString("aperture", "f " + strAperture);
                    bundle.putString("ISO", "ISO " + strISO);
                    bundle.putString("EV", "EV " + EV);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });

        }
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
        mGLView.onPause();

        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.setUpdateSystemStateCallback(null);
            DJIBattery djiBattery = djiAircraft.getBattery();
            djiBattery.setBatteryStateUpdateCallback(null);

            DJICamera camera = djiAircraft.getCamera();
            if (camera != null) {
                // Reset the callback
                camera.setDJICameraReceivedVideoDataCallback(null);
            }
        }

        getActivity().unregisterReceiver(mReceiver);
    }

    class PreviewSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        private DJICodecManager mCodecManager = null;

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d("surface", "onSurfaceTextureAvailable");
            if (mCodecManager != null) {
                mCodecManager.cleanSurface();
                mCodecManager = null;
            }
            mCodecManager = new DJICodecManager(getActivity(), surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d("surface", "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d("surface", "onSurfaceTextureDestroyed");
            if (mCodecManager != null) {
                mCodecManager.cleanSurface();
                mCodecManager = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Log.d("surface", "onSurfaceTextureUpdated");
        }

        public void sendDataToDecoder(byte[] videoBuffer, int size) {
            if (mCodecManager != null) {
                mCodecManager.sendDataToDecoder(videoBuffer, size);
            }
        }
    }

    class FlightControllerCallback implements DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback {

        @Override
        public void onResult(DJIFlightControllerCurrentState FCState) {
            double speed = Math.sqrt(Math.pow(FCState.getVelocityX(), 2) + Math.pow(FCState.getVelocityY(), 2));
            float vSpeed = -1 * FCState.getVelocityZ();
            // get aircraft altitude
            double altitude;
            if (FCState.isUltrasonicBeingUsed()) {
                altitude = FCState.getUltrasonicHeight();
            } else {
                altitude = FCState.getAircraftLocation().getAltitude();
            }

            double longA = FCState.getAircraftLocation().getCoordinate2D().getLongitude();
            double longH = FCState.getHomeLocation().getLongitude();
            double latA = FCState.getAircraftLocation().getCoordinate2D().getLatitude();
            double latH = FCState.getHomeLocation().getLatitude();

            FCState.getAircraftLocation().getLongitude();
            FCState.getAircraftLocation().getLatitude();

            double radLatA = Math.toRadians(FCState.getAircraftLocation().getCoordinate2D().getLatitude());
            double radLatH = Math.toRadians(FCState.getHomeLocation().getLatitude());
            double a = radLatA - radLatH;
            double b = Math.toRadians(FCState.getAircraftLocation().getCoordinate2D().getLongitude())
                    - Math.toRadians(FCState.getHomeLocation().getLongitude());
            double dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                    Math.cos(radLatA) * Math.cos(radLatH) * Math.pow(Math.sin(b / 2), 2)));
            dis = dis * 6378.137; //  the earth radius= 6378.137km
            //  distance into meter
            dis = Math.round(dis * 10000.0) / 10000.0 * 1000.0;

            int gpsSignalLevel = FCState.getGpsSignalStatus().value();

            Double heading = djiFlightController.getCompass().getHeading();
            Double AircraftPitch = FCState.getAttitude().pitch;
            Double AircraftRoll = FCState.getAttitude().roll;
            Double AircraftYaw = FCState.getAttitude().yaw;

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putDouble("speed", speed);
            bundle.putFloat("vSpeed", vSpeed);
            bundle.putDouble("altitude", altitude);
            bundle.putDouble("distance", dis);
            bundle.putInt("gpsSignalLevel", gpsSignalLevel);

            bundle.putDouble("longA", longA);
            bundle.putDouble("longH", longH);
            bundle.putDouble("latA", latA);
            bundle.putDouble("latH", latH);

            bundle.putDouble("AircraftPitch", AircraftPitch);
            bundle.putDouble("AircraftRoll", AircraftRoll);
            bundle.putDouble("AircraftYaw", AircraftYaw);
            bundle.putDouble("Head", heading);

            msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    class VSpeedWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            float vSpeed = Float.valueOf(text);
            if (vSpeed <= 0.001 && vSpeed >= -0.0001) {
                ivFlightVerticalSpeed.setImageResource(0);
            } else if (vSpeed > 0.001) {
                ivFlightVerticalSpeed.setImageResource(com.synseaero.fpv.R.drawable.arrow_up);
            } else {
                ivFlightVerticalSpeed.setImageResource(com.synseaero.fpv.R.drawable.arrow_down);
            }
        }
    }

    private class BatteryReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //获得当前电量
            int current = intent.getExtras().getInt("level");
            //获得总电量
            int total = intent.getExtras().getInt("scale");
            float percent = (float) current * 100 / total;
            int index = Math.round(percent / 10);
            index = index < 0 ? 0 : (index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
            rlPhoneEnergy.setBackgroundResource(ENERGY_ICON[index]);
        }
    }

    class RCBatteryStateCallback implements DJIRemoteController.RCBatteryStateUpdateCallback {

        @Override
        public void onBatteryStateUpdate(DJIRemoteController djiRemoteController, DJIRCBatteryInfo batteryInfo) {
            int remainingPercent = batteryInfo.remainingEnergyInPercent;

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt("remainingPercent", remainingPercent);
            msg.what = MSG_REMOTE_CONTROLLER_BATTERY_STATE;
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        }
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {

        @Override
        public void onResult(DJIBatteryState djiBatteryState) {
            int remainingPercent = djiBatteryState.getBatteryEnergyRemainingPercent();

            Message msg = Message.obtain();
            msg.what = MSG_BATTERY_STATE;
            Bundle bundle = new Bundle();
            bundle.putInt("remainingPercent", remainingPercent);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    class AuxRCSignalCallback implements DJIAuxLink.DJIAuxLinkUpdatedRemoteControllerSignalInformationCallback {

        @Override
        public void onResult(ArrayList<DJISignalInformation> antennas) {
            int percent = 0;
            for (DJISignalInformation antenna : antennas) {
                percent += antenna.getPercent();
            }
            int length = antennas.size();
            percent = percent / length;

            //Log.d("rcSignal", "aux" + percent + "");

            Message msg = Message.obtain();
            msg.what = MSG_CONTROL_SIGNAL;
            Bundle bundle = new Bundle();
            bundle.putInt("strengthPercent", percent);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    class LBRCSignalCallback implements DJILBAirLink.DJILBAirLinkUpdatedLightbridgeModuleSignalInformationCallback {

        @Override
        public void onResult(ArrayList<DJISignalInformation> antennas) {
            int percent = 0;
            for (DJISignalInformation antenna : antennas) {
                percent += antenna.getPercent();
            }
            int length = antennas.size();
            percent = percent / length;

            //Log.d("rcSignal", "lb" + percent + "");

            Message msg = Message.obtain();
            msg.what = MSG_CONTROL_SIGNAL;
            Bundle bundle = new Bundle();
            bundle.putInt("strengthPercent", percent);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    class OcuSignalCallback implements SignalQualityUpdatedCallback {

        @Override
        public void onChange(int strength) {

            //Log.d("rcSignal", "ocu" + strength + "");

            //The signal quality in percent with range [0, 100], where 100 is the best quality
            Message msg = Message.obtain();
            msg.what = MSG_CONTROL_SIGNAL;
            Bundle bundle = new Bundle();
            bundle.putInt("strengthPercent", strength);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            //海拔
            double altitude = location.getAltitude();

            Log.i(TAG, "longitude" + longitude);
            Log.i(TAG, "latitude" + latitude);
            Log.i(TAG, "altitude" + altitude);

            Vector<Double> vPhone = new Vector<Double>();
            vPhone.add(longitude * Math.PI / 180);
            vPhone.add(latitude * Math.PI / 180);
            vPhone.add(altitude);
//            mGLView.setvPhoneLBH(vPhone);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }
            calculateOrientation();
        }

        private double lastOrientation = 0;
        private double lastPitch = 0;
        private double lastRoll = 0;

        // 计算方向
        private void calculateOrientation() {
            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticFieldValues);
            SensorManager.getOrientation(R, values);
            double ORIENTATION = Math.toDegrees(values[0]);
            double PITCH = Math.toDegrees(values[1]);
            double ROLL = Math.toDegrees(values[2]);

            Log.i(TAG, "ORIENTATION:" + ORIENTATION);
            Log.i(TAG, "PITCH" + PITCH);
            Log.i(TAG, "ROLL" + ROLL);
            if (Math.abs(ORIENTATION - lastOrientation) >= 3 || Math.abs(PITCH - lastPitch) >= 3 || Math.abs(ROLL - lastRoll) >= 3) {
                lastOrientation = ORIENTATION;
                lastPitch = PITCH;
                lastRoll = ROLL;
                Vector<Double> aPhone = new Vector<Double>();
                aPhone.add(PITCH * Math.PI / 180);
                aPhone.add(ROLL * Math.PI / 180);
                aPhone.add(ORIENTATION * Math.PI / 180);
                mGLView.setaPhonePRY(aPhone);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

    public void onUpPressed() {
        menuLayout.setAngle(+1);
    }

    public void onDownPressed() {
        menuLayout.setAngle(-1);
    }
}
