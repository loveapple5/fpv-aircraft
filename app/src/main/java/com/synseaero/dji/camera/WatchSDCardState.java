package com.synseaero.dji.camera;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.camera.CameraSDCardState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchSDCardState extends Task {


    public WatchSDCardState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();

            if (flag == 0) {
                camera.setDJIUpdateCameraSDCardStateCallBack(callback);
            } else {
                camera.setDJIUpdateCameraSDCardStateCallBack(null);
            }
        }
    }

    DJICamera.CameraUpdatedSDCardStateCallback callback = new DJICamera.CameraUpdatedSDCardStateCallback() {
        @Override
        public void onResult(CameraSDCardState cameraSDCardState) {

            int remainingSpaceMB = cameraSDCardState.getRemainingSpaceInMegaBytes();
            boolean isFull = cameraSDCardState.isFull();

            Bundle bundle = new Bundle();
            bundle.putInt("remainingSpaceMB", remainingSpaceMB);
            //SD卡存储不足
            bundle.putBoolean("isFull", isFull);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_SDCARD_STATE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
