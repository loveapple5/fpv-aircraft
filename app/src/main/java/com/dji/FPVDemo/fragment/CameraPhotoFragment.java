package com.dji.FPVDemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class CameraPhotoFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------------------------B设置菜单--------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiCamera = djiAircraft.getCamera();
        //djiCamera.setCameraMode();
        //开始拍照
        //djiCamera.startShootPhoto();
        //照片比例
        //djiCamera.setPhotoRatio();
        //照片格式
        //djiCamera.setPhotoFileFormat();
        //白平衡
        //djiCamera.setWhiteBalanceAndColorTemperature();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera_photo, container, false);

        return view;
    }
}
