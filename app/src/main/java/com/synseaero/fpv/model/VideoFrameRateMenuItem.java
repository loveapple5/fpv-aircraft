package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class VideoFrameRateMenuItem extends MenuItem {

    private DJICameraSettingsDef.CameraVideoResolution resolution;

    public VideoFrameRateMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            djiCamera.getVideoResolutionAndFrameRate(new DJICommonCallbacks.DJICompletionCallbackWithTwoParam<DJICameraSettingsDef.CameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraVideoResolution cameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate cameraVideoFrameRate) {
                    resolution = cameraVideoResolution;
                    switch (cameraVideoFrameRate) {
                        case FrameRate_24FPS:
                            curValue = values[0];
                            break;
                        case FrameRate_30FPS:
                            curValue = values[1];
                            break;
                        case FrameRate_60FPS:
                            curValue = values[2];
                            break;
                    }
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(VideoFrameRateMenuItem.this);
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }

    public void submitCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            DJICameraSettingsDef.CameraVideoFrameRate frameRate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_24FPS;
            if (curValue.equals(values[1])) {
                frameRate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_30FPS;
            } else if (curValue.equals(values[2])) {
                frameRate = DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_60FPS;
            }
            djiCamera.setVideoResolutionAndFrameRate(resolution, frameRate, null);
        }
    }
}
