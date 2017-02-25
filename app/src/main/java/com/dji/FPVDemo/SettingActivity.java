package com.dji.FPVDemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dji.FPVDemo.fragment.FlightControllerSettingFragment;

public class SettingActivity extends FragmentActivity {

    private FlightControllerSettingFragment mFCSFragment;

    private Button btnFCS;
    private Button btnRCS;

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

            }
        });
    }


}
