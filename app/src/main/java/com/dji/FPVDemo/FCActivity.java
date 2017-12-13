package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


public class FCActivity extends FragmentActivity implements View.OnClickListener {

    private View btnBack;
    private View btnFlightLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fc);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        btnFlightLimit = findViewById(R.id.ll_flight_limit);
        btnFlightLimit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_flight_limit:
                Intent flIntent = new Intent(this, FLActivity.class);
                startActivity(flIntent);
                break;
        }
    }
}