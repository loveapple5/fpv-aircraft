package com.synseaero.dji.camera;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;
import com.synseaero.util.StringUtils;

import java.util.Locale;

import dji.common.camera.DJICameraExposureParameters;
import dji.common.camera.DJICameraSettingsDef;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.camera.DJICamera;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchExposure extends Task {


    public WatchExposure(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJICamera camera = aircraft.getCamera();

            if (flag == 0) {
                camera.setCameraUpdatedCurrentExposureValuesCallback(callback);
            } else {
                camera.setCameraUpdatedCurrentExposureValuesCallback(null);
            }
        }

    }

    DJICamera.CameraUpdatedCurrentExposureValuesCallback callback = new DJICamera.CameraUpdatedCurrentExposureValuesCallback() {

        @Override
        public void onResult(DJICameraExposureParameters exposure) {
            DJICameraSettingsDef.CameraAperture aperture = exposure.getAperture();
            DJICameraSettingsDef.CameraShutterSpeed speed = exposure.getShutterSpeed();
            DJICameraSettingsDef.CameraISO ISO = exposure.getISO();
            DJICameraSettingsDef.CameraExposureCompensation compensation = exposure.getExposureCompensation();

            String strSpeed;
            float flSpeed = speed.value();
            if (flSpeed >= 1) {
                strSpeed = String.format(Locale.getDefault(), "%.2f", flSpeed);
            } else {
                float denominator = 1 / flSpeed;
                StringBuilder sb = new StringBuilder();
                strSpeed = sb.append("1/").append(String.format(Locale.getDefault(), "%.2f", denominator)).toString();
            }

            float flAperture = (float) aperture.value() / 100;
            String strAperture = String.format(Locale.getDefault(), "%.1f", flAperture);

            String strISO = StringUtils.getISOString(ISO);
            String EV = StringUtils.getEVString(compensation);

            Bundle bundle = new Bundle();
            bundle.putString("shutterSpeed", strSpeed);
            bundle.putString("aperture", "f " + strAperture);
            bundle.putString("ISO", "ISO " + strISO);
            bundle.putString("EV", "EV " + EV);
            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_CAMERA_EXPOSURE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

}
