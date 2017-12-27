package com.synseaero.dji;


import android.os.Bundle;
import android.os.Messenger;

import com.synseaero.dji.airlink.GetWifiSSId;
import com.synseaero.dji.airlink.GetWifiPassword;
import com.synseaero.dji.airlink.SetWifiPassword;
import com.synseaero.dji.airlink.SetWifiSSId;
import com.synseaero.dji.battery.GetSelfDischargeDay;
import com.synseaero.dji.battery.SetSelfDischargeDay;
import com.synseaero.dji.battery.WatchBatteryState;
import com.synseaero.dji.camera.WatchExposure;
import com.synseaero.dji.camera.WatchSDCardState;
import com.synseaero.dji.flightcontroller.GetFCState;
import com.synseaero.dji.flightcontroller.GetGoHomeAltitude;
import com.synseaero.dji.flightcontroller.GetHomeLocation;
import com.synseaero.dji.flightcontroller.SetGoHomeAltitude;
import com.synseaero.dji.gimbal.GetGimbalSmoothingOnAxis;
import com.synseaero.dji.gimbal.GetPitchExtension;
import com.synseaero.dji.gimbal.SetGimbalSmoothingOnAxis;
import com.synseaero.dji.gimbal.SetPitchExtension;
import com.synseaero.dji.gimbal.WatchGimbalState;
import com.synseaero.dji.remotecontroller.WatchRCBatteryState;
import com.synseaero.dji.remotecontroller.WatchRCHardwareState;
import com.synseaero.dji.remotecontroller.GetRCMode;
import com.synseaero.dji.remotecontroller.GetWheelSpeed;
import com.synseaero.dji.remotecontroller.SetRCMode;
import com.synseaero.dji.remotecontroller.SetWheelSpeed;

public class TaskFactory {

    public static Task createTask(int messageId, Bundle data, Messenger messenger) {

        switch (messageId) {
            case (MessageType.MSG_GET_GO_HOME_ALTITUDE): {

                return new GetGoHomeAltitude(data, messenger);

            }
            case (MessageType.MSG_SET_GO_HOME_ALTITUDE): {

                return new SetGoHomeAltitude(data, messenger);

            }
            case (MessageType.MSG_GET_GIMBAL_WHEEL_SPEED): {

                return new GetWheelSpeed(data, messenger);

            }
            case (MessageType.MSG_SET_GIMBAL_WHEEL_SPEED): {

                return new SetWheelSpeed(data, messenger);

            }
            case (MessageType.MSG_GET_REMOTE_CONTROLLER_MODE): {

                return new GetRCMode(data, messenger);

            }
            case (MessageType.MSG_SET_REMOTE_CONTROLLER_MODE): {

                return new SetRCMode(data, messenger);

            }
            case (MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION): {

                return new GetPitchExtension(data, messenger);

            }
            case (MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION): {

                return new SetPitchExtension(data, messenger);

            }
            case (MessageType.MSG_GET_SMOOTHING_ON_AXIS): {

                return new GetGimbalSmoothingOnAxis(data, messenger);

            }
            case (MessageType.MSG_SET_SMOOTHING_ON_AXIS): {

                return new SetGimbalSmoothingOnAxis(data, messenger);

            }
            case (MessageType.MSG_GET_WIFI_NAME): {

                return new GetWifiSSId(data, messenger);

            }
            case (MessageType.MSG_GET_WIFI_PASSWORD): {

                return new GetWifiPassword(data, messenger);

            }
            case (MessageType.MSG_SET_WIFI_NAME): {

                return new SetWifiSSId(data, messenger);

            }
            case (MessageType.MSG_SET_WIFI_PASSWORD): {

                return new SetWifiPassword(data, messenger);

            }
            case (MessageType.MSG_GET_HOME_LOCATION): {

                return new GetHomeLocation(data, messenger);

            }
            case (MessageType.MSG_GET_FC_STATE): {

                return new GetFCState(data, messenger);

            }
            case (MessageType.MSG_WATCH_RC_HARDWARE_STATE): {

                return new WatchRCHardwareState(data, messenger);

            }
            case (MessageType.MSG_WATCH_CAMERA_EXPOSURE): {

                return new WatchExposure(data, messenger);

            }
            case (MessageType.MSG_WATCH_RC_BATTERY_STATE): {

                return new WatchRCBatteryState(data, messenger);

            }
            case (MessageType.MSG_WATCH_BATTERY_STATE): {

                return new WatchBatteryState(data, messenger);

            }
            case (MessageType.MSG_WATCH_GIMBAL_STATE): {

                return new WatchGimbalState(data, messenger);

            }
            case (MessageType.MSG_WATCH_SDCARD_STATE): {

                return new WatchSDCardState(data, messenger);
            }
            case (MessageType.MSG_GET_BATTERY_DISCHARGE_DAY): {

                return new GetSelfDischargeDay(data, messenger);
            }
            case (MessageType.MSG_SET_BATTERY_DISCHARGE_DAY): {

                return new SetSelfDischargeDay(data, messenger);
            }
        }
        return null;
    }


}
