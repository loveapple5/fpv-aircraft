package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class ShutterMenuItem extends MenuItem {

    public ShutterMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null && djiAircraft.getCamera() != null) {
            DJICamera djiCamera = djiAircraft.getCamera();

            djiCamera.getExposureMode(new DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraExposureMode>() {
                @Override
                public void onSuccess(DJICameraSettingsDef.CameraExposureMode cameraExposureMode) {
                    if (cameraExposureMode == DJICameraSettingsDef.CameraExposureMode.Program) {
                        curValue = values[0];
                    } else {
                        curValue = values[1];
                    }
                    if(fetchCallback != null) {
                        fetchCallback.onFetch(ShutterMenuItem.this);
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
        if (djiAircraft != null && djiAircraft.getCamera() != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            DJICameraSettingsDef.CameraExposureMode curMode = DJICameraSettingsDef.CameraExposureMode.Program;
            if (curValue.equals(values[1])) {
                curMode = DJICameraSettingsDef.CameraExposureMode.Manual;
            }
            djiCamera.setExposureMode(curMode, null);
        }
    }
}
