package com.synseaero.dji.airlink;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.airlink.SignalQualityUpdatedCallback;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJILBAirLink;
import dji.sdk.airlink.DJIOcuSyncLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchDownLinkSignalQuality extends Task {

    public WatchDownLinkSignalQuality(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIAirLink airLink = aircraft.getAirLink();
            if (airLink.isOcuSyncLinkSupported()) {

                DJIOcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                if (flag == 0) {
                    ocuSyncLink.setDownlinkSignalQualityUpdatedCallback(callback);
                } else {
                    ocuSyncLink.setDownlinkSignalQualityUpdatedCallback(null);
                }

            } else if (airLink.isLBAirLinkSupported()) {

                DJILBAirLink lbAirLink = airLink.getLBAirLink();
                if (flag == 0) {
                    lbAirLink.setVideoSignalStrengthChangeCallback(callback);
                } else {
                    lbAirLink.setVideoSignalStrengthChangeCallback(null);
                }

            } else if (airLink.isWiFiLinkSupported()) {

                DJIWiFiLink wifiLink = airLink.getWiFiLink();
                if (flag == 0) {
                    wifiLink.setVideoSignalStrengthChangeCallback(callback);
                } else {
                    wifiLink.setVideoSignalStrengthChangeCallback(null);
                }

            }

        }
    }

    private SignalQualityUpdatedCallback callback = new SignalQualityUpdatedCallback() {

        @Override
        public void onChange(int percent) {
            Bundle bundle = new Bundle();
            bundle.putInt("percent", percent);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_DOWN_LINK_SIGNAL_QUALITY_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


}
