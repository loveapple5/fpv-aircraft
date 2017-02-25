package com.dji.FPVDemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dji.FPVDemo.opengl.MyGLSurfaceView;


import java.util.ArrayList;

import dji.common.airlink.DJISignalInformation;
import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.remotecontroller.DJIRCBatteryInfo;
import dji.common.remotecontroller.DJIRCHardwareState;
import dji.common.util.DJICommonCallbacks;
import dji.common.util.DJIParamCapability;
import dji.common.util.DJIParamMinMaxCapability;
import dji.midware.data.model.P3.DataFlycUploadWayPointMissionMsg;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIAuxLink;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.camera.DJICamera.CameraReceivedVideoDataCallback;
import dji.sdk.camera.DJIMediaManager;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.missionmanager.DJIMissionManager;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;



public class MainActivity extends Activity implements SurfaceTextureListener,SensorEventListener{
    private static final String TAG = MainActivity.class.getName();
    public boolean viewTPV = true;
    public TextView spd, vspd,alt,dis;
    public Button btnStatusList;


//    public float currentDegree = 0.0f;
//    public float currentAzimuth = 0f;
//    public float azimuthIni = 0f;
//    public float currentRoll = 0f;
//    public float currentPitch = 0f;
//    public float azimuth;
    public boolean firstCall;

    // Camera and textureview-display
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBackmReceivedVideoDataCallBack = null;
    protected TextureView mVideoSurface = null;
    // Codec for video live view
    protected DJICodecManager mCodecManager = null;
    // Airlink

//    protected DJISignalInformation djiSignalInformation;

    // Flight controller

    protected DJIFlightController flightController = null;
    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE = 1;
    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR = 2;
    final public int MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION = 3;


//    private RemoteControl myRemoteControl = new RemoteControl();
    protected DJIRemoteController remoteController = null;
    final public int MSG_REMOTE_CONTROLLER_CURRENT_STATE =1;
    final public int MSG_REMOTE_CONTROLLER_ERROR =3;
    public static Boolean switchView = false;

    SendTone mytone = new SendTone();

    // Gimbal controller
    public SensorManager sm;
    private GimbalControl myGimbalControl = new GimbalControl();


    // OpenGL componets
    private MyGLSurfaceView mGLView;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        setContentView(R.layout.tpv_view);
        initUI();
        initCodec();
//        initAirLink();
        initFlightController();
        initRemoteController();
        myGimbalControl.iniGimbal();

        // init phone IMU Sensor for Gimbal
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);

        mGLView = new MyGLSurfaceView(this);
        mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLView.setZOrderOnTop(true);

    }



    public void initUI() {
        // init mVideoSurface
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);
        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
            }

        spd =(TextView)findViewById(R.id.textView7);
        vspd = (TextView)findViewById(R.id.textView6);
        alt = (TextView) findViewById(R.id.textView4);
        dis = (TextView) findViewById(R.id.textView5);
        btnStatusList = (Button) this.findViewById(R.id.btn_status_list);
        btnStatusList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatusListActivity.class);
                startActivity(intent);
            }
        });
    }

