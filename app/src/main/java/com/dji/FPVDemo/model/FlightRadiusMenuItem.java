package com.dji.FPVDemo.model;


import com.dji.FPVDemo.FPVDemoApplication;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightLimitation;
import dji.sdk.products.DJIAircraft;

public class FlightRadiusMenuItem extends MenuItem {

    public FlightRadiusMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController flightController = djiAircraft.getFlightController();
            DJIFlightLimitation limitation = flightController.getFlightLimitation();
            limitation.getMaxFlightRadius(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float aFloat) {
                    curValue = String.valueOf(aFloat.intValue());
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(FlightRadiusMenuItem.this);
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
            DJIFlightLimitation limitation = flightController.getFlightLimitation();
            float height = Float.valueOf(curValue);
            limitation.setMaxFlightRadius(height, null);
        }
    }
}
