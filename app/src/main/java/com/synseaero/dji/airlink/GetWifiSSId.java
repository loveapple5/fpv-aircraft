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

public class GetWifiSSId extends Task {

    public GetWifiSSId(Bundle data, Messenger messenger) {
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
                bundle.putString("DJI_DESC", "该型号飞行器没有wifi模块");
                Message message = Message.obtain();
                message.what = MessageType.MSG_GET_WIFI_NAME_RESPONSE;
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {

                }
            } else {
                wiFiLink.getWiFiSSID(new DJICommonCallbacks.DJICompletionCallbackWith<String>() {

                    @Override
                    public void onSuccess(String wifiSSId) {
                        Log.d("GetWifiSSId", wifiSSId);
                        Bundle bundle = new Bundle();
                        bundle.putString("wifiSSId", wifiSSId);
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_GET_WIFI_NAME_RESPONSE;
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
                        message.what = MessageType.MSG_GET_WIFI_NAME_RESPONSE;
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