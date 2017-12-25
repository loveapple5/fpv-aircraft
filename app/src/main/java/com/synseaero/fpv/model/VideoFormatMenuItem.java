package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class VideoFormatMenuItem extends MenuItem {

    public VideoFormatMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            djiCamera.getVideoFileFormat(new DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraVideoFileFormat>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraVideoFileFormat format) {
                    switch (format) {
                        case MOV:
                            curValue = values[0];
                            break;
                        case MP4:
                            curValue = values[1];
                            break;
                    }
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(VideoFormatMenuItem.this);
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
            DJICameraSettingsDef.CameraVideoFileFormat format = DJICameraSettingsDef.CameraVideoFileFormat.MOV;
            if (curValue.equals(values[1])) {
                format = DJICameraSettingsDef.CameraVideoFileFormat.MP4;
            }
            djiCamera.setVideoFileFormat(format, null);
        }
    }
}
