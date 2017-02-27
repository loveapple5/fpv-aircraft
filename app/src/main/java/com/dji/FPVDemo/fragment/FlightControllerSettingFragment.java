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
import android.widget.ToggleButton;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerFlightMode;
import dji.common.flightcontroller.DJIFlightControlState;
import dji.common.flightcontroller.DJIFlightControllerSmartGoHomeStatus;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.flightcontroller.DJILocationCoordinate3D;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flight_controller_setting, container, false);
        btnGoHome = (Button) root.findViewById(R.id.btn_gohome);
        btnGoHome.setOnClickListener(new GoHomeBtnClickListener());
        scChangeFlightMode = (SwitchCompat) root.findViewById(R.id.sc_change_mode);
        scChangeFlightMode.setOnCheckedChangeListener(new ChangeFlightModeScListener());
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

    private class ChangeFlightModeScListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            state.setMultipModeOpen(checked);
            //state.setFlightMode();//飞行模式

        }
    }

    private class ChangeFlightModeBtnBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            //TODO:获取当前位置
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            //设置返航点位置
            //state.setHomepoint();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDJI();
    }

    private void InitDJI(){
        //----------------------------B设置菜单--------------------------------
        //--------------------------B2飞控参数设置--------------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiFlightController = djiAircraft.getFlightController();
        DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
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
