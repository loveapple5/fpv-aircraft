package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class PhotoRatioMenuItem extends MenuItem {

    public PhotoRatioMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            djiCamera.getPhotoRatio(new DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoAspectRatio>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraPhotoAspectRatio cameraPhotoAspectRatio) {
                    switch(cameraPhotoAspectRatio) {
                        case AspectRatio_4_3:
                            curValue = values[0];
                            break;
                        case AspectRatio_16_9:
                            curValue = values[1];
                            break;
                    }
                    if(fetchCallback != null) {
                        fetchCallback.onFetch(PhotoRatioMenuItem.this);
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
            DJICameraSettingsDef.CameraPhotoAspectRatio ratio = DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_4_3;
            if(curValue.equals(values[1])) {
                ratio =  DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_16_9;
            }
            djiCamera.setPhotoRatio(ratio, null);
        }
    }
}
