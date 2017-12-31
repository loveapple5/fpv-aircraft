package com.synseaero.dji.flightcontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;
import com.synseaero.util.DJIUtils;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetFlightFailSafeOp extends Task {

    private int operationStrId;

    public SetFlightFailSafeOp(Bundle data, Messenger messenger) {
        super(data, messenger);
        operationStrId = data.getInt("operationStrId");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            Object key = DJIUtils.getMapKey(DJIUtils.failSafeOperationMap, operationStrId);
            DJIFlightFailsafeOperation operation = (DJIFlightFailsafeOperation) key;
            flightController.setFlightFailsafeOperation(operation, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("operationStrId", operationStrId);
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP_RESPONSE;
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
