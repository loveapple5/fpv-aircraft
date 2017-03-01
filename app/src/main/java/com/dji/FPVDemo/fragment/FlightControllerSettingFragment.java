package com.dji.FPVDemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerFlightMode;
import dji.common.flightcontroller.DJIFlightControlState;
import dji.common.flightcontroller.DJIFlightControllerSmartGoHomeStatus;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.flightcontroller.DJILocationCoordinate3D;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
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
    private SwitchCompat scChangeFlightMode;
    private EditText etHomeHeight;
    private Button btnHomeHeight;

    private EditText etMaxFlightHeight;
    private Button btnMaxFlightHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flight_controller_setting, container, false);
        btnGoHome = (Button) root.findViewById(R.id.btn_gohome);
        btnGoHome.setOnClickListener(new GoHomeBtnClickListener());
        scChangeFlightMode = (SwitchCompat) root.findViewById(R.id.sc_change_mode);
        scChangeFlightMode.setOnCheckedChangeListener(new ChangeFlightModeScListener());

        etHomeHeight = (EditText) root.findViewById(R.id.et_home_height);
        btnHomeHeight = (Button) root.findViewById(R.id.btn_home_height);
        btnHomeHeight.setOnClickListener(new GoHomeHeightClickListener());

        etMaxFlightHeight = (EditText) root.findViewById(R.id.et_max_flight_height);
        btnMaxFlightHeight = (Button) root.findViewById(R.id.btn_max_flight_height);
        btnMaxFlightHeight.setOnClickListener(new MaxFlightHeightClickListener());
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
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
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
        DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
        djiFlightController.setFlightFailsafeOperation();//通讯中断后措施
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
