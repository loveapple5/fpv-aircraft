package com.synseaero.fpv.model;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;

public class LEDMenuItem extends MenuItem {

    public LEDMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void fetchCurValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController controller = djiAircraft.getFlightController();
            controller.getLEDsEnabled(new DJICommonCallbacks.DJICompletionCallbackWith<Boolean>() {

                @Override
                public void onSuccess(Boolean aBoolean) {
                    curValue = aBoolean ? "true" : "false";
                    if (fetchCallback != null) {
                        fetchCallback.onFetch(LEDMenuItem.this);
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
            DJIFlightController controller = djiAircraft.getFlightController();
            controller.setLEDsEnabled("true".equalsIgnoreCase(curValue), null);
        }
    }
}
