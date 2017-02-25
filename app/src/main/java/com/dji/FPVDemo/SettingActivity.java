package com.dji.FPVDemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.dji.FPVDemo.fragment.FlightControllerSettingFragment;

public class SettingActivity extends FragmentActivity {

    private FlightControllerSettingFragment mFCSFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFCSFragment = new FlightControllerSettingFragment();
        transaction.replace(R.id.container, mFCSFragment);
        transaction.commit();
    }


}
