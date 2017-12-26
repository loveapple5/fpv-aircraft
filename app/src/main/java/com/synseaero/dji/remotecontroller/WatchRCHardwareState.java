package com.synseaero.dji.remotecontroller;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.remotecontroller.DJIRCHardwareState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchRCHardwareState extends Task {

    public WatchRCHardwareState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();

            if (flag == 0) {
                remoteController.setHardwareStateUpdateCallback(callback);
            } else {
                remoteController.setHardwareStateUpdateCallback(null);
            }

        }
    }

    DJIRemoteController.RCHardwareStateUpdateCallback callback = new DJIRemoteController.RCHardwareStateUpdateCallback() {

        @Override
        public void onHardwareStateUpdate(DJIRemoteController djiRemoteController, DJIRCHardwareState djircHardwareState) {
            boolean c2Present = djircHardwareState.customButton2.isPresent;
            boolean c2Down = djircHardwareState.customButton2.buttonDown;

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_RC_HARDWARE_STATE_RESPONSE;
            Bundle bundle = new Bundle();
            bundle.putBoolean("c2Present", c2Present);
            bundle.putBoolean("c2Down", c2Down);
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {

            }
        }
    };
}
