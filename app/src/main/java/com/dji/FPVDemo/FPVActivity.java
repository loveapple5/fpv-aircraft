package com.dji.FPVDemo;


import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.dji.FPVDemo.fragment.FPVFragment;
import com.dji.FPVDemo.fragment.TPVFragment;

public class FPVActivity extends FragmentActivity {

    private TPVFragment mTPVFragment;
    private FPVFragment mFPVFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_fpv);

        mFPVFragment = new FPVFragment();
        mTPVFragment = new TPVFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layout_fpv_root, mTPVFragment);
        transaction.commit();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fpv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_fpv:
                transaction.replace(R.id.layout_fpv_root, mFPVFragment);
                transaction.commit();
                break;
            case R.id.menu_tpv:
                transaction.replace(R.id.layout_fpv_root, mTPVFragment);
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
