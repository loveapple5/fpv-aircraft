package com.dji.FPVDemo.fragment;


import dji.common.camera.DJICameraSettingsDef;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import java.util.ArrayList;

import dji.common.camera.DJICameraSettingsDef;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class CameraPhotoFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

    private Spinner spPhotoRatio;
    private Spinner spPhotoFormat;

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
        spPhotoRatio = (Spinner) view.findViewById(R.id.sp_photo_ratio);

        //数据
        ArrayList<String> RCRatioList = new ArrayList<String>();
        RCRatioList.add("4:3");
        RCRatioList.add("16:9");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter RCRatioAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, RCRatioList);
        //设置样式
        RCRatioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spPhotoRatio.setAdapter(RCRatioAdapter);
        spPhotoRatio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                DJICameraSettingsDef.CameraPhotoAspectRatio ratio = DJICameraSettingsDef.CameraPhotoAspectRatio.find(i);
                djiCamera.setPhotoRatio(ratio, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPhotoFormat = (Spinner) view.findViewById(R.id.sp_photo_format);
        //数据
        ArrayList<String> RCFormatList = new ArrayList<String>();
        RCFormatList.add("RAW");
        RCFormatList.add("JPG");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, RCFormatList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spPhotoFormat.setAdapter(RCModeAdapter);
        spPhotoFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DJICameraSettingsDef.CameraPhotoFileFormat format = DJICameraSettingsDef.CameraPhotoFileFormat.find(i);
                djiCamera.setPhotoFileFormat(format, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }
}