//    private void initAirLink(){
//        try{
//            final DJIAircraft djiAircraft = (DJIAircraft)FPVDemoApplication.getProductInstance();
//            if(djiAircraft!=null) {
//                final DJIAirLink airLink = djiAircraft.getAirLink();
//                // remoteControl signal quallity
//                airLink.getAuxLink().setAuxLinkUpdatedRemoteControllerSignalInformationCallback(new DJIAuxLink.DJIAuxLinkUpdatedRemoteControllerSignalInformationCallback() {
//                    @Override
//                    public void onResult(ArrayList<DJISignalInformation> arrayList) {
//// TODO  error to be fixed here
////                        Integer RCSignal=djiSignalInformation.getPercent();
//                        Integer RCSignal= arrayList.get(1).getPercent();
////                        arrayList.indexOf(DJISignalInformation);
//
//                        Log.d("RCSignal =",String.valueOf(RCSignal)+"%");
//
//                        Message msgRCS = new Message();
//                        Bundle bundleRCS = new Bundle();
//                        bundleRCS.putInt ("RCSignal", RCSignal);
//                        msgRCS.what = MSG_REMOTE_CONTROLLER_CURRENT_STATE;
//                        msgRCS.setData(bundleRCS);
//                        mHandlerAirLink.sendMessage(msgRCS);
//                    }
//                });
//
//            }
//
//        } catch (Exception e){
//            Log.e("InitAirLink failed","e");
//
//        }
//
//    }
//
//    private Handler mHandlerAirLink = new Handler(new Handler.Callback(){
//
//        @Override
//        public boolean handleMessage(Message msgRCS) {
//            Bundle bundleRCS=msgRCS.getData();
//            Integer RCSignal = bundleRCS.getInt("RCSignal");
//            Log.d("RCSignal=",String.valueOf(RCSignal)+"%");
//
//            return false;
//        }
//
//    });


    private void initRemoteController(){
        try {
            final DJIAircraft djiAircraft = (DJIAircraft)FPVDemoApplication.getProductInstance();
            if(djiAircraft!=null){
                remoteController = djiAircraft.getRemoteController();


//                dji.sdk.RemoteController.DJIRemoteController.DJIRCGimbalControlSpeed;


// TODO gimbal speed needs to be checked
                Short s =100;   // s stands for the Gimbal speed
                remoteController.setRCWheelControlGimbalSpeed(s, new DJICommonCallbacks.DJICompletionCallback(){
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Log.v("Gimbal speed", "Gimbal speed set 100 successfully");
                        }else{
                            Log.v("Gimbal speed", "Gimbal speed set failed");
                        }
                    }
                });
//


                remoteController.setHardwareStateUpdateCallback(
                        new DJIRemoteController.RCHardwareStateUpdateCallback() {
                            @Override
                            public void onHardwareStateUpdate(DJIRemoteController djiRemoteController, DJIRCHardwareState djircHardwareState) {
                                Boolean CusButton1 = djircHardwareState.customButton1.buttonDown;
                                Boolean CusButton2 = djircHardwareState.customButton2.buttonDown;

//                                djircHardwareState.transformSwitch;

                                Message msg0 = new Message();
                                Bundle bundle0 = new Bundle();
                                bundle0.putBoolean ("CusButton1", CusButton1);
                                bundle0.putBoolean ("CusButton2", CusButton2);
                                msg0.what = MSG_REMOTE_CONTROLLER_CURRENT_STATE;
                                msg0.setData(bundle0);
                                remoteControllHandler.sendMessage(msg0);
                            }

                        }
                );




//                remoteController.setBatteryStateUpdateCallback(new DJIRemoteController.RCBatteryStateUpdateCallback(){
//
//                    @Override
//                    public void onBatteryStateUpdate(DJIRemoteController djiRemoteController, DJIRCBatteryInfo djircBatteryInfo) {
//                        Integer RCBattery = djircBatteryInfo.remainingEnergyInPercent;
//
//                        Log.d("RC battery==",String.valueOf(RCBattery)+"%");
//
//                        Message msg2= new Message();
//                        Bundle bundle2 = new Bundle();
//                        bundle2.putInt("RCBattery", RCBattery);
//                        msg2.what = MSG_REMOTE_CONTROLLER_CURRENT_STATE;
//                        msg2.setData(bundle2);
//                        RCBatteryHandler.sendMessage(msg2);
//                    }
//                });

            }

        }catch (Exception e){
            Message msg0 = new Message();
            msg0.what = MSG_REMOTE_CONTROLLER_ERROR;
            remoteControllHandler.sendMessage(msg0);

//            Message msg2= new Message();
//            msg2.what = MSG_REMOTE_CONTROLLER_ERROR;
        }


    }

    private void initFlightController() {
        try {
            DJIAircraft djiAircraft = (DJIAircraft)FPVDemoApplication.getProductInstance();

            if (djiAircraft != null) {
                flightController = djiAircraft.getFlightController();

//                flightController.;

                flightController.setUpdateSystemStateCallback(
                        new DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback() {
                            @Override
                            public void onResult(DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
                                /////////////// aircraft status  ////////////////////
                                Double Speed = Math.sqrt(Math.pow(djiFlightControllerCurrentState.getVelocityX(), 2) + Math.pow(djiFlightControllerCurrentState.getVelocityY(), 2));
                                Float Vspeed = -1*djiFlightControllerCurrentState.getVelocityZ();
                                // get aircraft altitude
                                Float  Altitude;
                                if (djiFlightControllerCurrentState.isUltrasonicBeingUsed()) {
                                    Altitude = djiFlightControllerCurrentState.getUltrasonicHeight();
                                } else {
                                    Altitude =djiFlightControllerCurrentState.getAircraftLocation().getAltitude();
                                }
                                /// distance between the aircraft and home-point  ///
                                Double radLatA = Math.toRadians(djiFlightControllerCurrentState.getAircraftLocation().getCoordinate2D().getLatitude());
                                Double radLatH = Math.toRadians(djiFlightControllerCurrentState.getHomeLocation().getLatitude());
                                Double a=radLatA-radLatH;
                                Double b = Math.toRadians(djiFlightControllerCurrentState.getAircraftLocation().getCoordinate2D().getLongitude())
                                        - Math.toRadians(djiFlightControllerCurrentState.getHomeLocation().getLongitude());
                                Double dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                                        Math.cos(radLatA)*Math.cos(radLatH)*Math.pow(Math.sin(b/2),2)));
                                dis = dis * 6378.137; //  the earth radius= 6378.137km
                                //  distance into meter
                                dis = Math.round(dis * 10000.0) / 10000.0 *1000.0;
                                // aircraft attitude
                                Double AircraftPitch=djiFlightControllerCurrentState.getAttitude().pitch;
                                Double AircraftRoll=djiFlightControllerCurrentState.getAttitude().roll;
                                Double AircraftYaw=djiFlightControllerCurrentState.getAttitude().yaw;

                                ///////////// Compass //////////////
                                Double heading = flightController.getCompass().getHeading();
                                Boolean CompassError = flightController.getCompass().hasError();
                                ///////////////  GPS   ///////////////
                                Double GPSCount = djiFlightControllerCurrentState.getSatelliteCount();
                                Integer GPSLevel = djiFlightControllerCurrentState.getGpsSignalStatus().value();

                                Integer RemainingBattery = djiFlightControllerCurrentState.getRemainingBattery().value();


                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putDouble ("Speed", Speed);
                                bundle.putFloat("Vspeed", Vspeed);
                                bundle.putFloat("Altitude", Altitude);
                                bundle.putDouble("Distance",dis);
                                bundle.putDouble("AircraftPitch",AircraftPitch);
                                bundle.putDouble("AircraftRoll",AircraftRoll);
                                bundle.putDouble("AircraftYaw",AircraftYaw);
                                bundle.putDouble("Head",heading);
                                bundle.putBoolean("CompassError",CompassError);
                                bundle.putDouble("GPSCount",GPSCount);
                                bundle.putInt("GPSLevel",GPSLevel);
//                                bundle.putInt("RemainingBattery",RemainingBattery);

                                msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        });

            } else
            {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("FlightControllerCurrentStateNoConnection", "Flight Controller Current State - no connection available: ");
                msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION;
                mHandler.sendMessage(msg);
            }

        } catch (Exception e) {

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("FlightControllerCurrentState", "Flight Controller Current State Error: " + e.getMessage());
            msg.what = MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR;
            mHandler.sendMessage(msg);
        }


    }



    protected Handler remoteControllHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg0) {
            Bundle bundle0=msg0.getData();
            Boolean CustButton1=false;
            Boolean CustButton2=false;

            switch (msg0.what){
                case MSG_REMOTE_CONTROLLER_CURRENT_STATE:
                    CustButton1 = bundle0.getBoolean("CusButton1");
                    CustButton2 = bundle0.getBoolean("CusButton2");
                    break;
                case MSG_REMOTE_CONTROLLER_ERROR:
                    showToast(getString(R.string.remotecontrolerror));
                    break;
            }

            switchView = CustButton2;
            changeView();

//        generate tone for LCD valve

            if (switchView ==true && viewTPV!=true) {
                mytone.genTone2();
                mytone.sendSig();
            } else if (switchView ==true && viewTPV==true){
                mytone.genTone1();
                mytone.sendSig();
            }
            else{
                mytone.stopSig();
            }

            return false;
        }

        private void changeView() {
            if (switchView==true) {

                if (viewTPV == true) {
                    setContentView(R.layout.fpv_view);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    addContentView(mGLView,params);

                    viewTPV = false;

                    firstCall=true;

                } else {

                    ((ViewGroup)mGLView.getParent()).removeView(mGLView);
                    setContentView(R.layout.tpv_view);
                    initPreviewer();

                    viewTPV = true;
                }
                initUI();
                initCodec();
            }
        }
    });



