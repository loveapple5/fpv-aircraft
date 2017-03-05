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


public class CameraVideoFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

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

        return view;
    }

}
