package com.synseaero.dji.gimbal;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetPitchExtension extends Task {

    private boolean pitchExtensionEnable;

    public SetPitchExtension(Bundle data, Messenger messenger) {
        super(data, messenger);
        pitchExtensionEnable = data.getBoolean("pitchExtensionEnable");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIGimbal gimbal = aircraft.getGimbal();
            if(gimbal != null) {
                gimbal.setPitchRangeExtensionEnabled(pitchExtensionEnable, new DJICommonCallbacks.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError djiError) {
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION_RESPONSE;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("pitchExtensionEnable", pitchExtensionEnable);
                        if (djiError != null) {
                            bundle.putString("DJI_DESC", djiError.getDescription());
                        }

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
