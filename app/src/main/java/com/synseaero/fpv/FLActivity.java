package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.synseaero.fpv.view.SwitchButton;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightLimitation;
import dji.sdk.products.DJIAircraft;

public class FLActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = FLActivity.class.getName();

    protected static final int MSG_GET_FLIGHT_HEIGHT = 1;
    protected static final int MSG_GET_FLIGHT_RADIUS = 2;

    private View btnBack;

    private SwitchButton sbNovice;
    private EditText etFlightHeight;
    private EditText etFlightRadius;

    private DJIFlightLimitation djiFlightLimitation;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_FLIGHT_HEIGHT:
                    String flightHeight = msg.getData().getString("flightHeight");
                    etFlightHeight.setText(flightHeight);
                    Log.d(TAG, "flightHeight:" + flightHeight);
                    break;
                case MSG_GET_FLIGHT_RADIUS:
                    String flightRadius = msg.getData().getString("flightRadius");
                    etFlightRadius.setText(flightRadius);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fl);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        etFlightHeight = (EditText) findViewById(R.id.et_max_flight_height);
        etFlightHeight.setOnEditorActionListener(etListener);
        etFlightRadius = (EditText) findViewById(R.id.et_max_flight_radius);
        etFlightRadius.setOnEditorActionListener(etListener);
        sbNovice = (SwitchButton) findViewById(R.id.sb_novice);
        sbNovice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    djiFlightLimitation.setMaxFlightHeight(50, null);
                    djiFlightLimitation.setMaxFlightRadius(50, null);
                    etFlightHeight.setText("50");
                    etFlightRadius.setText("50");
                    etFlightHeight.setEnabled(false);
                    etFlightRadius.setEnabled(false);
                } else {
                    etFlightHeight.setEnabled(true);
                    etFlightRadius.setEnabled(true);
                }
            }
        });

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            djiFlightLimitation = flightController.getFlightLimitation();
            djiFlightLimitation.getMaxFlightHeight(heightCallback);
            djiFlightLimitation.getMaxFlightRadius(radiusCallback);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private TextView.OnEditorActionListener etListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(TAG, "actionId:" + actionId);
            Log.d(TAG, "v.getText():" + v.getText());
            //按下确认的时候进行处理
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                int id = v.getId();
                if (id == R.id.et_max_flight_height) {
                    String strHeight = etFlightHeight.getText().toString();
                    djiFlightLimitation.setMaxFlightHeight(Float.valueOf(strHeight), null);

                } else if (id == R.id.et_max_flight_radius) {
                    String strRadius = etFlightRadius.getText().toString();
                    djiFlightLimitation.setMaxFlightRadius(Float.valueOf(strRadius), null);
                }
            }
            return false;
        }
    };

    private DJICommonCallbacks.DJICompletionCallbackWith<Float> heightCallback = new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

        @Override
        public void onSuccess(Float aFloat) {
            String flightHeight = String.valueOf(aFloat.intValue());
            StringBuilder sb = new StringBuilder();
            sb.append(flightHeight);
            Bundle bundle = new Bundle();
            bundle.putString("flightHeight", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_GET_FLIGHT_HEIGHT;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };

    private DJICommonCallbacks.DJICompletionCallbackWith<Float> radiusCallback = new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

        @Override
        public void onSuccess(Float aFloat) {
            String flightRadius = String.valueOf(aFloat.intValue());
            StringBuilder sb = new StringBuilder();
            sb.append(flightRadius);
            Bundle bundle = new Bundle();
            bundle.putString("flightRadius", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_GET_FLIGHT_RADIUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };

}
