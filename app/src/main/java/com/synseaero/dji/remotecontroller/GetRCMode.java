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
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;

public class GetRCMode extends Task {


    public GetRCMode(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            remoteController.getRCControlMode(new DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode>() {

                @Override
                public void onSuccess(DJIRCControlMode djircControlMode) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putInt("controlStyle", djircControlMode.controlStyle.value());
                    bundle.putInt("controlStyleResId", DJIUtils.getMapValue(DJIUtils.rcStyleMap1, djircControlMode.controlStyle));
                    bundle.putInt("controlStyleImageResId1", DJIUtils.getMapValue(DJIUtils.rcStyleMap2, djircControlMode.controlStyle));
                    bundle.putInt("controlStyleImageResId2", DJIUtils.getMapValue(DJIUtils.rcStyleMap3, djircControlMode.controlStyle));
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {

                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putString("DJI_DESC", djiError.getDescription());
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
