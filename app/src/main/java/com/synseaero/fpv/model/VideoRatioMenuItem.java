package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class VideoRatioMenuItem extends MenuItem {

    private DJICameraSettingsDef.CameraVideoFrameRate frameRate;

    public VideoRatioMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            djiCamera.getVideoResolutionAndFrameRate(new DJICommonCallbacks.DJICompletionCallbackWithTwoParam<DJICameraSettingsDef.CameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraVideoResolution cameraVideoResolution, DJICameraSettingsDef.CameraVideoFrameRate cameraVideoFrameRate) {
                    frameRate = cameraVideoFrameRate;
                    switch (cameraVideoResolution) {
                        case Resolution_4096x2160:
                            curValue = values[0];
                            break;
                        case Resolution_1920x1080:
                            curValue = values[1];
                            break;
                        case Resolution_1280x720:
                            curValue = values[2];
                            break;
                    }
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(VideoRatioMenuItem.this);
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
            DJICameraSettingsDef.CameraVideoResolution resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_4096x2160;
            if (curValue.equals(values[1])) {
                resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_1920x1080;
            } else if (curValue.equals(values[2])) {
                resolution = DJICameraSettingsDef.CameraVideoResolution.Resolution_1280x720;
            }
            djiCamera.setVideoResolutionAndFrameRate(resolution, frameRate, null);
        }
    }

}
