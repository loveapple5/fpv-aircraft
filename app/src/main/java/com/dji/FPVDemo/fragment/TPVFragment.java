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
import android.widget.RatingBar;
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
import com.dji.FPVDemo.opengl.TPVGLSurfaceView;

import java.util.ArrayList;
import java.util.Vector;

import dji.common.airlink.DJISignalInformation;
import dji.common.airlink.SignalQualityUpdatedCallback;
import dji.common.battery.DJIBatteryState;
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


public class TPVFragment extends Fragment {

    private static final String TAG = TPVFragment.class.getName();

    public final int MSG_FLIGHT_CONTROLLER_CURRENT_STATE = 1;
    public final int MSG_REMOTE_CONTROLLER_BATTERY_STATE = 2;
    public final int MSG_BATTERY_STATE = 3;
    public final int MSG_CONTROL_SIGNAL = 4;

    public final int MSG_COMPASS_ERROR = 5;

    private DJIAircraft djiAircraft;

    private int wWidth;
    private int wHeight;

    private RelativeLayout rlTop;
    private RelativeLayout rlLeft;
    private RelativeLayout rlBottom;
    private RelativeLayout rlRight;

    private RelativeLayout rlCraftSignal;
    private RelativeLayout rlControllerSignal;
    private TextView tvSafeInfo;

    private RatingBar rbCraftSignal;
    private RatingBar rbControllerSignal;

    private View rlHelmetEnergy;
    private View rlPhoneEnergy;
    private View rlRCEnergy;
    private View rlCraftEnergy;

    private TextView tvFlightHeight;
    private TextView tvFlightDistance;
    private TextView tvFlightSpeed;
    private TextView tvFlightVerticalSpeed;
    private ImageView ivFlightVerticalSpeed;
    private ImageView ivLeftBg;
    private ImageView ivSatellite;
    private ImageView ivRc;

    private TextureView tvPreview;

    private BatteryReceiver mReceiver;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;

    private MapView mapView;
    private AMap aMap;

    // OpenGL componets
    private TPVGLSurfaceView mGLView;

    private LocationManager locationManager;

