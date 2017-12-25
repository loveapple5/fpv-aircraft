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
import com.synseaero.fpv.R;

import java.util.ArrayList;

import dji.common.camera.DJICameraSettingsDef;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class CameraFragment extends Fragment {

    private DJIAircraft djiAircraft;
    private DJICamera djiCamera;

    private Spinner spCameraMode;
    private Spinner spISO;
    private Spinner spShutterSpeed;
    private Spinner spExposreCompensation;

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
        //djiCamera.setISO();
        //djiCamera.setExposureCompensation();
        //djiCamera.setShutterSpeed();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera_setting, container, false);
        spCameraMode = (Spinner) view.findViewById(R.id.sp_camera_mode);
        spISO = (Spinner) view.findViewById(R.id.sp_camera_iso);
        spShutterSpeed = (Spinner) view.findViewById(R.id.sp_camera_shutter_speed);
        spExposreCompensation = (Spinner) view.findViewById(R.id.sp_exposure_compensation);

        initCameraMode();
        initCameraISO();
        initCameraShutterSpeed();
        initCameraExposureCompensation();
        return view;
    }

    private void initCameraMode() {
        //数据
        ArrayList<String> cameraModeList = new ArrayList<String>();
        cameraModeList.add("program");
        cameraModeList.add("manual");

        //适配器
        ArrayAdapter cameraModeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cameraModeList);
        //设置样式
        cameraModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spCameraMode.setAdapter(cameraModeAdapter);
        spCameraMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraExposureMode mode = DJICameraSettingsDef.CameraExposureMode.Manual;
                if (position == 0) {
                    mode = DJICameraSettingsDef.CameraExposureMode.Program;
                }
                djiCamera.setExposureMode(mode, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCameraISO() {
        //数据
        ArrayList<String> ISOList = new ArrayList<String>();
        ISOList.add("100");
        ISOList.add("200");
        ISOList.add("400");
        ISOList.add("800");
        ISOList.add("1600");

        //适配器
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ISOList);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spISO.setAdapter(adapter);
        spISO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraISO iso;
                switch(position) {
                    case 0:
                        iso = DJICameraSettingsDef.CameraISO.ISO_100;
                        break;
                    case 1:
                        iso = DJICameraSettingsDef.CameraISO.ISO_200;
                        break;
                    case 2:
                        iso = DJICameraSettingsDef.CameraISO.ISO_400;
                        break;
                    case 3:
                        iso = DJICameraSettingsDef.CameraISO.ISO_800;
                        break;
                    case 4:
                        iso = DJICameraSettingsDef.CameraISO.ISO_1600;
                        break;
                    default:
                        iso = DJICameraSettingsDef.CameraISO.ISO_100;
                        break;
                }
                djiCamera.setISO(iso, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCameraShutterSpeed() {
        //数据
        ArrayList<String> shutterSpeedList = new ArrayList<String>();
        shutterSpeedList.add("8000");
        shutterSpeedList.add("6400");
        shutterSpeedList.add("5000");
        shutterSpeedList.add("4000");

        //适配器
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, shutterSpeedList);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spShutterSpeed.setAdapter(adapter);
        spShutterSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraShutterSpeed speed;
                switch(position) {
                    case 0:
                        speed = DJICameraSettingsDef.CameraShutterSpeed.ShutterSpeed1_8000;
                        break;
                    case 1:
                        speed = DJICameraSettingsDef.CameraShutterSpeed.ShutterSpeed1_6400;
                        break;
                    case 2:
                        speed = DJICameraSettingsDef.CameraShutterSpeed.ShutterSpeed1_5000;
                        break;
                    default:
                        speed = DJICameraSettingsDef.CameraShutterSpeed.ShutterSpeed1_4000;
                        break;
                }

                djiCamera.setShutterSpeed(speed, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void initCameraExposureCompensation() {
        //数据
        ArrayList<String> list = new ArrayList<String>();
        list.add("-0.3");
        list.add("0.7");

        //适配器
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        //设置样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spExposreCompensation.setAdapter(adapter);
        spExposreCompensation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                DJICameraSettingsDef.CameraExposureCompensation compensation;
                switch(position) {
                    case 0:
                        compensation = DJICameraSettingsDef.CameraExposureCompensation.N_0_3;
                        break;
                    case 1:
                        compensation = DJICameraSettingsDef.CameraExposureCompensation.P_0_7;
                        break;
                    default:
                        compensation = DJICameraSettingsDef.CameraExposureCompensation.N_0_3;
                        break;
                }

                djiCamera.setExposureCompensation(compensation, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
