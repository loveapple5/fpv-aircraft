package com.synseaero.dji.camera;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.camera.CameraSystemState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class WatchCameraStatus extends Task {

    public WatchCameraStatus(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();
            camera.setDJICameraUpdatedSystemStateCallback(callback);
        }
    }

    DJICamera.CameraUpdatedSystemStateCallback callback = new DJICamera.CameraUpdatedSystemStateCallback() {

        @Override
        public void onResult(CameraSystemState cameraSystemState) {
            boolean isRecording = cameraSystemState.isRecording();
            int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();

            Bundle bundle = new Bundle();
            bundle.putBoolean("isRecording", isRecording);
            bundle.putInt("recordTime", recordTime);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_CAMERA_STATUS_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
