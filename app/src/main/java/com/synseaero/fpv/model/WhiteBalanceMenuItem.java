package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class WhiteBalanceMenuItem extends MenuItem {

    public WhiteBalanceMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null && djiAircraft.getCamera() != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            djiCamera.getWhiteBalanceAndColorTemperature(new DJICommonCallbacks.DJICompletionCallbackWithTwoParam<DJICameraSettingsDef.CameraWhiteBalance, Integer>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraWhiteBalance whiteBalance, Integer integer) {
                    switch (whiteBalance) {
                        case Auto:
                            curValue = values[0];
                            break;
                        case Sunny:
                            curValue = values[1];
                            break;
                        case Cloudy:
                            curValue = values[2];
                            break;
                    }
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(WhiteBalanceMenuItem.this);
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
            DJICameraSettingsDef.CameraWhiteBalance whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Auto;
            if (curValue.equals(values[1])) {
                whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Sunny;
            } else if (curValue.equals(values[2])) {
                whiteBalance = DJICameraSettingsDef.CameraWhiteBalance.Cloudy;
            }
            djiCamera.setWhiteBalanceAndColorTemperature(whiteBalance, 0, null);
        }
    }

}
