package com.synseaero.fpv.dji.airlink;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.fpv.dji.MessageType;
import com.synseaero.fpv.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetWifiSSId extends Task {

    private String wifiSSId;

    public SetWifiSSId(Bundle data, Messenger messenger) {
        super(data, messenger);
        wifiSSId = data.getString("wifiSSId");
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
                bundle.putString("DJI_DESC", "该型号飞行器没有wifi模块");
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_WIFI_NAME_RESPONSE;
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                wiFiLink.setWiFiSSID(wifiSSId, new DJICommonCallbacks.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError djiError) {
                        Bundle bundle = new Bundle();
                        if (djiError != null) {
                            bundle.putString("DJI_DESC", djiError.getDescription());
                        }
                        bundle.putString("wifiSSId", wifiSSId);
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_SET_WIFI_NAME_RESPONSE;
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