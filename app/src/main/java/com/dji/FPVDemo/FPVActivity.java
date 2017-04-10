package com.dji.FPVDemo;


import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.dji.FPVDemo.fragment.TPVFragment;

public class FPVActivity extends FragmentActivity {

    private TPVFragment mTPVFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_fpv);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mTPVFragment = new TPVFragment();
        transaction.replace(R.id.layout_fpv_root, mTPVFragment);
        transaction.commit();
    }
}
