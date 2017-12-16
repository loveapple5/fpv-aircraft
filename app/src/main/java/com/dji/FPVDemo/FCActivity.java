package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;


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

    private void fetchDJIValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.getGoHomeAltitude(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float aFloat) {
                    String curValue = String.valueOf(aFloat.intValue());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
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
