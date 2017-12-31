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

public class GetFlightFailSafeOp extends Task {


    public GetFlightFailSafeOp(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            flightController.getFlightFailsafeOperation(new DJICommonCallbacks.DJICompletionCallbackWith<DJIFlightFailsafeOperation>() {

                @Override
                public void onSuccess(DJIFlightFailsafeOperation operation) {
                    int operationStrId = DJIUtils.getMapValue(DJIUtils.failSafeOperationMap, operation);

                    Bundle bundle = new Bundle();
                    bundle.putInt("operationStrId", operationStrId);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DJI_DESC", djiError.getDescription());
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE;
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
