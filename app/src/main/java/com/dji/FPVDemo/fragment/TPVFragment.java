package com.dji.FPVDemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.product.Model;
import dji.common.remotecontroller.DJIRCBatteryInfo;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;


public class TPVFragment extends Fragment {

    public final int MSG_FLIGHT_CONTROLLER_CURRENT_STATE = 1;
    public final int MSG_REMOTE_CONTROLLER_BATTERY_STATE = 2;

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

    private ImageView ivCraftSignal;
    private ImageView ivControllerSignal;

    private ImageView ivHelmetEnergy;
    private ImageView ivPhoneEnergy;
    private ImageView ivRCEnergy;
    private ImageView ivCraftEnergy;

    private TextView tvFlightHeight;
    private TextView tvFlightDistance;
    private TextView tvFlightSpeed;
    private TextView tvFlightVerticalSpeed;
    private ImageView ivFlightVerticalSpeed;
    private ImageView ivLeftBg;

    private TextureView tvPreview;

    private BatteryReceiver mReceiver;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;


    private static final int SIGNAL_ICON[] = {
            R.drawable.signal_icon_0,
            R.drawable.signal_icon_1,
            R.drawable.signal_icon_2,
            R.drawable.signal_icon_3,
            R.drawable.signal_icon_4
    };

    private static final int ENERGY_ICON[] = {
            R.drawable.energy_icon_0,
            R.drawable.energy_icon_10,
            R.drawable.energy_icon_20,
            R.drawable.energy_icon_30,
            R.drawable.energy_icon_40,
            R.drawable.energy_icon_50,
            R.drawable.energy_icon_60,
            R.drawable.energy_icon_70,
            R.drawable.energy_icon_80,
            R.drawable.energy_icon_90,
            R.drawable.energy_icon_100,
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

                    ivCraftSignal.setImageResource(SIGNAL_ICON[gpsSignalLevel]);

                    break;
                case MSG_REMOTE_CONTROLLER_BATTERY_STATE:
                    int remainingPercent = bundle.getInt("remainingPercent");
                    int index = Math.round((float)remainingPercent / 10);

                    ivRCEnergy.setImageResource(ENERGY_ICON[index]);
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

        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.setUpdateSystemStateCallback(new FlightControllerCallback());
            DJIRemoteController remoteController = djiAircraft.getRemoteController();
            remoteController.setBatteryStateUpdateCallback(new RCBatteryStateCallback());
        }

        mReceiver = new BatteryReceiver();
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
        ivCraftSignal = (ImageView) view.findViewById(R.id.iv_craft_signal);
        ivControllerSignal = (ImageView) view.findViewById(R.id.iv_controller_signal);
        ivHelmetEnergy = (ImageView) view.findViewById(R.id.iv_helmet_energy);
        ivPhoneEnergy = (ImageView) view.findViewById(R.id.iv_phone_energy);
        ivRCEnergy = (ImageView) view.findViewById(R.id.iv_controller_energy);
        ivCraftEnergy = (ImageView) view.findViewById(R.id.iv_craft_energy);
        tvFlightHeight = (TextView) view.findViewById(R.id.tv_flight_height);
        tvFlightDistance = (TextView) view.findViewById(R.id.tv_flight_distance);
        tvFlightSpeed = (TextView) view.findViewById(R.id.tv_flight_speed);
        tvFlightVerticalSpeed = (TextView) view.findViewById(R.id.tv_flight_vertical_speed);
        ivFlightVerticalSpeed = (ImageView) view.findViewById(R.id.iv_flight_vertical_speed);
        ivLeftBg = (ImageView) view.findViewById(R.id.iv_fpv_left_bg);
        tvPreview = (TextureView) view.findViewById(R.id.tv_preview);
        //tvPreview.setSurfaceTextureListener(new PreviewSurfaceTextureListener());

        int paddingText = (int) (wWidth * 0.013);
        tvSafeInfo.setPadding(paddingText, 0, paddingText, 0);

        //top
        RelativeLayout.LayoutParams lpTop = (RelativeLayout.LayoutParams) rlTop.getLayoutParams();
        lpTop.width = (int) (wWidth * 0.221);
        lpTop.height = (int) (wHeight * 0.062);
        lpTop.topMargin = (int) (wHeight * 0.07);
        rlTop.setLayoutParams(lpTop);

        RelativeLayout.LayoutParams lpCraftSignal = (RelativeLayout.LayoutParams) rlCraftSignal.getLayoutParams();
        lpCraftSignal.width = (int) (wWidth * 0.058);
        lpCraftSignal.height = (int) (wHeight * 0.0409);
        rlCraftSignal.setLayoutParams(lpCraftSignal);

