package com.synseaero.fpv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.synseaero.fpv.FPVDemoApplication;

import java.util.ArrayList;

import dji.common.camera.DJICameraSettingsDef;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;


public class CameraVideoFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

    private Spinner spVideoFormat;
    private Spinner spVideoResolve;
    private Spinner spVideoStandard;

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

        View view = inflater.inflate(com.synseaero.fpv.R.layout.fragment_camera_video, container, false);
        spVideoFormat = (Spinner) view.findViewById(com.synseaero.fpv.R.id.sp_video_format);
        spVideoResolve = (Spinner) view.findViewById(com.synseaero.fpv.R.id.sp_video_resolve);
        spVideoStandard = (Spinner) view.findViewById(com.synseaero.fpv.R.id.sp_video_standard);
        initVideoFormat();
        initVideoResolve();
        initVideoStandard();
        return view;
    }

    private void initVideoFormat() {
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
                if(djiCamera != null) {
                    djiCamera.setVideoFileFormat(format, null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initVideoResolve() {
        //数据
        ArrayList<String> resolveList = new ArrayList<String>();
        resolveList.add("2704×1520/24fps");
        resolveList.add("2704×1520/30fps");
        resolveList.add("1920×1080/24fps");
        resolveList.add("1920×1080/30fps");
        resolveList.add("1280×720/24fps");
        resolveList.add("1280×720/24fps");
        resolveList.add("1280×720/48fps");
        resolveList.add("1280×720/60fps");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter resolveAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, resolveList);
        //设置样式
        resolveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spVideoResolve.setAdapter(resolveAdapter);
        spVideoResolve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraVideoResolution resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_1280x720;
                if(position >= 0 && position <= 1) {
                    resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_2704X1520;
                } else if(position >= 2 && position <= 3) {
                    resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_1920x1080;
                }
                DJICameraSettingsDef.CameraVideoFrameRate rate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_24FPS;
                if(position == 1 || position == 3) {
                    rate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_30FPS;
                } else if(position == 6) {
                    rate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_48FPS;
                } else {
                    rate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_60FPS;
                }
                djiCamera.setVideoResolutionAndFrameRate(resolution, rate, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initVideoStandard() {
        //数据
        ArrayList<String> RCFormatList = new ArrayList<String>();
        RCFormatList.add("PAL");
        RCFormatList.add("NTSC");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, RCFormatList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spVideoStandard.setAdapter(RCModeAdapter);
        spVideoStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraVideoStandard standard = DJICameraSettingsDef.CameraVideoStandard.find(position);
                djiCamera.setVideoStandard(standard, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
