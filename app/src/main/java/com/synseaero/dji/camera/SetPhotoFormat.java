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

public class SetPhotoFormat extends Task {

    private int formatResId;

    public SetPhotoFormat(Bundle data, Messenger messenger) {
        super(data, messenger);
        formatResId = data.getInt("formatResId");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();

            DJICameraSettingsDef.CameraPhotoFileFormat format = (DJICameraSettingsDef.CameraPhotoFileFormat) DJIUtils.getMapKey(DJIUtils.photoFormatMap, formatResId);

            camera.setPhotoFileFormat(format, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_PHOTO_FORMAT_RESPONSE;
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
                    bundle.putInt("formatResId", formatResId);

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