//    private Handler RCBatteryHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg2) {
//
//            Bundle bundle2 = msg2.getData();
//
//            switch (msg2.what) {
//                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:
//                    Integer RCBattery= bundle2.getInt("RCBattery");
//
//                    Log.d ( "RC Battery==",String.valueOf(RCBattery)+"%");
//                    break;
//                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR:
//
//                    break;
//
//            }
//
//
//            return false;
//        }
//    });


    private Handler mHandler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg){
            Bundle bundle =msg.getData();
            String Sspd ="";
            String Svspd="";
            String Salt ="";
            String Sdis ="";
            String Fheading ="";
            String Sptich ="";
            String Sroll ="";

            String SGPSLevel="";



            switch (msg.what){
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE:
                    Double Speed = bundle.getDouble("Speed");
                    Float Vspeed = bundle.getFloat("Vspeed");
                    Float Altitude = bundle.getFloat("Altitude");
                    Double Distance = bundle.getDouble("Distance");
                    Double  AircraftPitch = bundle.getDouble("AircraftPitch");
                    Double  AircraftRoll = bundle.getDouble("AircraftRoll");
                    Double  AircraftYaw = bundle.getDouble("AircraftYaw");
                    Double Heading = bundle.getDouble("Head"); // aircraft compass heading
                    Boolean CompassError = bundle.getBoolean("CompassError");
                    Double GPSCount = bundle.getDouble("GPSCount");
                    Integer GPSLevel = bundle.getInt("GPSLevel");
//                    Integer RemainingBattery = bundle.getInt("RemainingBattery");

                    Sspd = String.format("%.1f",Speed);
                    Svspd =String.format("%.1f",Vspeed);
                    Salt = String.format("%.1f",Altitude);
                    Sdis = String.format("%.1f",Distance);
                    Fheading= String.format("%.0f",Heading);
                    Sptich = String.format("%.0f",AircraftPitch);
                    Sroll =String.format("%.0f",AircraftRoll);

                    SGPSLevel= String.format("%d",GPSLevel);


                    Log.d ( "GPS Level==",String.valueOf(GPSLevel));
//                    Log.d ( "RemainingBattery==",String.valueOf(RemainingBattery)+"%");

                    break;
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE_NO_CONNECTION:
                    Sspd = "null";
                    Svspd = "null";
                    Salt = "null";
                    Sdis = "null";
                    break;
                case MSG_FLIGHT_CONTROLLER_CURRENT_STATE_ERROR:
                    Sspd = "error";
                    Svspd = "error";
                    Salt = "error";
                    Sdis = "error";
                    break;
            }

//            spd.setText(SGPSLevel);
            spd.setText(Sspd);
            vspd.setText(Svspd);
            alt.setText(Salt);
            dis.setText(Sdis);
            mGLView.setHeadingAngle(Float.parseFloat(Fheading));
            mGLView.setAttitude(Float.parseFloat(Sptich),Float.parseFloat(Sroll));

            return false;

        }



    });


    private void initCodec() {
        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new CameraReceivedVideoDataCallback() {

            @Override
            public void onResult(byte[] videoBuffer, int size) {
                if(mCodecManager != null){
                    // Send the raw H264 video data to codec manager for decoding
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }else {
                    Log.e(TAG, "mCodecManager is null");
                }
            }
        };

//        CAMERA_SERVICE

    }


