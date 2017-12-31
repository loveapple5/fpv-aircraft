package com.synseaero.dji.airlink;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import java.util.ArrayList;

import dji.common.airlink.DJISignalInformation;
import dji.common.airlink.SignalQualityUpdatedCallback;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIAuxLink;
import dji.sdk.airlink.DJILBAirLink;
import dji.sdk.airlink.DJIOcuSyncLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchUpLinkSignalQuality extends Task {

    public WatchUpLinkSignalQuality(Bundle data, Messenger messenger) {
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
                    ocuSyncLink.setUplinkSignalQualityUpdatedCallback(callback);
                } else {
                    ocuSyncLink.setUplinkSignalQualityUpdatedCallback(null);
                }

            } else if (airLink.isLBAirLinkSupported()) {
                DJILBAirLink lbAirLink = airLink.getLBAirLink();
                if (flag == 0) {
                    lbAirLink.setLBAirLinkUpdatedRemoteControllerSignalInformationCallback(lbCallback);
                } else {
                    lbAirLink.setLBAirLinkUpdatedRemoteControllerSignalInformationCallback(null);
                }

            } else if (airLink.isAuxLinkSupported()) {
                DJIAuxLink auxLink = airLink.getAuxLink();
                if (flag == 0) {
                    auxLink.setAuxLinkUpdatedRemoteControllerSignalInformationCallback(alCallback);
                } else {
                    auxLink.setAuxLinkUpdatedRemoteControllerSignalInformationCallback(null);
                }
            } else if (airLink.isWiFiLinkSupported()) {

                DJIWiFiLink wiFiLink = airLink.getWiFiLink();
                if (flag == 0) {
                    wiFiLink.setRemoteControllerStrengthChangeCallback(callback);
                } else {
                    wiFiLink.setRemoteControllerStrengthChangeCallback(null);
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
            message.what = MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private DJILBAirLink.DJILBAirLinkUpdatedRemoteControllerSignalInformationCallback lbCallback =
            new DJILBAirLink.DJILBAirLinkUpdatedRemoteControllerSignalInformationCallback() {

                @Override
                public void onResult(ArrayList<DJISignalInformation> antennas) {
                    int percent = 0;
                    for (DJISignalInformation antenna : antennas) {
                        percent += antenna.getPercent();
                    }
                    int length = antennas.size();
                    percent = percent / length;

                    Bundle bundle = new Bundle();
                    bundle.putInt("percent", percent);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            };

    private DJIAuxLink.DJIAuxLinkUpdatedRemoteControllerSignalInformationCallback alCallback =
            new DJIAuxLink.DJIAuxLinkUpdatedRemoteControllerSignalInformationCallback() {

                @Override
                public void onResult(ArrayList<DJISignalInformation> antennas) {
                    int percent = 0;
                    for (DJISignalInformation antenna : antennas) {
                        percent += antenna.getPercent();
                    }
                    int length = antennas.size();
                    percent = percent / length;

                    Bundle bundle = new Bundle();
                    bundle.putInt("percent", percent);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
}
