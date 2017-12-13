package com.dji.FPVDemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


public class FCActivity extends FragmentActivity implements View.OnClickListener {

    private View btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fc);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
