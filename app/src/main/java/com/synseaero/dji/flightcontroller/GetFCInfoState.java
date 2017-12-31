package com.synseaero.dji.flightcontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.flightcontroller.DJIAircraftRemainingBatteryState;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIGoHomeStatus;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

/***
 * 获取飞机的状态信息，如飞行模式，电量不足，达到最大高度，达到最大距离等
 */
public class GetFCInfoState extends Task {

    public GetFCInfoState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            DJIFlightControllerCurrentState curState = flightController.getCurrentState();
            //DJIAircraftRemainingBatteryState batteryState = curState.getRemainingBattery();

            //飞行中
            boolean flying = curState.isFlying();
            //飞机剩余电量 1和2都是电量不足
            //int batteryLevel = batteryState.value();
            //飞行模式
            String flightMode = curState.getFlightModeString();

            DJIGoHomeStatus goHomeStatus = curState.getGoHomeStatus();
            //飞机已降落
            boolean goHomeCompleted = goHomeStatus == DJIGoHomeStatus.Completion;
            //到达最大飞行高度
            boolean reachLimitedHeight = curState.isReachLimitedHeight();
            //到达最大飞行距离
            boolean reachLimitedRadius = curState.isReachLimitedRadius();

            Bundle bundle = new Bundle();
            bundle.putBoolean("flying", flying);
            //bundle.putInt("batteryLevel", batteryLevel);
            bundle.putString("flightMode", flightMode);
            bundle.putBoolean("goHomeCompleted", goHomeCompleted);
            bundle.putBoolean("reachLimitedHeight", reachLimitedHeight);
            bundle.putBoolean("reachLimitedRadius", reachLimitedRadius);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_FC_INFO_STATE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
