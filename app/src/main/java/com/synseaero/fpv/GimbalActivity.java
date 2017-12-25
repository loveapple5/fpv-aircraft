package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.SwitchButton;

public class GimbalActivity extends DJIActivity implements View.OnClickListener {

    private View btnBack;
    private SwitchButton sbPitchRange;
    private SeekBar sbPitchSmooth;
    private TextView tvPitchSmooth;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    boolean pitchExtensionEnable = bundle.getBoolean("pitchExtensionEnable");
                    sbPitchRange.setCheckedNoEvent(pitchExtensionEnable);
                } else {
                    Toast.makeText(GimbalActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    boolean pitchExtensionEnable = bundle.getBoolean("pitchExtensionEnable");
                    sbPitchRange.setCheckedNoEvent(pitchExtensionEnable);
                } else {
                    Toast.makeText(GimbalActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_GET_SMOOTHING_ON_AXIS_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    int smoothing = bundle.getInt("smoothing");
                    sbPitchSmooth.setProgress(smoothing);
                    tvPitchSmooth.setText(String.valueOf(smoothing));
                } else {
                    Toast.makeText(GimbalActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_SET_SMOOTHING_ON_AXIS_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    int smoothing = bundle.getInt("smoothing");
                    sbPitchSmooth.setProgress(smoothing);
                    tvPitchSmooth.setText(String.valueOf(smoothing));
                } else {
                    Toast.makeText(GimbalActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gimbal);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        sbPitchRange = (SwitchButton) findViewById(R.id.sb_gimbal_pitch_range);
        sbPitchSmooth = (SeekBar) findViewById(R.id.sb_gimbal_pitch_smooth);
        tvPitchSmooth = (TextView) findViewById(R.id.tv_gimbal_pitch_smooth);

        sbPitchRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION;
                Bundle data = new Bundle();
                data.putBoolean("pitchExtensionEnable", isChecked);
                message.setData(data);
                sendDJIMessage(message);
            }
        });
        sbPitchSmooth.setOnSeekBarChangeListener(new PitchSmoothListener());


        registerDJIMessenger(MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_SMOOTHING_ON_AXIS_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_SMOOTHING_ON_AXIS_RESPONSE, messenger);

        Message message = Message.obtain();
        message.what = MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION;
        sendDJIMessage(message);

        Message message2 = Message.obtain();
        message2.what = MessageType.MSG_GET_SMOOTHING_ON_AXIS;
        sendDJIMessage(message2);
    }

    class PitchSmoothListener implements SeekBar.OnSeekBarChangeListener {

        private boolean fromUser = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.fromUser = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(this.fromUser) {
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_SMOOTHING_ON_AXIS;
                Bundle data = new Bundle();
                data.putInt("smoothing", seekBar.getProgress());
                message.setData(data);
                sendDJIMessage(message);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_SMOOTHING_ON_AXIS_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_SMOOTHING_ON_AXIS_RESPONSE, messenger);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
