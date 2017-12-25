package com.synseaero.dji.remotecontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;
import com.synseaero.util.DJIUtils;

import dji.common.error.DJIError;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetRCMode extends Task {

    private int controlStyleResId;

    public SetRCMode(Bundle data, Messenger messenger) {
        super(data, messenger);
        controlStyleResId = data.getInt("controlStyleResId");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            final DJIRCControlStyle controlStyle = (DJIRCControlStyle) DJIUtils.getMapKey(DJIUtils.rcStyleMap1, controlStyleResId);
            DJIRCControlMode mode = new DJIRCControlMode();
            mode.controlStyle = controlStyle;
            remoteController.setRCControlMode(mode, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_REMOTE_CONTROLLER_MODE_RESPONSE;
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
                    bundle.putInt("controlStyle", controlStyle.value());
                    bundle.putInt("controlStyleResId", controlStyleResId);
                    bundle.putInt("controlStyleImageResId1", DJIUtils.getMapValue(DJIUtils.rcStyleMap2, controlStyle));
                    bundle.putInt("controlStyleImageResId2", DJIUtils.getMapValue(DJIUtils.rcStyleMap3, controlStyle));

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
