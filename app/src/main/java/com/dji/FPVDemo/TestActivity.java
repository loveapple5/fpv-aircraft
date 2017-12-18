package com.dji.FPVDemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;


public class TestActivity extends FragmentActivity {

    private static final String TAG = TestActivity.class.getName();

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private LocationManager locationManager;
    private MyLocationListener locationListener;

    private MySensorEventListener sensorEventListener;

    private SensorManager mSensorManager;
    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        sensorEventListener = new MySensorEventListener();

        // 实例化传感器管理者
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(sensorEventListener, accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorEventListener, magnetic, Sensor.TYPE_MAGNETIC_FIELD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        // criteria.setAltitudeRequired(false);//不要求海拔信息
        if(locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

            Log.i(TAG, "ORIENTATION:" + ORIENTATION);
//            Log.i(TAG, "PITCH" + PITCH);
//            Log.i(TAG, "ROLL" + ROLL);


            if (ORIENTATION >= -5 && ORIENTATION < 5) {
                Log.i(TAG, "正北");
            } else if (ORIENTATION >= 5 && ORIENTATION < 85) {
                Log.i(TAG, "东北");
            } else if (ORIENTATION >= 85 && ORIENTATION <= 95) {
                Log.i(TAG, "正东");
            } else if (ORIENTATION >= 95 && ORIENTATION < 175) {
                 Log.i(TAG, "东南");
            } else if ((ORIENTATION >= 175 && ORIENTATION <= 180)
                    || (ORIENTATION) >= -180 && ORIENTATION < -175) {
                Log.i(TAG, "正南");
            } else if (ORIENTATION >= -175 && ORIENTATION < -95) {
                 Log.i(TAG, "西南");
            } else if (ORIENTATION >= -95 && ORIENTATION < -85) {
                 Log.i(TAG, "正西");
            } else if (ORIENTATION >= -85 && ORIENTATION < -5) {
                Log.i(TAG, "西北");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

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
}