        RelativeLayout.LayoutParams lpControllerSignal = (RelativeLayout.LayoutParams) rlControllerSignal.getLayoutParams();
        lpControllerSignal.width = (int) (wWidth * 0.058);
        lpControllerSignal.height = (int) (wHeight * 0.0409);
        rlControllerSignal.setLayoutParams(lpControllerSignal);

        RelativeLayout.LayoutParams lpSafeInfo = (RelativeLayout.LayoutParams) tvSafeInfo.getLayoutParams();
        lpSafeInfo.width = (int) (wWidth * 0.126);
        lpSafeInfo.height = (int) (wHeight * 0.0409);
        tvSafeInfo.setLayoutParams(lpSafeInfo);
        tvSafeInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (lpSafeInfo.height * 0.7));

        RelativeLayout.LayoutParams lpIvCraftSignal = (RelativeLayout.LayoutParams) ivCraftSignal.getLayoutParams();
        lpIvCraftSignal.width = (int) (wWidth * 0.0147);
        lpIvCraftSignal.height = (int) (wHeight * 0.0195);
        lpIvCraftSignal.leftMargin = lpCraftSignal.width / 2;
        ivCraftSignal.setLayoutParams(lpIvCraftSignal);

        RelativeLayout.LayoutParams lpIvControllerSignal = (RelativeLayout.LayoutParams) ivControllerSignal.getLayoutParams();
        lpIvControllerSignal.width = (int) (wWidth * 0.0147);
        lpIvControllerSignal.height = (int) (wHeight * 0.0195);
        lpIvControllerSignal.leftMargin = lpControllerSignal.width / 2;
        ivControllerSignal.setLayoutParams(lpIvCraftSignal);

        //left
        RelativeLayout.LayoutParams lpLeft = (RelativeLayout.LayoutParams) rlLeft.getLayoutParams();
        lpLeft.width = (int) (wWidth * 0.15);
        lpLeft.height = (int) (wHeight * 0.52);
        lpLeft.leftMargin = (int) (wWidth * 0.06);
        rlLeft.setLayoutParams(lpLeft);

        RelativeLayout.LayoutParams lpLeftBg = (RelativeLayout.LayoutParams) ivLeftBg.getLayoutParams();
        lpLeftBg.width = (int) (wWidth * 0.13);
        lpLeftBg.height = (int) (wHeight * 0.52);
        ivLeftBg.setLayoutParams(lpLeftBg);


        RelativeLayout.LayoutParams lpPreview = (RelativeLayout.LayoutParams) tvPreview.getLayoutParams();
        lpPreview.width = (int) (wWidth * 0.1);
        lpPreview.height = (int) (wHeight * 0.1);
