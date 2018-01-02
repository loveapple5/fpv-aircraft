package com.synseaero.dji.sdkmanager;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.flightcontroller.FlyForbidStatus;
import dji.sdk.flightcontroller.DJIFlyZoneManager;
import dji.sdk.sdkmanager.DJISDKManager;

public class WatchFlyForbidStatus extends Task {

    public WatchFlyForbidStatus(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIFlyZoneManager manager = DJISDKManager.getInstance().getFlyZoneManager();
        if (manager != null) {
            if (flag == 0) {
                manager.setFlyForbidStatusUpdatedCallback(new Callback());
            } else {
                manager.setFlyForbidStatusUpdatedCallback(null);
            }
        }
    }

    private class Callback implements DJIFlyZoneManager.FlyForbidStatusUpdatedCallback {

        @Override
        public void onFlyForbidStatusUpdated(FlyForbidStatus flyForbidStatus) {
            int status = flyForbidStatus.value();

            Bundle bundle = new Bundle();
            bundle.putInt("status", status);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_FLY_FORBID_STATUS_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {

            }
        }
    }
}
