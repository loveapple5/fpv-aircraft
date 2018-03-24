package com.synseaero.fpv.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.os.Messenger;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.synseaero.dji.MessageType;
import com.synseaero.fpv.FPVActivity;
import com.synseaero.fpv.MediaService;
import com.synseaero.fpv.R;
import com.synseaero.fpv.model.FlightInformation;
import com.synseaero.fpv.opengl.MyGLSurfaceView;
import com.synseaero.util.DJIUtils;
import com.synseaero.util.DensityUtil;
import com.synseaero.view.CircleMenuLayout;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;

import cn.feng.skin.manager.base.BaseFragment;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.sdkmanager.DJISDKManager;


public class FPVFragment extends BaseFragment {

    private static final String TAG = FPVFragment.class.getName();

//    public static final int MSG_CONTROL_SIGNAL = 4;

    private FPVActivity activity;

    private int wWidth;
    private int wHeight;

    private RelativeLayout rlTop;
    private RelativeLayout rlLeft;
    private LinearLayout llBottom;
    private RelativeLayout rlRight;

    private RelativeLayout rlCraftSignal;
    private RelativeLayout rlControllerSignal;
    private TextView tvSafeInfo;

//    private ImageView ivCraftSignal;
//    private ImageView ivControllerSignal;

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


    //综合信息栏信息map
    private TreeMap<Integer, FlightInformation> flightInfoMap = new TreeMap<>();

    private int[] mItemImgs = new int[]{
            R.drawable.menu_map, R.drawable.menu_luminance, R.drawable.menu_helmet, R.drawable.menu_camera,
//            R.drawable.menu_video, R.drawable.menu_pan,
            R.drawable.menu_setting
    };

    private int[] mItemsText = new int[]{
            R.string.map, R.string.brightness, R.string.helmet, R.string.photo,
            //R.string.video, R.string.gimbal,
            R.string.fc
    };

    private LocationManager locationManager;

    private MyLocationListener locationListener;

    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private MySensorEventListener sensorEventListener;

    private int lowEnergyWarningThreshold = DJIUtils.COMMON_LOW_PERCENT;
    private int goHomeBatteryThreshold = DJIUtils.COMMON_LOW_PERCENT;

    private boolean isFlying = false;
    private long flyingStateChangeTime;
    //0已降落 1起飞中 2飞行中
    private int flyState = 0;

    private RatingBar rbRc;
    private RatingBar rbGps;

    private Timer timer;

    private long enterTime;

