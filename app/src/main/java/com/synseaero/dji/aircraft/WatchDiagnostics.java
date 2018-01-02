package com.synseaero.dji.aircraft;

import android.os.Bundle;
import android.os.Messenger;

import com.synseaero.dji.Task;

import java.util.List;

import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchDiagnostics extends Task {

    public WatchDiagnostics(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            if(flag == 0) {
                aircraft.setUpdateDiagnosticsListCallback(new Callback());
            } else {
                aircraft.setUpdateDiagnosticsListCallback(null);
            }
        }
    }

    private class Callback implements DJIDiagnostics.UpdateDiagnosticsListCallback {

        @Override
        public void onDiagnosticsListUpdate(List<DJIDiagnostics> list) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {

                }
            }
        }
    }
}
