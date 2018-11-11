package com.synseaero.dji.camera;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;
import com.synseaero.util.DJIUtils;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetPhotoRatio extends Task {

    private int ratioResId;

    public SetPhotoRatio(Bundle data, Messenger messenger) {
        super(data, messenger);
        ratioResId = data.getInt("ratioResId");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();
            if(camera != null) {
                DJICameraSettingsDef.CameraPhotoAspectRatio ratio = (DJICameraSettingsDef.CameraPhotoAspectRatio) DJIUtils.getMapKey(DJIUtils.photoResolutionMap, ratioResId);
                camera.setPhotoRatio(ratio, new DJICommonCallbacks.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError djiError) {
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_SET_PHOTO_RATIO_RESPONSE;
                        Bundle bundle = new Bundle();
                        if (djiError != null) {
                            bundle.putString("DJI_DESC", djiError.getDescription());
                        }
                        bundle.putInt("ratioResId", ratioResId);

                        message.setData(bundle);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {

                        }
                    }
                });
            }
        }
    }
}