    private Marker aircraftMarker;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = new DJICamera.CameraReceivedVideoDataCallback() {

        @Override
        public void onResult(byte[] videoBuffer, int size) {
            if (fpvTextureListener != null) {
                fpvTextureListener.sendDataToDecoder(videoBuffer, size);
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
//                case MSG_CONTROL_SIGNAL: {
//                    int strengthPercent = bundle.getInt("strengthPercent");
//                    int index2 = Math.round((float) strengthPercent / 25);
//
//                    //ivControllerSignal.setImageResource(SIGNAL_ICON[index2]);
//                    break;
//                }
                case MessageType.MSG_GET_FC_STATE_RESPONSE: {
                    double speed = bundle.getDouble("speed", 0);
                    float vSpeed = bundle.getFloat("vSpeed", 0);
                    double altitude = bundle.getDouble("altitude", 0);
                    int distance = (int) bundle.getDouble("distance", 0);
                    //int gpsSignalLevel = bundle.getInt("gpsSignalLevel");

                    double longA = bundle.getDouble("longA", 0);
                    double longH = bundle.getDouble("longH", 0);
                    double latA = bundle.getDouble("latA", 0);
                    double latH = bundle.getDouble("latH", 0);

                    String strSpeed = String.format("%.1f", speed);

                    if (vSpeed <= 0.001 && vSpeed >= -0.0001) {
                        vSpeed = 0;
                    }

                    int intVSpeed = (int) vSpeed;

//                    if (gpsSignalLevel > 5 || gpsSignalLevel < 0) {
//                        gpsSignalLevel = 0;
//                    }

                    tvFlightSpeed.setText(strSpeed);
                    tvFlightVerticalSpeed.setText(intVSpeed + "");
                    tvFlightHeight.setText(Math.round(altitude) + "");
                    tvFlightDistance.setText(distance + "");

                    double AircraftPitch = bundle.getDouble("AircraftPitch", 0);
                    double AircraftRoll = bundle.getDouble("AircraftRoll", 0);
                    double AircraftYaw = bundle.getDouble("AircraftYaw", 0);
                    double Heading = bundle.getDouble("Head"); // aircraft compass heading
//                    String Fheading = String.format("%.0f", Heading);
//                    String Sptich = String.format("%.0f", AircraftPitch);
//                    String Sroll = String.format("%.0f", AircraftRoll);
//                    mGLView.setHeadingAngle((float)Heading);
//                    mGLView.setAttitude(AircraftPitch, AircraftRoll);

                    Vector<Double> aAircraft = new Vector<>();
                    aAircraft.add(Heading);
                    aAircraft.add(AircraftPitch);
                    aAircraft.add(AircraftRoll);
                    mGLView.setAircraftAPR(aAircraft);

                    ivDirection.setRotation(-(float)Heading);
                    //ivCraftSignal.setImageResource(SIGNAL_ICON[gpsSignalLevel]);

                    Vector<Double> vPhone = new Vector<>();
                    vPhone.add(longH * Math.PI / 180);
                    vPhone.add(latH * Math.PI / 180);
                    vPhone.add(0d);
                    mGLView.setPhoneLBH(vPhone);
                    Vector<Double> vAircraft = new Vector<>();
                    vAircraft.add(longA * Math.PI / 180);
                    vAircraft.add(latA * Math.PI / 180);
                    vAircraft.add(altitude);
                    mGLView.setAircraftLBH(vAircraft);

                    LatLng aircraftPosition = new LatLng(latA, longA);
                    LatLng transAircraftPosition = convert(aircraftPosition, CoordinateConverter.CoordType.GPS);
                    CameraPosition cameraPosition = aMap.getCameraPosition();
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(transAircraftPosition, cameraPosition.zoom));

                    if (aircraftMarker != null) {
                        aircraftMarker.remove();
                    }
                    aircraftMarker = aMap.addMarker(new MarkerOptions().position(transAircraftPosition)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    break;
                }
                case MessageType.MSG_GET_RC_BATTERY_STATE_RESPONSE: {
                    int remainingPercent = bundle.getInt("remainingPercent");
                    int index = Math.round((float) remainingPercent / 8) - 1;
                    index = index < 0 ? 0 : index;
                    rlRCEnergy.getBackground().setLevel(index);

                    if (remainingPercent < DJIUtils.COMMON_LOW_PERCENT) {
                        FlightInformation information = new FlightInformation();
                        information.level = 203;
                        information.information = activity.getString(R.string.rc_battery_low_power_hint);
                        information.type = 0;
                        information.voiceId = R.raw.voice07;
                        setComprehensiveInfo(203, information, true);
                    }

                    break;
                }
                case MessageType.MSG_GET_BATTERY_STATE_RESPONSE: {
                    int aircraftRemainingPercent = bundle.getInt("remainingPercent");
                    int aircraftIndex = Math.round((float) aircraftRemainingPercent / 8) - 1;
                    aircraftIndex = aircraftIndex < 0 ? 0 : aircraftIndex;
                    rlCraftEnergy.getBackground().setLevel(aircraftIndex);

                    if (aircraftRemainingPercent < goHomeBatteryThreshold) {
                        FlightInformation information = new FlightInformation();
                        information.level = 202;
                        information.information = activity.getString(R.string.fc_battery_very_low_power_hint);
                        information.type = 0;
                        information.voiceId = R.raw.voice05;
                        setComprehensiveInfo(202, information, true);
                    } else if (aircraftRemainingPercent < lowEnergyWarningThreshold) {
                        FlightInformation information = new FlightInformation();
                        information.level = 202;
                        information.information = activity.getString(R.string.fc_battery_low_power_hint);
                        information.type = 0;
                        information.voiceId = R.raw.voice06;
                        setComprehensiveInfo(202, information, true);
                    }

                    break;
                }
                case MessageType.MSG_GET_CAMERA_EXPOSURE_RESPONSE: {
                    String shutterSpeed = bundle.getString("shutterSpeed", "");
                    tvCameraShutterSpeed.setText(shutterSpeed);

                    String aperture = bundle.getString("aperture", "");
                    tvCameraAperture.setText(aperture);

                    String ISO = bundle.getString("ISO", "");
                    tvCameraISO.setText(ISO);

                    String EV = bundle.getString("EV", "");
                    tvCameraEV.setText(EV);

                    break;
                }
                case MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE: {
                    int percent = bundle.getInt("percent");

                    rbRc.setRating((float) percent / 10);
                    Log.d(TAG, "upLink:" + percent);

                    if (percent < DJIUtils.COMMON_LOW_PERCENT) {
                        FlightInformation information = new FlightInformation();
                        information.level = 103;
                        information.information = activity.getString(R.string.up_link_signal_weak_hint);
                        information.type = 1;
                        setComprehensiveInfo(103, information, true);
                    }
                    break;
                }
                case MessageType.MSG_GET_DOWN_LINK_SIGNAL_QUALITY_RESPONSE: {
                    int percent = bundle.getInt("percent");
                    if (percent < 5) {
                        FlightInformation information = new FlightInformation();
                        information.level = 104;
                        information.information = activity.getString(R.string.down_link_signal_none_hint);
                        information.type = 1;
                        setComprehensiveInfo(104, information, true);
                    } else if (percent < DJIUtils.COMMON_LOW_PERCENT) {
                        FlightInformation information = new FlightInformation();
                        information.level = 104;
                        information.information = activity.getString(R.string.down_link_signal_weak_hint);
                        information.type = 1;
                        information.voiceId = R.raw.voice03;
                        setComprehensiveInfo(104, information, true);
                    }
                    break;
                }
                case MessageType.MSG_GET_GIMBAL_STATE_RESPONSE: {
                    boolean isMotorOverloaded = bundle.getBoolean("isMotorOverloaded", false);
                    if (isMotorOverloaded) {
                        FlightInformation information = new FlightInformation();
                        information.level = 101;
                        information.information = activity.getString(R.string.gimbal_motor_overloaded);
                        information.type = 1;
                        setComprehensiveInfo(101, information, true);
                    }
                    break;
                }
                case MessageType.MSG_GET_SDCARD_STATE_RESPONSE: {
                    boolean isFull = bundle.getBoolean("isFull", false);
                    if (isFull) {
                        FlightInformation information = new FlightInformation();
                        information.level = 105;
                        information.information = activity.getString(R.string.sd_card_full);
                        information.type = 1;
                        setComprehensiveInfo(105, information, false);
                    }
                    break;
                }
                case MessageType.MSG_GET_FC_INFO_STATE_RESPONSE: {
                    int flightModeStrId = bundle.getInt("flightModeStrId");
                    int flightModeVoiceId = bundle.getInt("flightModeVoiceId");
                    boolean isFlying = bundle.getBoolean("isFlying");
                    boolean isHomeSet = bundle.getBoolean("isHomeSet");
                    int gpsSignalStatus = bundle.getInt("gpsSignalStatus");

                    //boolean goHomeCompleted = bundle.getBoolean("goHomeCompleted", false);
                    boolean reachLimitedHeight = bundle.getBoolean("reachLimitedHeight", false);
                    boolean reachLimitedRadius = bundle.getBoolean("reachLimitedRadius", false);

                    //记录起飞时间
                    if (FPVFragment.this.isFlying != isFlying) {
                        FPVFragment.this.isFlying = isFlying;
                        flyingStateChangeTime = System.currentTimeMillis();
                    }

                    //获取当前飞行状态
                    if (!FPVFragment.this.isFlying) {
                        flyState = 0;
                    } else {
                        //起飞5s内
                        if (isHomeSet && flyingStateChangeTime + 5000 > System.currentTimeMillis()) {
                            flyState = 1;
                        } else {
                            flyState = 2;
                        }
                    }

                    //设置gps信号强度
                    int gpsLevel = gpsSignalStatus * 2;
                    if (gpsLevel > 10) {
                        gpsLevel = 0;
                    }
                    rbGps.setRating(gpsLevel);
                    Log.d(TAG, "gpsLevel:" + gpsLevel);

                    if (flightModeStrId != -1) {
                        FlightInformation flightModeInfo = new FlightInformation();
                        flightModeInfo.level = 206;
                        flightModeInfo.information = activity.getString(flightModeStrId);
                        flightModeInfo.type = 0;
                        flightModeInfo.voiceId = flightModeVoiceId;
                        setComprehensiveInfo(206, flightModeInfo, false);
                    }


                    if (reachLimitedHeight) {
                        FlightInformation reachLimitedHeightInfo = new FlightInformation();
                        reachLimitedHeightInfo.level = 208;
                        reachLimitedHeightInfo.information = activity.getString(R.string.reach_limited_height);
                        reachLimitedHeightInfo.type = 0;
                        reachLimitedHeightInfo.voiceId = R.raw.voice18;
                        setComprehensiveInfo(208, reachLimitedHeightInfo, true);
                    }

                    if (reachLimitedRadius) {
                        FlightInformation reachLimitedRadiusInfo = new FlightInformation();
                        reachLimitedRadiusInfo.level = 209;
                        reachLimitedRadiusInfo.information = activity.getString(R.string.reach_limited_radius);
                        reachLimitedRadiusInfo.type = 0;
                        reachLimitedRadiusInfo.voiceId = R.raw.voice19;
                        setComprehensiveInfo(209, reachLimitedRadiusInfo, true);
                    }

                    break;
                }
                case MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE: {
                    goHomeBatteryThreshold = bundle.getInt("threshold");
                    break;
                }
            }
        }
    };

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

