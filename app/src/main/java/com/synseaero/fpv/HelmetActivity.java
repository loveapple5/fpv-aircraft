package com.synseaero.fpv;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.synseaero.view.ActionSheetDialog;
import com.synseaero.view.SwitchButton;


public class HelmetActivity extends DJIActivity implements View.OnClickListener {

    final int[] strStyleIds = new int[]{R.string.style_1, R.string.style_2, R.string.style_3};

    private TextView tvStyle;

    protected BroadcastReceiver mSkinReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int style = intent.getIntExtra("style", 1);
            tvStyle.setText(strStyleIds[style - 1]);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_helmet);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_style).setOnClickListener(this);

        tvStyle = (TextView) findViewById(R.id.tv_style);

        SwitchButton sbFindBack = (SwitchButton) findViewById(R.id.sb_find_back);
        //sbFindBack.setOnCheckedChangeListener();
        SwitchButton sbFan = (SwitchButton) findViewById(R.id.sb_helmet_fan);
        //sbFan.setOnCheckedChangeListener();

        int style = ((FPVApplication)getApplication()).getSkinStyle();
        tvStyle.setText(strStyleIds[style - 1]);

        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVApplication.ACTION_APP_SKIN_CHANGED);
        registerReceiver(mSkinReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.ll_style: {
                showStyleMenu();
                break;
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSkinReceiver);
    }

    private void showStyleMenu() {

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                ((FPVApplication) getApplication()).changeSkin(which);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strStyleIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strStyleIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strStyleIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }
}