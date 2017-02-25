package com.dji.FPVDemo;

import android.util.Log;

import dji.common.error.DJIError;
import dji.common.gimbal.DJIGimbalAngleRotation;
import dji.common.gimbal.DJIGimbalCapabilityKey;
import dji.common.gimbal.DJIGimbalRotateAngleMode;
import dji.common.gimbal.DJIGimbalRotateDirection;
import dji.common.gimbal.DJIGimbalWorkMode;
import dji.common.util.DJICommonCallbacks;
import dji.common.util.DJIParamMinMaxCapability;
import dji.sdk.gimbal.DJIGimbal;

/**
 * Created by LEX on 2016/11/2.
 */
public class GimbalControl {

    private DJIGimbalAngleRotation mPitchRotation;
    private DJIGimbalAngleRotation mYawRotation;
    private DJIGimbalAngleRotation mRollRotation;


    protected void iniGimbal() {

        DJIGimbal gimbal = getGimbalInstance();

//          setup the pitch rotation
        mPitchRotation = new DJIGimbalAngleRotation(false, 0, DJIGimbalRotateDirection.Clockwise);
        mYawRotation = new DJIGimbalAngleRotation(false,0f, DJIGimbalRotateDirection.Clockwise);
        mRollRotation =new DJIGimbalAngleRotation(false,0f,DJIGimbalRotateDirection.Clockwise);


        if(gimbal != null && gimbal.gimbalCapability() != null && gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.AdjustPitch) !=null){
            mPitchRotation.enable=true;
//            Log.d("iniGimbal: ","set Gimabal adjustPitch success");
        } else {
            Log.d("iniGimbal: ","set Gimabal adjustPitch failed");
        }

//        enable the Pitch Extension if Possible

        if (gimbal.gimbalCapability().get(DJIGimbalCapabilityKey.PitchRangeExtension)!=null)
        {
            gimbal.setPitchRangeExtensionEnabled(true,
                    new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
//                                Log.d("PitchRangeExtension", "set PitchRangeExtension successfully");
                            }else{
                                Log.d("PitchRangeExtension", "set PitchRangeExtension failed");
                            }
                        }
                    }

            );
        }

//        set the gimbal work mode as Yawfllow mode (FPV and Free modes are not supported for drone)
        gimbal.setGimbalWorkMode(DJIGimbalWorkMode.YawFollowMode, new DJICommonCallbacks.DJICompletionCallback() {
            //            getGimbalInstance().setGimbalWorkMode(DJIGimbalWorkMode.YawFollowMode, new DJICommonCallbacks.DJICompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    Log.d("iniGimbal", "Set Gimbal Work Mode success");
                } else {
                    Log.d("iniGimbal", "Set Gimbal Work Mode failed" + error);
                }
            }
        });


    }



    private DJIGimbal getGimbalInstance() {
        return FPVDemoApplication.getProductInstance().getGimbal();
    }

    protected void rotateGimbalByAngle(float newAngle) {

        DJIGimbal gimbal = getGimbalInstance();

        if (gimbal != null ) {

            Object key = DJIGimbalCapabilityKey.AdjustPitch;
            Number minValue = ((DJIParamMinMaxCapability)(gimbal.gimbalCapability().get(key))).getMin();
            Number maxValue = ((DJIParamMinMaxCapability)(gimbal.gimbalCapability().get(key))).getMax();
            float pitch= newAngle/180*(maxValue.floatValue()-minValue.floatValue());

            mPitchRotation.angle = pitch;

//            gimbal.;

            gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.AbsoluteAngle, mPitchRotation, mRollRotation, mYawRotation,
                    new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Log.d("RotateGimbal", "RotateGimbal successfully");
                            }else{
                                Log.d("PitchRangeExtension", "RotateGimbal failed");
                            }
                        }
                    }
            );


        } else {
            Log.d("Gimbal is null", "Gimbal is null");
            return;

        }


    }
}