    private Messenger messenger = new Messenger(mHandler);

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

            FlightInformation fpvModeInfo = new FlightInformation();
            fpvModeInfo.level = 207;
            fpvModeInfo.information = getString(R.string.change_to_fpv_mode);
            fpvModeInfo.type = 0;
            fpvModeInfo.voiceId = R.raw.voice14;
            setComprehensiveInfo(207, fpvModeInfo, false);
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

            FlightInformation tpvModeInfo = new FlightInformation();
            tpvModeInfo.level = 207;
            tpvModeInfo.information = getString(R.string.change_to_tpv_mode);
            tpvModeInfo.type = 0;
            tpvModeInfo.voiceId = R.raw.voice16;
            setComprehensiveInfo(207, tpvModeInfo, false);

        } else if (this.mode == MODE_MENU) {
            this.ivDirection.setVisibility(View.GONE);
            menuLayout.setVisibility(View.VISIBLE);
            mGLView.setMode(MODE_MENU);
        }

    }

    public Messenger getMessenger() {
        return this.messenger;
    }

    public int getMode() {
        return this.mode;
    }

    public int getLastMode() {
        return this.lastMode;
    }

    public void setActivity(FPVActivity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager wm = getActivity().getWindowManager();
        wWidth = wm.getDefaultDisplay().getWidth();
        wHeight = wm.getDefaultDisplay().getHeight();

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

        SharedPreferences sp = getContext().getSharedPreferences("battery", Context.MODE_PRIVATE);
        lowEnergyWarningThreshold = sp.getInt("lowEnergyWarningThreshold", DJIUtils.COMMON_LOW_PERCENT);

        //相机曝光属性
        activity.registerDJIMessenger(MessageType.MSG_GET_CAMERA_EXPOSURE_RESPONSE, messenger);
        //遥控器状态
        activity.registerDJIMessenger(MessageType.MSG_GET_RC_BATTERY_STATE_RESPONSE, messenger);
        //飞机电池状态
        activity.registerDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        //云台状态
        activity.registerDJIMessenger(MessageType.MSG_GET_GIMBAL_STATE_RESPONSE, messenger);
        //飞控参数信息
        activity.registerDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);
        //飞控状态信息
        activity.registerDJIMessenger(MessageType.MSG_GET_FC_INFO_STATE_RESPONSE, messenger);

        activity.registerDJIMessenger(MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);

        activity.registerDJIMessenger(MessageType.MSG_GET_DOWN_LINK_SIGNAL_QUALITY_RESPONSE, messenger);

        activity.registerDJIMessenger(MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE, messenger);

        //watch camera exposure状态变化
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_CAMERA_EXPOSURE, 0);
        //watch rc电池状态变化
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_RC_BATTERY_STATE, 0);
        //watch飞机电池状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 0);
        //watch云台状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_GIMBAL_STATE, 0);
        //watch上行信号强度
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_UP_LINK_SIGNAL_QUALITY, 0);
        //watch下行信号强度
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_DOWN_LINK_SIGNAL_QUALITY, 0);
        //watch sd卡状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_SDCARD_STATE, 0);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Message message = Message.obtain();
                message.what = MessageType.MSG_GET_FC_STATE;

                activity.sendDJIMessage(message);

                Message message2 = Message.obtain();
                message2.what = MessageType.MSG_GET_FC_INFO_STATE;
                activity.sendDJIMessage(message2);
            }
        }, 2000, 1000);

        enterTime = System.currentTimeMillis();
    }

    public void setHelmetEnergy(int percent) {
        int index = Math.round((float) percent / 8) - 1;
        index = index < 0 ? 0 : index;
        rlHelmetEnergy.getBackground().setLevel(index);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fpv, container, false);
        rlTop = (RelativeLayout) view.findViewById(R.id.layout_fpv_top);
        rlLeft = (RelativeLayout) view.findViewById(R.id.layout_fpv_left);
        llBottom = (LinearLayout) view.findViewById(R.id.layout_fpv_bottom);
        rlRight = (RelativeLayout) view.findViewById(R.id.layout_fpv_right);
        rlCraftSignal = (RelativeLayout) view.findViewById(R.id.layout_fpv_craft_signal);
        rlControllerSignal = (RelativeLayout) view.findViewById(R.id.layout_fpv_controller_signal);
        tvSafeInfo = (TextView) view.findViewById(R.id.tv_fpv_safe_info);