//        lpPreview.leftMargin = (int)(wWidth * 0.039);
        lpPreview.topMargin = (int) (wHeight * 0.028);
        tvPreview.setLayoutParams(lpPreview);

        //bottom
        RelativeLayout.LayoutParams lpBottom = (RelativeLayout.LayoutParams) rlBottom.getLayoutParams();
        lpBottom.width = (int) (wWidth * 0.390);
        lpBottom.height = (int) (wHeight * 0.107);
        lpBottom.bottomMargin = (int) (wHeight * 0.045);
        rlBottom.setLayoutParams(lpBottom);

        RelativeLayout.LayoutParams lpHelmetEnergy = (RelativeLayout.LayoutParams) ivHelmetEnergy.getLayoutParams();
        lpHelmetEnergy.width = (int) (wWidth * 0.032);
        lpHelmetEnergy.height = (int) (wHeight * 0.048);
        lpHelmetEnergy.leftMargin = (int) (wWidth * 0.039);
        lpHelmetEnergy.topMargin = (int) (wHeight * 0.026);
        ivHelmetEnergy.setLayoutParams(lpHelmetEnergy);

        RelativeLayout.LayoutParams lpPhoneEnergy = (RelativeLayout.LayoutParams) ivPhoneEnergy.getLayoutParams();
        lpPhoneEnergy.width = (int) (wWidth * 0.03);
        lpPhoneEnergy.height = (int) (wHeight * 0.045);
        lpPhoneEnergy.leftMargin = (int) (wWidth * 0.0365);
        lpPhoneEnergy.topMargin = (int) (wHeight * 0.026);
        ivPhoneEnergy.setLayoutParams(lpPhoneEnergy);

        RelativeLayout.LayoutParams lpControllerEnergy = (RelativeLayout.LayoutParams) ivRCEnergy.getLayoutParams();
        lpControllerEnergy.width = (int) (wWidth * 0.032);
        lpControllerEnergy.height = (int) (wHeight * 0.048);
        lpControllerEnergy.rightMargin = (int) (wWidth * 0.036);
        lpControllerEnergy.topMargin = (int) (wHeight * 0.026);
        ivRCEnergy.setLayoutParams(lpControllerEnergy);

        RelativeLayout.LayoutParams lpCraftEnergy = (RelativeLayout.LayoutParams) ivCraftEnergy.getLayoutParams();
        lpCraftEnergy.width = (int) (wWidth * 0.032);
        lpCraftEnergy.height = (int) (wHeight * 0.048);
        lpCraftEnergy.rightMargin = (int) (wWidth * 0.037);
        lpCraftEnergy.topMargin = (int) (wHeight * 0.026);
        ivCraftEnergy.setLayoutParams(lpCraftEnergy);

        //right
        RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) rlRight.getLayoutParams();
        lpRight.width = (int) (wWidth * 0.15);
        lpRight.height = (int) (wHeight * 0.52);
        lpRight.rightMargin = (int) (wWidth * 0.06);
        rlRight.setLayoutParams(lpRight);

        RelativeLayout.LayoutParams lpFlightHeight = (RelativeLayout.LayoutParams) tvFlightHeight.getLayoutParams();
        lpFlightHeight.topMargin = (int) (wHeight * 0.03);
        lpFlightHeight.leftMargin = (int) (wWidth * 0.071);
        tvFlightHeight.setLayoutParams(lpFlightHeight);
        tvFlightHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (wHeight * 0.07));
        tvFlightHeight.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightHeight.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightDistance = (RelativeLayout.LayoutParams) tvFlightDistance.getLayoutParams();
        lpFlightDistance.topMargin = (int) (wHeight * 0.16);
        lpFlightDistance.leftMargin = (int) (wWidth * 0.093);
        tvFlightDistance.setLayoutParams(lpFlightDistance);
        tvFlightDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (wHeight * 0.07));
        tvFlightDistance.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightDistance.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightSpeed = (RelativeLayout.LayoutParams) tvFlightSpeed.getLayoutParams();
        lpFlightSpeed.topMargin = (int) (wHeight * 0.4);
        lpFlightSpeed.leftMargin = (int) (wWidth * 0.071);
        tvFlightSpeed.setLayoutParams(lpFlightSpeed);
        tvFlightSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (wHeight * 0.07));
        tvFlightSpeed.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightSpeed.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightVerticalSpeed = (RelativeLayout.LayoutParams) tvFlightVerticalSpeed.getLayoutParams();
        lpFlightVerticalSpeed.topMargin = (int) (wHeight * 0.475);
        lpFlightVerticalSpeed.leftMargin = (int) (wWidth * 0.12);
        tvFlightVerticalSpeed.setLayoutParams(lpFlightVerticalSpeed);
        tvFlightVerticalSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (wHeight * 0.035));
        tvFlightVerticalSpeed.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightVerticalSpeed.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpIvFlightVerticalSpeed = (RelativeLayout.LayoutParams) ivFlightVerticalSpeed.getLayoutParams();
        lpIvFlightVerticalSpeed.width = (int) (wWidth * 0.01);
        lpIvFlightVerticalSpeed.height = (int) (wHeight * 0.02);
        lpIvFlightVerticalSpeed.topMargin = (int) (wHeight * 0.494);
        lpIvFlightVerticalSpeed.leftMargin = (int) (wWidth * 0.11);
        ivFlightVerticalSpeed.setLayoutParams(lpIvFlightVerticalSpeed);

        tvFlightVerticalSpeed.addTextChangedListener(new VSpeedWatcher());

        return view;
    }

    public void onResume() {
        super.onResume();
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
    }

    public void onPause() {
        super.onPause();
        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            // Reset the callback
            camera.setDJICameraReceivedVideoDataCallback(null);
        }
        if (djiAircraft != null) {
            djiAircraft.getFlightController().setUpdateSystemStateCallback(null);
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
            float altitude;
            if (FCState.isUltrasonicBeingUsed()) {
                altitude = FCState.getUltrasonicHeight();
            } else {
                altitude = FCState.getAircraftLocation().getAltitude();
            }

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
            bundle.putFloat("altitude", altitude);
            bundle.putDouble("distance", dis);
            bundle.putInt("gpsSignalLevel", gpsSignalLevel);
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

            ivPhoneEnergy.setImageResource(ENERGY_ICON[index]);
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
}
