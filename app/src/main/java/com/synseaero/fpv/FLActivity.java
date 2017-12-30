package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.SwitchButton;

public class FLActivity extends DJIActivity implements View.OnClickListener {

    private static final String TAG = FLActivity.class.getName();

//    protected static final int MSG_GET_FLIGHT_HEIGHT = 1;
//    protected static final int MSG_GET_FLIGHT_RADIUS = 2;

    private SwitchButton sbNovice;
    private EditText etFlightHeight;
    private EditText etFlightRadius;

//    private DJIFlightLimitation djiFlightLimitation;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String errDesc = bundle.getString("DJI_DESC", "");
            switch (msg.what) {
                case MessageType.MSG_GET_MAX_FLIGHT_HEIGHT_RESPONSE:
                case MessageType.MSG_SET_MAX_FLIGHT_HEIGHT_RESPONSE:
                    if(errDesc.isEmpty()) {
                        Float flightHeight = bundle.getFloat("height", 0);
                        String strHeight = String.valueOf(flightHeight.intValue());
                        etFlightHeight.setText(strHeight);
                    } else {
                        Toast.makeText(FLActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }

                    //Log.d(TAG, "flightHeight:" + flightHeight);
                    break;
                case MessageType.MSG_GET_MAX_FLIGHT_RADIUS_RESPONSE:
                case MessageType.MSG_SET_MAX_FLIGHT_RADIUS_RESPONSE:
                    if(errDesc.isEmpty()) {
                        Float flightRadius = bundle.getFloat("radius");
                        String strRadius = String.valueOf(flightRadius.intValue());
                        etFlightRadius.setText(strRadius);
                    } else {
                        Toast.makeText(FLActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fl);

        findViewById(R.id.iv_back).setOnClickListener(this);

        etFlightHeight = (EditText) findViewById(R.id.et_max_flight_height);
        etFlightHeight.setOnEditorActionListener(etListener);
        etFlightRadius = (EditText) findViewById(R.id.et_max_flight_radius);
        etFlightRadius.setOnEditorActionListener(etListener);
        sbNovice = (SwitchButton) findViewById(R.id.sb_novice);
        sbNovice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Bundle bundle = new Bundle();
                    bundle.putFloat("height", 50);

                    Message setHeightMsg = Message.obtain();
                    setHeightMsg.what = MessageType.MSG_SET_MAX_FLIGHT_HEIGHT;
                    setHeightMsg.setData(bundle);
                    sendDJIMessage(setHeightMsg);


                    Bundle bundle2 = new Bundle();
                    bundle2.putFloat("radius", 50);

                    Message setRadiusMsg = Message.obtain();
                    setRadiusMsg.what = MessageType.MSG_SET_MAX_FLIGHT_RADIUS;
                    setRadiusMsg.setData(bundle2);
                    sendDJIMessage(setRadiusMsg);

                } else {

                }
            }
        });

        registerDJIMessenger(MessageType.MSG_GET_MAX_FLIGHT_HEIGHT_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_MAX_FLIGHT_HEIGHT_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_MAX_FLIGHT_RADIUS_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_MAX_FLIGHT_RADIUS_RESPONSE, messenger);

        Message getHeightMsg = Message.obtain();
        getHeightMsg.what = MessageType.MSG_GET_MAX_FLIGHT_HEIGHT;
        sendDJIMessage(getHeightMsg);

        Message getRadiusMsg = Message.obtain();
        getRadiusMsg.what = MessageType.MSG_GET_MAX_FLIGHT_RADIUS;
        sendDJIMessage(getRadiusMsg);

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
                    float height = Float.valueOf(strHeight);

                    Bundle bundle = new Bundle();
                    bundle.putFloat("height", height);

                    Message setHeightMsg = Message.obtain();
                    setHeightMsg.what = MessageType.MSG_SET_MAX_FLIGHT_HEIGHT;
                    setHeightMsg.setData(bundle);
                    sendDJIMessage(setHeightMsg);

                } else if (id == R.id.et_max_flight_radius) {
                    String strRadius = etFlightRadius.getText().toString();
                    float radius = Float.valueOf(strRadius);

                    Bundle bundle = new Bundle();
                    bundle.putFloat("radius", radius);

                    Message setRadiusMsg = Message.obtain();
                    setRadiusMsg.what = MessageType.MSG_SET_MAX_FLIGHT_RADIUS;
                    setRadiusMsg.setData(bundle);
                    sendDJIMessage(setRadiusMsg);

                }
            }
            return false;
        }
    };

}
