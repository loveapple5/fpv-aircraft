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

public class GetPhotoRatio extends Task {


    public GetPhotoRatio(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();
            camera.getPhotoRatio(new DJICommonCallbacks.DJICompletionCallbackWith<DJICameraSettingsDef.CameraPhotoAspectRatio>() {

                @Override
                public void onSuccess(DJICameraSettingsDef.CameraPhotoAspectRatio cameraPhotoAspectRatio) {
                    Bundle bundle = new Bundle();
                    int ratioResId = DJIUtils.getMapValue(DJIUtils.photoResolutionMap, cameraPhotoAspectRatio);
                    bundle.putInt("ratioResId", ratioResId);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_PHOTO_RATIO_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DJI_DESC", djiError.getDescription());
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_PHOTO_RATIO_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
