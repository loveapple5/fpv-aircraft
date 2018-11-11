package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;

public class PhotoFormatMenuItem extends MenuItem {

    public PhotoFormatMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            if(djiCamera !=null) {
                djiCamera.getPhotoFileFormat(new DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoFileFormat>() {

                    @Override
                    public void onSuccess(DJICameraSettingsDef.CameraPhotoFileFormat cameraPhotoFileFormat) {
                        switch (cameraPhotoFileFormat) {
                            case JPEG:
                                curValue = values[0];
                                break;
                            case RAW:
                                curValue = values[1];
                                break;
                        }
                        if (fetchCallback != null) {
                            fetchCallback.onFetch(PhotoFormatMenuItem.this);
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
    }

    public void submitCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJICamera djiCamera = djiAircraft.getCamera();
            if(djiCamera !=null) {
                DJICameraSettingsDef.CameraPhotoFileFormat format = DJICameraSettingsDef.CameraPhotoFileFormat.JPEG;
                if (curValue.equals(values[1])) {
                    format = DJICameraSettingsDef.CameraPhotoFileFormat.RAW;
                }
                djiCamera.setPhotoFileFormat(format, null);
            }
        }
    }
}
