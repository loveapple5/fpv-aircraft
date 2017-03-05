package com.dji.FPVDemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dji.FPVDemo.fragment.BatterySettingFragment;
import com.dji.FPVDemo.fragment.CameraPhotoFragment;
import com.dji.FPVDemo.fragment.CameraVideoFragment;
import com.dji.FPVDemo.fragment.FlightControllerSettingFragment;
import com.dji.FPVDemo.fragment.GimbalSettingFragment;
import com.dji.FPVDemo.fragment.RemoteControllerSettingFragment;
import com.dji.FPVDemo.fragment.WiFiLinkSettingFragment;

public class SettingActivity extends FragmentActivity {

    private FlightControllerSettingFragment mFCSFragment;
    private RemoteControllerSettingFragment mRCSFragment;
    private WiFiLinkSettingFragment mWLSFragment;
    private BatterySettingFragment mBSFragment;
    private GimbalSettingFragment gimbalFragment;
    private CameraPhotoFragment cameraPhotoFragment;
    private CameraVideoFragment cameraVideoFragment;


    private Button btnFCS;
    private Button btnRCS;
    private Button btnWLS;
    private Button btnBS;
    private Button btnGimbal;
    private Button btnPhoto;
    private Button btnVideo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_setting);
        initUI();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFCSFragment = new FlightControllerSettingFragment();
        transaction.replace(R.id.container, mFCSFragment);
        transaction.commit();
    }

    private void initUI() {
        btnFCS = (Button) findViewById(R.id.btn_fcs);
        btnFCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mFCSFragment = new FlightControllerSettingFragment();
                transaction.replace(R.id.container, mFCSFragment);
                transaction.commit();
            }
        });

        btnRCS = (Button) findViewById(R.id.btn_rcs);
        btnRCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mRCSFragment = new RemoteControllerSettingFragment();
                transaction.replace(R.id.container, mRCSFragment);
                transaction.commit();

            }
        });
        btnWLS = (Button) findViewById(R.id.btn_wls);
        btnWLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mWLSFragment = new WiFiLinkSettingFragment();
                transaction.replace(R.id.container, mWLSFragment);
                transaction.commit();

            }
        });
        btnBS = (Button) findViewById(R.id.btn_bs);
        btnBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                mBSFragment = new BatterySettingFragment();
                transaction.replace(R.id.container, mBSFragment);
                transaction.commit();

            }
        });

        btnGimbal = (Button) findViewById(R.id.btn_gimbal);
        btnGimbal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                gimbalFragment = new GimbalSettingFragment();
                transaction.replace(R.id.container, gimbalFragment);
                transaction.commit();

            }
        });

        btnPhoto = (Button) findViewById(R.id.btn_camera_photo);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                cameraPhotoFragment = new CameraPhotoFragment();
                transaction.replace(R.id.container, cameraPhotoFragment);
                transaction.commit();

            }
        });

        btnVideo = (Button) findViewById(R.id.btn_camera_video);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                cameraVideoFragment = new CameraVideoFragment();
                transaction.replace(R.id.container, cameraVideoFragment);
                transaction.commit();

            }
        });
    }


}
