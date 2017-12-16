package com.dji.FPVDemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dji.common.handheld.DJIHandheldButtonStatus;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.products.DJIHandHeld;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseComponent.DJIComponentListener;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIBaseProduct.DJIBaseProductListener;
import dji.sdk.base.DJIBaseProduct.DJIComponentKey;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;

public class FPVDemoApplication extends Application {

    public static final String FLAG_CONNECTION_CHANGE = "fpv_tutorial_connection_change";


    private static DJIBaseProduct mProduct;

    private Handler mHandler;

    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized DJIBaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getDJIProduct();
//           mProduct.getMissionManager().
        }
        return mProduct;
    }

    public static synchronized DJIAircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (DJIAircraft) getProductInstance();


    }


    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof DJIAircraft;
    }

    public static boolean isHandHeldConnected() {
        return getProductInstance() != null && getProductInstance() instanceof DJIHandHeld;
    }

    public static synchronized DJICamera getCameraInstance() {

        if (getProductInstance() == null) return null;

        DJICamera camera = getProductInstance().getCamera();

//        if (getProductInstance() instanceof DJIAircraft){
//            camera = ((DJIAircraft) getProductInstance()).getCamera();
//
//        } else if (getProductInstance() instanceof DJIHandHeld) {
//            camera = ((DJIHandHeld) getProductInstance()).getCamera();
//        }

        return camera;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        //This is used to start SDK services and initiate SDK.
        DJISDKManager.getInstance().initSDKManager(this, mDJISDKManagerCallback);
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                StringBuilder sb = new StringBuilder();
//                try {
//                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String date = sDateFormat.format(new java.util.Date());
//                    sb.append("\r\n" + date + "\n");
//
//                    Writer writer = new StringWriter();
//                    PrintWriter printWriter = new PrintWriter(writer);
//                    ex.printStackTrace(printWriter);
//                    Throwable cause = ex.getCause();
//                    while (cause != null) {
//                        cause.printStackTrace(printWriter);
//                        cause = cause.getCause();
//                    }
//                    printWriter.flush();
//                    printWriter.close();
//                    String result = writer.toString();
//                    sb.append(result);
//
//
//                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                    String time = formatter.format(new Date());
//                    String fileName = "crash-" + time + ".log";
//                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash" + File.separator;
//                    File dir = new File(path);
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//                    FileOutputStream fos = new FileOutputStream(path + fileName, true);
//                    fos.write(sb.toString().getBytes());
//                    fos.flush();
//                    fos.close();
//
//                } catch (Exception e) {
//
//                }
//            }
//        });
    }

    /**
     * When starting SDK services, an instance of interface DJISDKManager.DJISDKManagerCallback will be used to listen to
     * the SDK Registration result and the product changing.
     */
    private DJISDKManager.DJISDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.DJISDKManagerCallback() {

        //Listens to the SDK registration result
        @Override
        public void onGetRegisteredResult(DJIError error) {

            if (error == DJISDKError.REGISTRATION_SUCCESS) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();

                    }
                });


                DJISDKManager.getInstance().startConnectionToProduct();

            } else {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Register sdk fails, check network is available", Toast.LENGTH_LONG).show();
                    }
                });


            }
            Log.e("TAG", error.toString());
        }

        //Listens to the connected product changing, including two parts, component changing or product connection changing.
        @Override
        public void onProductChanged(DJIBaseProduct oldProduct, DJIBaseProduct newProduct) {

            mProduct = newProduct;
            if (mProduct != null) {
                mProduct.setDJIBaseProductListener(mDJIBaseProductListener);
            }

            notifyStatusChange();
        }
    };

    private DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProductListener() {

        @Override
        public void onComponentChange(DJIComponentKey key, DJIBaseComponent oldComponent, DJIBaseComponent newComponent) {

            if (newComponent != null) {
                newComponent.setDJIComponentListener(mDJIComponentListener);
            }
            notifyStatusChange();
        }

        @Override
        public void onProductConnectivityChanged(boolean isConnected) {

            notifyStatusChange();
        }

    };

    private DJIComponentListener mDJIComponentListener = new DJIComponentListener() {

        @Override
        public void onComponentConnectivityChanged(boolean isConnected) {
            notifyStatusChange();
        }

    };

    public void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
