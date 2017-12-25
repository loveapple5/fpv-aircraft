package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;

public class GoHomeHeightMenuItem extends MenuItem {

    public GoHomeHeightMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            flightController.getGoHomeAltitude(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float aFloat) {
                    curValue = String.valueOf(aFloat.intValue());
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(GoHomeHeightMenuItem.this);
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }

    public void submitCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            float altitude = Float.valueOf(curValue);
            flightController.setGoHomeAltitude(altitude, null);
        }
    }

}
