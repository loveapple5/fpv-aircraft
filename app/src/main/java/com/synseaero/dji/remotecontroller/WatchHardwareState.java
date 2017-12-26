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


public class WatchHardwareState extends Task {

    public WatchHardwareState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            remoteController.setHardwareStateUpdateCallback(new DJIRemoteController.RCHardwareStateUpdateCallback() {

                @Override
                public void onHardwareStateUpdate(DJIRemoteController djiRemoteController, DJIRCHardwareState djircHardwareState) {
                    boolean c2Present = djircHardwareState.customButton2.isPresent;
                    boolean c2Down = djircHardwareState.customButton2.buttonDown;

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_FC_HARDWARE_STATE_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("c2Present", c2Present);
                    bundle.putBoolean("c2Down", c2Down);
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
