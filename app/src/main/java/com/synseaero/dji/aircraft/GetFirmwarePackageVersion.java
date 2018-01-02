package com.synseaero.dji.aircraft;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class GetFirmwarePackageVersion extends Task {

    public GetFirmwarePackageVersion(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            String version = aircraft.getFirmwarePackageVersion();
            Log.d("GetFirmwareVersion", "version:" + version);

            Bundle bundle = new Bundle();
            bundle.putString("version", version);
            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {

            }
        }
    }
}
