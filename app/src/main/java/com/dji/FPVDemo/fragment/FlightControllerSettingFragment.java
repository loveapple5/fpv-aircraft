package com.dji.FPVDemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import java.util.ArrayList;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerFlightMode;
import dji.common.flightcontroller.DJIFlightControlState;
import dji.common.flightcontroller.DJIFlightControllerSmartGoHomeStatus;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.flightcontroller.DJILocationCoordinate3D;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightLimitation;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;


public class FlightControllerSettingFragment extends Fragment {

    private DJIAircraft djiAircraft;

    private DJIFlightController djiFlightController;
    private DJIRemoteController djiRemoteController;

    private Button btnGoHome;
    private Switch scChangeFlightMode;

    private EditText etHomeHeight;
    private Button btnHomeHeight;

    private EditText etMaxFlightHeight;
    private Button btnMaxFlightHeight;

    private EditText etMaxFlightRadius;
    private Button btnMaxFlightRadius;

    private Switch scNoviceMode;
    private Spinner spFailSafe;
    private Switch scSmartGoHome;
    private Switch scLED;
    private Switch scVisionPositioning;

    private ArrayList failSafeModeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flight_controller_setting, container, false);
        btnGoHome = (Button) root.findViewById(R.id.btn_gohome);
        btnGoHome.setOnClickListener(new GoHomeBtnClickListener());
        scChangeFlightMode = (Switch) root.findViewById(R.id.sc_change_mode);
        scChangeFlightMode.setOnCheckedChangeListener(new ChangeFlightModeScListener());

        etHomeHeight = (EditText) root.findViewById(R.id.et_home_height);
        btnHomeHeight = (Button) root.findViewById(R.id.btn_home_height);
        btnHomeHeight.setOnClickListener(new GoHomeHeightClickListener());

        etMaxFlightHeight = (EditText) root.findViewById(R.id.et_max_flight_height);
        btnMaxFlightHeight = (Button) root.findViewById(R.id.btn_max_flight_height);
        btnMaxFlightHeight.setOnClickListener(new MaxFlightHeightClickListener());

        etMaxFlightRadius = (EditText) root.findViewById(R.id.et_max_flight_radius);
        btnMaxFlightRadius = (Button) root.findViewById(R.id.btn_max_flight_radius);
        btnMaxFlightRadius.setOnClickListener(new MaxFlightRadiusClickListener());

        scNoviceMode = (Switch) root.findViewById(R.id.sc_novice_mode);
        scNoviceMode.setOnCheckedChangeListener(new NoviceModeScListener());

        scSmartGoHome = (Switch) root.findViewById(R.id.sc_smart_go_home);
        scSmartGoHome.setOnCheckedChangeListener(new SmartGoHomeScListener());
        scLED = (Switch) root.findViewById(R.id.sc_led);
        scLED.setOnCheckedChangeListener(new LEDScListener());
        scVisionPositioning = (Switch) root.findViewById(R.id.sc_vision_positioning);
        scVisionPositioning.setOnCheckedChangeListener(new VisionPositioningScListener());

        spFailSafe = (Spinner) root.findViewById(R.id.sc_fail_safe);
        //数据
        failSafeModeList = new ArrayList<String>();
        failSafeModeList.add("悬停");
        failSafeModeList.add("下降");
        failSafeModeList.add("返航");

        //适配器
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, failSafeModeList);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spFailSafe.setAdapter(adapter);
        spFailSafe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DJIFlightFailsafeOperation op = DJIFlightFailsafeOperation.find(i);
                djiFlightController.setFlightFailsafeOperation(op, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
    }

    private class GoHomeBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            DJILocationCoordinate3D location3D = state.getAircraftLocation();
            //设置返航点位置
            DJILocationCoordinate2D location2D = location3D.getCoordinate2D();
            state.setHomepoint(location2D);
            state.setHomePointSet(true);
        }
    }

    private class GoHomeHeightClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String text = etHomeHeight.getText().toString();
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            int height = Integer.parseInt(text);
            int getHeight = state.getGoHomeHeight();
            Log.d("fc", "getHeight:" + getHeight);
            Log.d("fc", "setHeight:" + height);
            if (height >= 20 && height <= 500) {
                state.setGoHomeHeight(height);//返航高度
            } else {
                Toast.makeText(getActivity(), R.string.home_height_toast, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ChangeFlightModeScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            state.setMultipModeOpen(checked);
            //state.setFlightMode();//飞行模式

        }
    }

    private class MaxFlightHeightClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String text = etMaxFlightHeight.getText().toString();
            float height = Float.parseFloat(text);
            if (height >= 20 && height <= 500) {
                DJIFlightLimitation limitation = djiFlightController.getFlightLimitation();
                limitation.setMaxFlightHeight(height, new MaxFlightHeightCallback());//最大升限
            } else {
                Toast.makeText(getActivity(), R.string.max_flight_height_toast, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MaxFlightHeightCallback implements DJICommonCallbacks.DJICompletionCallback {

        @Override
        public void onResult(DJIError djiError) {

        }
    }

    private class MaxFlightRadiusClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String text = etMaxFlightRadius.getText().toString();
            float height = Float.parseFloat(text);
            if (height >= 20 && height <= 500) {
                DJIFlightLimitation limitation = djiFlightController.getFlightLimitation();
                limitation.setMaxFlightRadius(height, new MaxFlightRadiusCallback());//最大距离
                limitation.setMaxFlightRadiusLimitationEnabled(true, new MaxFlightRadiusCallback());
            } else {
                Toast.makeText(getActivity(), R.string.max_flight_radius_toast, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MaxFlightRadiusCallback implements DJICommonCallbacks.DJICompletionCallback {

        @Override
        public void onResult(DJIError djiError) {

        }
    }

    private class NoviceModeScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (checked) {
                DJIFlightLimitation limitation = djiFlightController.getFlightLimitation();
                limitation.setMaxFlightHeight(30, new MaxFlightHeightCallback());//最大升限
                limitation.setMaxFlightRadius(30, new MaxFlightRadiusCallback());//最大距离
                limitation.setMaxFlightRadiusLimitationEnabled(true, new MaxFlightRadiusCallback());
            }

        }
    }

    private class SmartGoHomeScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            state.getSmartGoHomeStatus().setAircraftShouldGoHome(checked);//设置自动返航
//        state.getSmartGoHomeStatus().setBatteryPercentageNeededToGoHome();//设置自动返航电量
        }
    }

    private class LEDScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            djiFlightController.setLEDsEnabled(checked, null);//LED开关
        }
    }

    private class VisionPositioningScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (djiFlightController.getIntelligentFlightAssistant() != null) {
                djiFlightController.getIntelligentFlightAssistant().setVisionPositioningEnabled(checked, null);//视觉定位
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDJI();
    }

    private void InitDJI() {
        //----------------------------B设置菜单--------------------------------
        //--------------------------B2飞控参数设置--------------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiFlightController = djiAircraft.getFlightController();
        //返航点位置
        //state.setHomepoint();
//        state.setGoHomeHeight();//返航高度
//
//        state.setMultipModeOpen();
//        state.getSmartGoHomeStatus().setAircraftShouldGoHome();//设置自动返航
//        state.getSmartGoHomeStatus().setBatteryPercentageNeededToGoHome();//设置自动返航电量
//        DJIFlightLimitation limitation = djiFlightController.getFlightLimitation();
//        limitation.setMaxFlightHeight();//最大升限
//        limitation.setMaxFlightRadius();//最大飞行半径
//
//        djiFlightController.setLEDsEnabled();//LED开关
//        djiFlightController.getIntelligentFlightAssistant().setVisionPositioningEnabled();//视觉定位
    }
}