    private MyLocationListener locationListener;

    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private MySensorEventListener sensorEventListener;

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
            R.drawable.energy_icon_12,
    };

    private int visibilityIndex = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:
                    double speed = bundle.getDouble("speed");
                    float vSpeed = bundle.getFloat("vSpeed");
                    double altitude =  bundle.getDouble("altitude");
                    int distance = (int) bundle.getDouble("distance");
                    int gpsSignalLevel = bundle.getInt("gpsSignalLevel");

                    double longA = bundle.getDouble("longA");
                    double longH = bundle.getDouble("longH");
                    double latA = bundle.getDouble("latA");
                    double latH =  bundle.getDouble("latH");

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
                    rbCraftSignal.setRating(gpsSignalLevel * 20);

                    tvFlightSpeed.setText(strSpeed);
                    tvFlightVerticalSpeed.setText(intVSpeed + "");
                    tvFlightHeight.setText(Math.round(altitude) + "");
                    tvFlightDistance.setText(distance + "");

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
                    int index2 = Math.round((float) strengthPercent / 10);

                    rbControllerSignal.setRating(index2);
                    break;
                case MSG_COMPASS_ERROR:
                    tvSafeInfo.setText("指南针异常，请移动飞机或校准指南针");
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

    public void setHelmetEnergy(int percent) {
        int index = Math.round((float) percent / 8);
        index = index < 0 ? 0 :(index > ENERGY_ICON.length - 1 ? ENERGY_ICON.length - 1 : index);
        rlHelmetEnergy.setBackgroundResource(ENERGY_ICON[index]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tpv, container, false);
        rlTop = (RelativeLayout) view.findViewById(R.id.layout_tpv_top);
        rlLeft = (RelativeLayout) view.findViewById(R.id.layout_tpv_left);
        rlBottom = (RelativeLayout) view.findViewById(R.id.layout_tpv_bottom);
        rlRight = (RelativeLayout) view.findViewById(R.id.layout_tpv_right);
        rlCraftSignal = (RelativeLayout) view.findViewById(R.id.layout_craft_signal);
        rlControllerSignal = (RelativeLayout) view.findViewById(R.id.layout_controller_signal);
        tvSafeInfo = (TextView) view.findViewById(R.id.tv_safe_info);
        rbCraftSignal = (RatingBar) view.findViewById(R.id.rb_craft_signal);
        rbControllerSignal = (RatingBar) view.findViewById(R.id.rb_controller_signal);
        rlHelmetEnergy = view.findViewById(R.id.rl_helmet);
        rlPhoneEnergy = view.findViewById(R.id.rl_phone);
        rlRCEnergy = view.findViewById(R.id.rl_controller);
        rlCraftEnergy = view.findViewById(R.id.rl_craft);
        tvFlightHeight = (TextView) view.findViewById(R.id.tv_flight_height);
        tvFlightDistance = (TextView) view.findViewById(R.id.tv_flight_distance);
        tvFlightSpeed = (TextView) view.findViewById(R.id.tv_flight_speed);
        tvFlightVerticalSpeed = (TextView) view.findViewById(R.id.tv_flight_vertical_speed);
        ivFlightVerticalSpeed = (ImageView) view.findViewById(R.id.iv_flight_vertical_speed);
        //ivLeftBg = (ImageView) view.findViewById(R.id.iv_fpv_left_bg);
        tvPreview = (TextureView) view.findViewById(R.id.tv_preview);
        //tvPreview.setSurfaceTextureListener(new PreviewSurfaceTextureListener());
        mapView = (MapView)view.findViewById(R.id.mv_map);

        ivSatellite = (ImageView) view.findViewById(R.id.iv_sat);
        ivRc = (ImageView) view.findViewById(R.id.iv_rc);

        int paddingText = (int) (wWidth * 0.013);
        tvSafeInfo.setPadding(paddingText, 0, paddingText, 0);

        mGLView = new TPVGLSurfaceView(getActivity());
        mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLView.setZOrderOnTop(true);
        mGLView.setVisibility(View.GONE);
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

        if(ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            if(locationManager != null) {
                locationManager.removeUpdates(locationListener);
                locationManager = null;
            }
        }
        mSensorManager.unregisterListener(sensorEventListener);
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

            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.setUpdateSystemStateCallback(new FlightControllerCallback());
            DJIRemoteController remoteController = djiAircraft.getRemoteController();
            remoteController.setBatteryStateUpdateCallback(new RCBatteryStateCallback());
        }

        visibilityIndex++;
        if(visibilityIndex > 1) {
            mGLView.setVisibility(View.VISIBLE);
        }
        // mGLView.setVisibility(View.VISIBLE);
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
            double altitude;
//            if (FCState.isUltrasonicBeingUsed()) {
//                altitude = FCState.getUltrasonicHeight();
//            } else {
//                altitude = FCState.getAircraftLocation().getAltitude();
//            }

            altitude = FCState.getAircraftLocation().getAltitude();

            double longA = FCState.getAircraftLocation().getCoordinate2D().getLongitude();
            double longH = FCState.getHomeLocation().getLongitude();
            double latA = FCState.getAircraftLocation().getCoordinate2D().getLatitude();
            double latH = FCState.getHomeLocation().getLatitude();

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

            if(djiAircraft != null) {
                DJIFlightController flightController = djiAircraft.getFlightController();
                Double heading = flightController.getCompass().getHeading();
                Double AircraftPitch = FCState.getAttitude().pitch;
                Double AircraftRoll = FCState.getAttitude().roll;
                Double AircraftYaw = FCState.getAttitude().yaw;

                bundle.putDouble("AircraftPitch", AircraftPitch);
                bundle.putDouble("AircraftRoll", AircraftRoll);
                bundle.putDouble("AircraftYaw", AircraftYaw);
                bundle.putDouble("Head", heading);

                boolean comPassError = flightController.getCompass().hasError();
                if(comPassError) {
                    Message msg2 = Message.obtain();
                    msg2.what = MSG_COMPASS_ERROR;
                    mHandler.sendMessage(msg2);
                }
            }

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
            for(DJISignalInformation antenna:antennas){
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
            for(DJISignalInformation antenna:antennas){
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
            double ORIENTATION =  Math.toDegrees(values[0]);
            double PITCH = Math.toDegrees(values[1]);
            double ROLL = Math.toDegrees(values[2]);

            Log.i(TAG, "ORIENTATION:" + ORIENTATION);
            Log.i(TAG, "PITCH" + PITCH);
            Log.i(TAG, "ROLL" + ROLL);
            if(Math.abs(ORIENTATION - lastOrientation) >= 3 || Math.abs(PITCH - lastPitch) >= 3 || Math.abs(ROLL - lastRoll) >= 3) {
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
}
