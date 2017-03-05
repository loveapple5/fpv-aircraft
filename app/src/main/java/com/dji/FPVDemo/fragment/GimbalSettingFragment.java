package com.dji.FPVDemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import java.util.ArrayList;

import dji.common.gimbal.DJIGimbalAdvancedSettingsState;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;


public class GimbalSettingFragment extends Fragment {


    private DJIAircraft djiAircraft;
    private DJIGimbal djiGimbal;

    private SwitchCompat wcGimbalPitchRange;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_remote_controller_setting, container, false);
        wcGimbalPitchRange = (SwitchCompat) view.findViewById(R.id.rc_gimbal_pitch_range);
        wcGimbalPitchRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                djiGimbal.setPitchRangeExtensionEnabled(checked, null);
            }
        });
        InitDJI();
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    };
    private void InitUI(View view){


    };
    private void InitDJI(){
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiGimbal=djiAircraft.getGimbal();
//        djiGimbal.setGimbalWorkMode();工作模式
        djiGimbal.setGimbalAdvancedSettingsStateUpdateCallback(new djiGimbalAdvancedSettingsStateUpdateCallback());
//        djiGimbal.setPitchRangeExtensionEnabled();
//        djiGimbal.setControllerSmoothingOnAxis();
//        djiAircraft.getFlightController().
    };
    private  class djiGimbalAdvancedSettingsStateUpdateCallback implements DJIGimbal.GimbalAdvancedSettingsStateUpdateCallback{
        @Override
        public void onGimbalAdvancedSettingsStateUpdate(DJIGimbal djiGimbal, DJIGimbalAdvancedSettingsState djiGimbalAdvancedSettingsState) {
//            djiGimbalAdvancedSettingsState.getAdvancedSettingsProfile();
            djiGimbalAdvancedSettingsState.getControllerSmoothingPitch();
            djiGimbalAdvancedSettingsState.getSmoothTrackAccelerationPitch();
//            djiGimbalAdvancedSettingsState.
        }
    }

}
