package com.synseaero.fpv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.synseaero.fpv.FPVDemoApplication;

import dji.common.gimbal.DJIGimbalAdvancedSettingsState;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;


public class GimbalSettingFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJIGimbal djiGimbal;

    private Switch wcGimbalPitchRange;

    private EditText etGimbalTune;
    private Button btnGimbalTune;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.synseaero.fpv.R.layout.fragment_gimbal_setting, container, false);
        wcGimbalPitchRange = (Switch) view.findViewById(com.synseaero.fpv.R.id.rc_gimbal_pitch_range);
        wcGimbalPitchRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                djiGimbal.setPitchRangeExtensionEnabled(checked, null);
            }
        });
        etGimbalTune = (EditText) view.findViewById(com.synseaero.fpv.R.id.et_gimbal_tune);
        btnGimbalTune = (Button) view.findViewById(com.synseaero.fpv.R.id.btn_gimbal_tune);
        btnGimbalTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDegree = etGimbalTune.getText().toString();
                if(!strDegree.isEmpty()) {
                    float degree = Float.parseFloat(strDegree);
                    djiGimbal.fineTuneGimbalRollInDegrees(degree, null);
                }
            }
        });
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiGimbal = djiAircraft.getGimbal();
        if(djiGimbal != null) {
//        djiGimbal.setGimbalWorkMode();工作模式
            djiGimbal.setGimbalAdvancedSettingsStateUpdateCallback(new djiGimbalAdvancedSettingsStateUpdateCallback());
        }
//        djiGimbal.setPitchRangeExtensionEnabled();
//        djiGimbal.setControllerSmoothingOnAxis();
//        djiAircraft.getFlightController().
    }

    private class djiGimbalAdvancedSettingsStateUpdateCallback implements DJIGimbal.GimbalAdvancedSettingsStateUpdateCallback {
        @Override
        public void onGimbalAdvancedSettingsStateUpdate(DJIGimbal djiGimbal, DJIGimbalAdvancedSettingsState djiGimbalAdvancedSettingsState) {
//            djiGimbalAdvancedSettingsState.getAdvancedSettingsProfile();
            //djiGimbalAdvancedSettingsState.getControllerSmoothingPitch();
            //djiGimbalAdvancedSettingsState.getSmoothTrackAccelerationPitch();
//            djiGimbalAdvancedSettingsState.
        }
    }

}
