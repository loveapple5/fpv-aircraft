package com.synseaero.dji.gimbal;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.gimbal.DJIGimbalAxis;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class GetGimbalSmoothingOnAxis extends Task {


    public GetGimbalSmoothingOnAxis(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIGimbal gimbal = aircraft.getGimbal();
            if(gimbal != null) {
                gimbal.getControllerSmoothingOnAxis(DJIGimbalAxis.Pitch, new DJICommonCallbacks.DJICompletionCallbackWith<Integer>() {

                    @Override
                    public void onSuccess(Integer smoothing) {
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_GET_SMOOTHING_ON_AXIS_RESPONSE;
                        Bundle bundle = new Bundle();
                        bundle.putInt("smoothing", smoothing);
                        message.setData(bundle);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {

                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_GET_SMOOTHING_ON_AXIS_RESPONSE;
                        Bundle bundle = new Bundle();
                        bundle.putString("DJI_DESC", djiError.getDescription());
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
