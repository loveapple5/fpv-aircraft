package com.synseaero.dji.camera;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class FormatSDCard extends Task {

    public FormatSDCard(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();
            if(camera != null) {
                camera.formatSDCard(new DJICommonCallbacks.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError djiError) {
                        Bundle bundle = new Bundle();
                        if (djiError != null) {
                            bundle.putString("DJI_DESC", djiError.getDescription());
                        }
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_FORMAT_SDCARD_RESPONSE;
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
}
