package com.synseaero.dji.airlink;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class GetWifiPassword extends Task {

    public GetWifiPassword(Bundle data, Messenger messenger) {
        super(data, messenger);
    }


    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIAirLink airLink = aircraft.getAirLink();
            DJIWiFiLink wiFiLink = airLink.getWiFiLink();
            if (wiFiLink == null) {
                Bundle bundle = new Bundle();
                bundle.putString("DJI_DESC", "该型号飞机没有wifi");
                Message message = Message.obtain();
                message.what = MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE;
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {

                }
            } else {
                wiFiLink.getWiFiPassword(new DJICommonCallbacks.DJICompletionCallbackWith<String>() {

                    @Override
                    public void onSuccess(String password) {
                        Log.d("GetWifiPassword", password);
                        Bundle bundle = new Bundle();
                        bundle.putString("wifiPassword", password);
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE;
                        message.setData(bundle);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {

                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Bundle bundle = new Bundle();
                        bundle.putString("DJI_DESC", djiError.getDescription());
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_GET_WIFI_PASSWORD_RESPONSE;
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
