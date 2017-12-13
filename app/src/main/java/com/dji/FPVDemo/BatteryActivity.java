package com.dji.FPVDemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

public class BatteryActivity extends FragmentActivity implements View.OnClickListener {

    private View btnBack;
    private View btnHistory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_battery);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);
        btnHistory = findViewById(R.id.ll_battery_history);
        btnHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_battery_history:
                Intent history = new Intent(this, BatteryHistoryActivity.class);
                startActivity(history);
                break;
        }
    }
}
