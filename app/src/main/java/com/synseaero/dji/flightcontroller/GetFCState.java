package com.synseaero.dji.flightcontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

/***
 * 获取飞机的位置速度姿态等信息
 */
public class GetFCState extends Task {

    public GetFCState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            DJIFlightControllerCurrentState curState = flightController.getCurrentState();

            double speed = Math.sqrt(Math.pow(curState.getVelocityX(), 2) + Math.pow(curState.getVelocityY(), 2));
            float vSpeed = -1 * curState.getVelocityZ();
            // get aircraft altitude
            double altitude;
            if (curState.isUltrasonicBeingUsed()) {
                altitude = curState.getUltrasonicHeight();
            } else {
                altitude = curState.getAircraftLocation().getAltitude();
            }

            double longA = curState.getAircraftLocation().getCoordinate2D().getLongitude();
            double longH = curState.getHomeLocation().getLongitude();
            double latA = curState.getAircraftLocation().getCoordinate2D().getLatitude();
            double latH = curState.getHomeLocation().getLatitude();

            double radLatA = Math.toRadians(latA);
            double radLatH = Math.toRadians(latH);
            double a = radLatA - radLatH;
            double b = Math.toRadians(longA)
                    - Math.toRadians(longH);
            double dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                    Math.cos(radLatA) * Math.cos(radLatH) * Math.pow(Math.sin(b / 2), 2)));
            dis = dis * 6378.137; //  the earth radius= 6378.137km
            //  distance into meter
            dis = Math.round(dis * 10000.0) / 10000.0 * 1000.0;

            //int gpsSignalLevel = curState.getGpsSignalStatus().value();

            Double heading = flightController.getCompass().getHeading();
            Double AircraftPitch = curState.getAttitude().pitch;
            Double AircraftRoll = curState.getAttitude().roll;
            Double AircraftYaw = curState.getAttitude().yaw;

            int flightTime = curState.getFlightTime();

            Bundle bundle = new Bundle();
            bundle.putDouble("speed", speed);
            bundle.putFloat("vSpeed", vSpeed);
            bundle.putDouble("altitude", altitude);
            bundle.putDouble("distance", dis);
            //bundle.putInt("gpsSignalLevel", gpsSignalLevel);

            bundle.putDouble("longA", longA);
            bundle.putDouble("longH", longH);
            bundle.putDouble("latA", latA);
            bundle.putDouble("latH", latH);

            bundle.putDouble("AircraftPitch", AircraftPitch);
            bundle.putDouble("AircraftRoll", AircraftRoll);
            bundle.putDouble("AircraftYaw", AircraftYaw);
            bundle.putDouble("Head", heading);

            bundle.putInt("flightTime", flightTime);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_FC_STATE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
