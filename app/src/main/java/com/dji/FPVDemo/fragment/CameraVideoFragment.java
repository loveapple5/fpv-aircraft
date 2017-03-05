package com.dji.FPVDemo.fragment;

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
import com.dji.FPVDemo.StatusListActivity;

import java.util.ArrayList;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;


public class CameraVideoFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

    private Spinner spVideoFormat;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------------------------B设置菜单--------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiCamera = djiAircraft.getCamera();

        //视频格式
        //djiCamera.setVideoFileFormat();
        //视频尺寸
        //djiCamera.setVideoResolutionAndFrameRate();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera_video, container, false);
        spVideoFormat = (Spinner) view.findViewById(R.id.sp_video_format);
        //数据
        ArrayList<String> RCFormatList = new ArrayList<String>();
        RCFormatList.add("MOV");
        RCFormatList.add("MP4");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, RCFormatList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spVideoFormat.setAdapter(RCModeAdapter);
        spVideoFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DJICameraSettingsDef.CameraVideoFileFormat format = DJICameraSettingsDef.CameraVideoFileFormat.find(i);
                djiCamera.setVideoFileFormat(format, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

}