//        ivCraftSignal = (ImageView) view.findViewById(R.id.iv_craft_signal);
//        ivControllerSignal = (ImageView) view.findViewById(R.id.iv_controller_signal);
        rlHelmetEnergy = view.findViewById(R.id.rl_fpv_helmet);
        rlPhoneEnergy = view.findViewById(R.id.rl_fpv_phone);
        rlRCEnergy = view.findViewById(R.id.rl_fpv_controller);
        rlCraftEnergy = view.findViewById(R.id.rl_fpv_craft);
        tvFlightHeight = (TextView) view.findViewById(R.id.tv_fpv_flight_height);
        tvFlightDistance = (TextView) view.findViewById(R.id.tv_fpv_flight_distance);
        tvFlightSpeed = (TextView) view.findViewById(R.id.tv_fpv_flight_speed);
        tvFlightVerticalSpeed = (TextView) view.findViewById(R.id.tv_fpv_flight_vertical_speed);
        ivFlightVerticalSpeed = (ImageView) view.findViewById(R.id.iv_fpv_flight_vertical_speed);
        //ivLeftBg = (ImageView) view.findViewById(R.id.iv_fpv_left_bg);
        //tvPreview = (TextureView) view.findViewById(R.id.tv_fpv_preview);

        //tvTpvPreview = (TextureView) view.findViewById(R.id.tv_tpv_preview);
        //tvPreview.setSurfaceTextureListener(new PreviewSurfaceTextureListener());
        mapView = (MapView) view.findViewById(R.id.mv_fpv_map);

        ivSatellite = (ImageView) view.findViewById(R.id.iv_fpv_sat);
        ivRc = (ImageView) view.findViewById(R.id.iv_fpv_rc);

        int paddingText = (int) (wWidth * 0.013);
        tvSafeInfo.setPadding(paddingText, 0, paddingText, 0);

        tvCameraShutterSpeed = (TextView) view.findViewById(R.id.fpv_camera_shutter_speed);
        tvCameraAperture = (TextView) view.findViewById(R.id.fpv_camera_aperture);
        tvCameraISO = (TextView) view.findViewById(R.id.fpv_camera_iso);
        tvCameraEV = (TextView) view.findViewById(R.id.fpv_camera_ev);

        ivDirection = (ImageView) view.findViewById(R.id.iv_fpv_direction);

        llFpvCamera = (LinearLayout) view.findViewById(R.id.layout_fpv_camera);
        rlTpvCamera = (RelativeLayout) view.findViewById(R.id.layout_tpv_camera);
        llPreview = (LinearLayout) view.findViewById(R.id.ll_preview);

        menuLayout = (CircleMenuLayout) view.findViewById(R.id.menu_tpv);
        menuLayout.setMenuItemCount(12);
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

        rbGps = (RatingBar) view.findViewById(R.id.rb_fpv_craft_signal);
        rbRc = (RatingBar) view.findViewById(R.id.rb_fpv_controller_signal);


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

        //相机曝光属性
        activity.unregisterDJIMessenger(MessageType.MSG_GET_CAMERA_EXPOSURE_RESPONSE, messenger);
        //遥控器状态
        activity.unregisterDJIMessenger(MessageType.MSG_GET_RC_BATTERY_STATE_RESPONSE, messenger);
        //飞机电池状态
        activity.unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        //云台状态
        activity.unregisterDJIMessenger(MessageType.MSG_GET_GIMBAL_STATE_RESPONSE, messenger);
        //飞控参数信息
        activity.unregisterDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);
        //飞控状态信息
        activity.unregisterDJIMessenger(MessageType.MSG_GET_FC_INFO_STATE_RESPONSE, messenger);

        activity.unregisterDJIMessenger(MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);

        activity.unregisterDJIMessenger(MessageType.MSG_GET_DOWN_LINK_SIGNAL_QUALITY_RESPONSE, messenger);

        activity.unregisterDJIMessenger(MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE, messenger);

        //停止watch camera exposure状态变化
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_CAMERA_EXPOSURE, 1);
        //停止watch rc电池状态变化
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_RC_BATTERY_STATE, 1);
        //停止watch飞机电池状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 1);
        //停止watch云台状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_GIMBAL_STATE, 1);
        //停止watch上行信号强度
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_UP_LINK_SIGNAL_QUALITY, 1);
        //停止watch下行信号强度
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_DOWN_LINK_SIGNAL_QUALITY, 1);
        //停止watch sd卡状态
        activity.sendWatchDJIMessage(MessageType.MSG_WATCH_SDCARD_STATE, 1);

        timer.cancel();
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
        mGLView.onResume();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);//注册BroadcastReceiver

        //视频流数据量大，不使用消息系统进行数据传输
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product.isConnected()) {
            DJICamera camera = product.getCamera();
            // Set the callback
            camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
        }

    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
        mGLView.onPause();

        getActivity().unregisterReceiver(mReceiver);


        //视频流数据量大，不使用消息系统进行数据传输
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product.isConnected()) {
            DJICamera camera = product.getCamera();
            // Reset the callback
            camera.setDJICameraReceivedVideoDataCallback(null);
        }


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
                ivFlightVerticalSpeed.setImageLevel(1);
            } else if (vSpeed > 0.001) {
                ivFlightVerticalSpeed.setImageLevel(2);
            } else {
                ivFlightVerticalSpeed.setImageLevel(3);
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
            index = index < 0 ? 0 : index;
            //rlPhoneEnergy.setBackgroundResource(ENERGY_ICON[index]);
            rlPhoneEnergy.getBackground().setLevel(index);
        }
    }

    //手机位置
    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            //海拔
            double altitude = location.getAltitude();

