package com.synseaero.dji.gimbal;

import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.gimbal.DJIGimbalAngleRotation;
import dji.common.gimbal.DJIGimbalRotateAngleMode;
import dji.common.gimbal.DJIGimbalRotateDirection;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class RotateByAngle extends Task {

    private float pitch;
    private float yaw;

    public RotateByAngle(Bundle data, Messenger messenger) {
        super(data, messenger);
        pitch = data.getFloat("pitch", 0);
        yaw = data.getFloat("yaw", 0);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIGimbal gimbal = aircraft.getGimbal();

            DJIGimbalAngleRotation mPitchRotation = new DJIGimbalAngleRotation(false, 0f, DJIGimbalRotateDirection.Clockwise);
            DJIGimbalAngleRotation mYawRotation = new DJIGimbalAngleRotation(false, 0f, DJIGimbalRotateDirection.Clockwise);
            DJIGimbalAngleRotation mRollRotation = new DJIGimbalAngleRotation(false, 0f, DJIGimbalRotateDirection.Clockwise);

            //if (gimbal.gimbalCapability() != null && gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustPitch) != null) {
//                Number minValue = ((DJIParamMinMaxCapability) (gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustPitch))).getMin();
//                Number maxValue = ((DJIParamMinMaxCapability) (gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustPitch))).getMax();
//                float rotatePitch = pitch / 180 * (maxValue.floatValue() - minValue.floatValue());
            mPitchRotation.enable = true;
            mPitchRotation.angle = pitch;
//                Log.d("RotateByAngle", "mPitchRotation.maxValue:" + maxValue.floatValue());
//                Log.d("RotateByAngle", "mPitchRotation.minValue:" + minValue.floatValue());
            Log.d("RotateByAngle", "mPitchRotation.angle:" + mPitchRotation.angle);
            //}
            //if (gimbal.gimbalCapability() != null && gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustYaw) != null) {
            //    Number minValue = ((DJIParamMinMaxCapability) (gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustYaw))).getMin();
            //    Number maxValue = ((DJIParamMinMaxCapability) (gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustYaw))).getMax();
            //    float rotateYaw = yaw / 180 * (maxValue.floatValue() - minValue.floatValue());
                mYawRotation.enable = true;
                mYawRotation.angle = yaw;
                Log.d("RotateByAngle", "mYawRotation.angle:" + mYawRotation.angle);
            //}

            if(gimbal != null) {
                gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.AbsoluteAngle, mPitchRotation, mRollRotation, mYawRotation,
                        new DJICommonCallbacks.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
//                            Message message = Message.obtain();
//                            message.what = MessageType.MSG_ROTATE_GIMBAL_BY_ANGLE_RESPONSE;
//                            Bundle bundle = new Bundle();
//                            if (djiError != null) {
//                                bundle.putString("DJI_DESC", djiError.getDescription());
//                            }
//                            message.setData(bundle);
//                            try {
//                                messenger.send(message);
//                            } catch (RemoteException e) {
//
//                            }
                            }
                        }
                );

            }
        }
    }
}
