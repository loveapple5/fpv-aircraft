package com.dji.FPVDemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class SettingActivity extends FragmentActivity implements View.OnClickListener {

    private View btnBack;
    private View btnFc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);
        btnFc = findViewById(R.id.ll_fc);
        btnFc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_fc:
                Intent fcIntent = new Intent(this, FCActivity.class);
                startActivity(fcIntent);
                break;
        }
    }
}
