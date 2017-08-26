package com.dji.FPVDemo.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;
import com.dji.FPVDemo.opengl.MyGLSurfaceView;
import com.dji.FPVDemo.util.StringUtils;

import java.util.ArrayList;

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
    private RelativeLayout rlBottom;
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

    private TextureView tvPreview;

    private BatteryReceiver mReceiver;

    private LocationManager locationManager;

    private MyLocationListener locationListener;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;

    private MapView mapView;
    private AMap aMap;

    // OpenGL componets
    private MyGLSurfaceView mGLView;

    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private MySensorEventListener sensorEventListener;

    private static final int SIGNAL_ICON[] = {
            R.drawable.signal_icon_0,
            R.drawable.signal_icon_1,
            R.drawable.signal_icon_2,
            R.drawable.signal_icon_3,
            R.drawable.signal_icon_4
    };

    private static final int ENERGY_ICON[] = {
            R.drawable.energy_icon_1,
            R.drawable.energy_icon_2,
            R.drawable.energy_icon_3,
            R.drawable.energy_icon_4,
            R.drawable.energy_icon_5,
            R.drawable.energy_icon_6,
            R.drawable.energy_icon_7,
            R.drawable.energy_icon_8,
            R.drawable.energy_icon_9,
            R.drawable.energy_icon_10,
            R.drawable.energy_icon_11,
    };


    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:
                    double speed = bundle.getDouble("speed");
                    float vSpeed = bundle.getFloat("vSpeed");
                    int altitude = (int) bundle.getFloat("altitude");
                    int distance = (int) bundle.getDouble("distance");
                    int gpsSignalLevel = bundle.getInt("gpsSignalLevel");

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
                    tvFlightHeight.setText(altitude + "");
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

                    break;
                case MSG_REMOTE_CONTROLLER_BATTERY_STATE:
                    int remainingPercent = bundle.getInt("remainingPercent");
                    int index = Math.round((float) remainingPercent / 8) - 1;
                    index = index < 0 ? 0 :(index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
                    rlRCEnergy.setBackgroundResource(ENERGY_ICON[index]);
                    break;
                case MSG_BATTERY_STATE:
                    int aircraftRemainingPercent = bundle.getInt("remainingPercent");
                    int aircraftIndex = Math.round((float) aircraftRemainingPercent / 8) - 1;
                    aircraftIndex = aircraftIndex < 0 ? 0 :(aircraftIndex > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : aircraftIndex);
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();

        WindowManager wm = getActivity().getWindowManager();
        wWidth = wm.getDefaultDisplay().getWidth();
        wHeight = wm.getDefaultDisplay().getHeight();

        mReceivedVideoDataCallBack = new DJICamera.CameraReceivedVideoDataCallback() {

            @Override
            public void onResult(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    // Send the raw H264 video data to codec manager for decoding
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
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
    }

    public void setHelmetEnergy(int percent) {
        int index = Math.round((float) percent / 8) - 1;
        index = index < 0 ? 0 :(index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
        rlHelmetEnergy.setBackgroundResource(ENERGY_ICON[index]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fpv, container, false);
        rlTop = (RelativeLayout) view.findViewById(R.id.layout_fpv_top);
        rlLeft = (RelativeLayout) view.findViewById(R.id.layout_fpv_left);
        rlBottom = (RelativeLayout) view.findViewById(R.id.layout_fpv_bottom);
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
        tvPreview = (TextureView) view.findViewById(R.id.tv_fpv_preview);
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

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
        mGLView.onResume();
        DJIBaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            Toast.makeText(getActivity(), R.string.disconnected, Toast.LENGTH_SHORT).show();
        } else {
            tvPreview.setSurfaceTextureListener(new PreviewSurfaceTextureListener());

            if (!product.getModel().equals(Model.UnknownAircraft)) {
                DJICamera camera = product.getCamera();
                if (camera != null) {
                    // Set the callback
                    camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
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
            DJIAirLink airLink = djiAircraft.getAirLink();
            Model model = djiAircraft.getModel();
            if (model == Model.Phantom_3_Standard || model == Model.Phantom_3_4K) {
                DJIAuxLink auxLink = airLink.getAuxLink();
                auxLink.setAuxLinkUpdatedRemoteControllerSignalInformationCallback(new AuxRCSignalCallback());
            } else if (model == Model.MavicPro) {
                DJIOcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                ocuSyncLink.setUplinkSignalQualityUpdatedCallback(new OcuSignalCallback());
            } else {
                DJILBAirLink lbAirLink = airLink.getLBAirLink();
                lbAirLink.setDJILBAirLinkUpdatedLightbridgeModuleSignalInformationCallback(new LBRCSignalCallback());
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

                    if(aperture == null || speed == null || ISO == null || compensation == null) {
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

                    Log.d("aperture", aperture.value() + "");
                    Log.d("ISO", ISO.value() + "");

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

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            // criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
            // criteria.setAltitudeRequired(false);//不要求海拔信息
            if(locationManager != null) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
        mGLView.onPause();
        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            // Reset the callback
            camera.setDJICameraReceivedVideoDataCallback(null);
        }
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.setUpdateSystemStateCallback(null);
            DJIBattery djiBattery = djiAircraft.getBattery();
            djiBattery.setBatteryStateUpdateCallback(null);
        }

        getActivity().unregisterReceiver(mReceiver);
        if(ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
        mSensorManager.unregisterListener(sensorEventListener);
    }

    class PreviewSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mCodecManager = new DJICodecManager(getActivity(), surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (mCodecManager != null) {
                mCodecManager.cleanSurface();
                mCodecManager = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    class FlightControllerCallback implements DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback {

        @Override
        public void onResult(DJIFlightControllerCurrentState FCState) {
            double speed = Math.sqrt(Math.pow(FCState.getVelocityX(), 2) + Math.pow(FCState.getVelocityY(), 2));
            float vSpeed = -1 * FCState.getVelocityZ();
            // get aircraft altitude
            float altitude;
            if (FCState.isUltrasonicBeingUsed()) {
                altitude = FCState.getUltrasonicHeight();
            } else {
                altitude = FCState.getAircraftLocation().getAltitude();
            }

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
            bundle.putFloat("altitude", altitude);
            bundle.putDouble("distance", dis);
            bundle.putInt("gpsSignalLevel", gpsSignalLevel);

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
                ivFlightVerticalSpeed.setImageResource(R.drawable.arrow_up);
            } else {
                ivFlightVerticalSpeed.setImageResource(R.drawable.arrow_down);
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
            index = index < 0 ? 0 :(index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
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

            Log.i(TAG, "longitude" + longitude) ;
            Log.i(TAG, "latitude" + latitude) ;
            Log.i(TAG, "altitude" + altitude) ;
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

        // 计算方向
        private void calculateOrientation() {
            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticFieldValues);
            SensorManager.getOrientation(R, values);
            float ORIENTATION = (float) Math.toDegrees(values[0]);

            float PITCH = (float)Math.toDegrees(values[1]);
            float ROLL = (float)Math.toDegrees(values[2]);

//            Log.i(TAG, "ORIENTATION:" + ORIENTATION);
//            Log.i(TAG, "PITCH" + PITCH);
//            Log.i(TAG, "ROLL" + ROLL);


//            if (values[0] >= -5 && values[0] < 5) {
//                Log.i(TAG, "正北");
//            } else if (values[0] >= 5 && values[0] < 85) {
//                Log.i(TAG, "东北");
//            } else if (values[0] >= 85 && values[0] <= 95) {
//                Log.i(TAG, "正东");
//            } else if (values[0] >= 95 && values[0] < 175) {
//                 Log.i(TAG, "东南");
//            } else if ((values[0] >= 175 && values[0] <= 180)
//                    || (values[0]) >= -180 && values[0] < -175) {
//                Log.i(TAG, "正南");
//            } else if (values[0] >= -175 && values[0] < -95) {
//                 Log.i(TAG, "西南");
//            } else if (values[0] >= -95 && values[0] < -85) {
//                 Log.i(TAG, "正西");
//            } else if (values[0] >= -85 && values[0] < -5) {
//                Log.i(TAG, "西北");
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    }
}
