package com.dji.FPVDemo.dji.remotecontroller;

import android.os.Bundle;
import android.os.Messenger;

import com.dji.FPVDemo.dji.Task;

import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


public class StopGetHardwareState extends Task {

    public StopGetHardwareState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            remoteController.setHardwareStateUpdateCallback(null);
        }
    }
}