//            Log.i(TAG, "longitude" + longitude);
//            Log.i(TAG, "latitude" + latitude);
//            Log.i(TAG, "altitude" + altitude);

            Vector<Double> vPhone = new Vector<Double>();
            vPhone.add(longitude * Math.PI / 180);
            vPhone.add(latitude * Math.PI / 180);
            vPhone.add(altitude);
//            mGLView.setPhoneLBH(vPhone);
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


    //手机姿态
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

//            Log.i(TAG, "ORIENTATION:" + ORIENTATION);
//            Log.i(TAG, "PITCH" + PITCH);
//            Log.i(TAG, "ROLL" + ROLL);
            if (Math.abs(ORIENTATION - lastOrientation) >= 3 || Math.abs(PITCH - lastPitch) >= 3 || Math.abs(ROLL - lastRoll) >= 3) {
                lastOrientation = ORIENTATION;
                lastPitch = PITCH;
                lastRoll = ROLL;
                Vector<Double> aPhone = new Vector<>();
                aPhone.add(PITCH * Math.PI / 180);
                aPhone.add(ROLL * Math.PI / 180);
                aPhone.add(ORIENTATION * Math.PI / 180);
                mGLView.setPhonePRY(aPhone);

                if (mode == MODE_FPV || (mode == MODE_MENU && lastMode == MODE_FPV)) {
                    //云台俯仰角设置
                    float roll = Math.round(lastRoll);
                    Log.d(TAG, "roll:" + roll);

                    Bundle bundle = new Bundle();
                    bundle.putFloat("pitch", -(roll - 78));
//                    bundle.putFloat("yaw", Math.round(lastOrientation));

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_ROTATE_GIMBAL_BY_ANGLE;
                    message.setData(bundle);
                    activity.sendDJIMessage(message);
                }

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

    /***
     * 设置综合信息栏信息
     */
    private void setComprehensiveInfo(int level, FlightInformation flightInformation, boolean force) {

        long curTime = System.currentTimeMillis();
        //设置等级信息
        FlightInformation beforeInfo = flightInfoMap.get(level);
        if (force || beforeInfo == null || !beforeInfo.equals(flightInformation)) {
            flightInfoMap.put(level, flightInformation);
        }

        //获取当前最高等级的信息
        FlightInformation priorInfo = null;
        Iterator<Map.Entry<Integer, FlightInformation>> it = flightInfoMap.entrySet().iterator();
        for (; it.hasNext(); ) {
            Map.Entry<Integer, FlightInformation> entry = it.next();
            FlightInformation info = entry.getValue();
            if (info != null && info.expiredTime >= curTime) {
                priorInfo = info;
                break;
            }
        }

        int stateStrId = DJIUtils.getMapValue(DJIUtils.flyStateMap, flyState);
        String prefix = activity.getString(stateStrId);

        String showInfo;
        int voiceId = -1;
        //将信息展示在综合信息栏
        if (priorInfo != null) {
            showInfo = prefix + activity.getString(R.string.comma) + priorInfo.information;
            voiceId = priorInfo.voiceId;
        } else {
            showInfo = prefix;
        }
        if (!showInfo.equals(tvSafeInfo.getText().toString())) {
            //Log.d(TAG, "showInfo:" + showInfo);
            tvSafeInfo.setText(showInfo);
            if (voiceId != -1 && curTime - 5000 >= enterTime) {
                Intent intent = new Intent(activity, MediaService.class);
                intent.putExtra("source", voiceId);
                activity.startService(intent);
            }
        }


    }
}