//        public boolean onTouchEvent (MotionEvent e)  {
//
//        if (e.getAction() == MotionEvent.ACTION_DOWN) {
//            if(viewTPV==true) {
//                setContentView(R.layout.fpv_view);
//                viewTPV=false;
//            }
//            else {
//                setContentView(R.layout.tpv_view);
//                viewTPV=true;
//            }
//            initUI();
//            initCodec();
//            }
//        return true;
//    }


    private void initPreviewer() {

        DJIBaseProduct product = FPVDemoApplication.getProductInstance();

//        DJIAirLink thelink=product.;

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }

            if (!product.getModel().equals(Model.UnknownAircraft)) {
                DJICamera camera = product.getCamera();
                if (camera != null){
                    // Set the callback
                    camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);

                }
            }

        }
    }

    private void uninitPreviewer() {
        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null){
            // Reset the callback
            FPVDemoApplication.getCameraInstance().setDJICameraReceivedVideoDataCallback(null);
            FPVDemoApplication.getAircraftInstance().getFlightController().setUpdateSystemStateCallback(null);
        }
    }




    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();

        mGLView.onResume();
        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }


    }


    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        mytone.stopSig();
        sm.unregisterListener(this);

        mGLView.onPause();
        super.onPause();

    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();
        mytone.stopSig();
        super.onDestroy();
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG,"onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

//////////////// sensor Event  /////////////////////////////////
float previousroll;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // only work in FPV mode
        if  (viewTPV!= true) {
            float roll = Math.round(event.values[2]);
//        pitch.setText(String.valueOf(roll));
            if (roll != previousroll) {
                Log.d("Sensor changed value:", String.valueOf(roll));
                previousroll = roll;
                myGimbalControl.rotateGimbalByAngle(roll);
                // toggle the firstCall flag
                firstCall = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
/////////////////////////////////////////////////////////////

}
