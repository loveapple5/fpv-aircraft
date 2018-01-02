package com.synseaero.dji;


import android.os.Bundle;
import android.os.Messenger;

import com.synseaero.dji.aircraft.GetFirmwarePackageVersion;
import com.synseaero.dji.aircraft.WatchDiagnostics;
import com.synseaero.dji.airlink.GetWifiSSId;
import com.synseaero.dji.airlink.GetWifiPassword;
import com.synseaero.dji.airlink.SetWifiPassword;
import com.synseaero.dji.airlink.SetWifiSSId;
import com.synseaero.dji.airlink.WatchDownLinkSignalQuality;
import com.synseaero.dji.airlink.WatchUpLinkSignalQuality;
import com.synseaero.dji.battery.GetSelfDischargeDay;
import com.synseaero.dji.battery.GetSeriesNumber;
import com.synseaero.dji.battery.SetSelfDischargeDay;
import com.synseaero.dji.battery.WatchBatteryState;
import com.synseaero.dji.camera.FormatSDCard;
import com.synseaero.dji.camera.WatchExposure;
import com.synseaero.dji.camera.WatchSDCardState;
import com.synseaero.dji.flightcontroller.GetFCState;
import com.synseaero.dji.flightcontroller.GetFlightFailSafeOp;
import com.synseaero.dji.flightcontroller.GetGoHomeAltitude;
import com.synseaero.dji.flightcontroller.GetGoHomeBatteryThreshold;
import com.synseaero.dji.flightcontroller.GetHomeLocation;
import com.synseaero.dji.flightcontroller.GetFCInfoState;
import com.synseaero.dji.flightcontroller.GetLandingBatteryThreshold;
import com.synseaero.dji.flightcontroller.GetLedEnabled;
import com.synseaero.dji.flightcontroller.GetMaxFlightHeight;
import com.synseaero.dji.flightcontroller.GetMaxFlightRadius;
import com.synseaero.dji.flightcontroller.GetVPEnabled;
import com.synseaero.dji.flightcontroller.SetFlightFailSafeOp;
import com.synseaero.dji.flightcontroller.SetGoHomeAltitude;
import com.synseaero.dji.flightcontroller.SetGoHomeBatteryThreshold;
import com.synseaero.dji.flightcontroller.SetHomeLocation;
import com.synseaero.dji.flightcontroller.SetLandingBatteryThreshold;
import com.synseaero.dji.flightcontroller.SetLedEnabled;
import com.synseaero.dji.flightcontroller.SetMaxFlightHeight;
import com.synseaero.dji.flightcontroller.SetMaxFlightRadius;
import com.synseaero.dji.flightcontroller.SetVPEnabled;
import com.synseaero.dji.gimbal.GetGimbalSmoothingOnAxis;
import com.synseaero.dji.gimbal.GetPitchExtension;
import com.synseaero.dji.gimbal.RotateByAngle;
import com.synseaero.dji.gimbal.SetGimbalSmoothingOnAxis;
import com.synseaero.dji.gimbal.SetPitchExtension;
import com.synseaero.dji.gimbal.WatchGimbalState;
import com.synseaero.dji.remotecontroller.WatchRCBatteryState;
import com.synseaero.dji.remotecontroller.WatchRCHardwareState;
import com.synseaero.dji.remotecontroller.GetRCMode;
import com.synseaero.dji.remotecontroller.GetWheelSpeed;
import com.synseaero.dji.remotecontroller.SetRCMode;
import com.synseaero.dji.remotecontroller.SetWheelSpeed;
import com.synseaero.dji.sdkmanager.WatchFlyForbidStatus;

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
            case (MessageType.MSG_SET_HOME_LOCATION): {

                return new SetHomeLocation(data, messenger);

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
            case (MessageType.MSG_GET_FC_INFO_STATE): {

                return new GetFCInfoState(data, messenger);
            }
            case (MessageType.MSG_GET_BATTERY_SERIES_NUMBER): {

                return new GetSeriesNumber(data, messenger);
            }
            case (MessageType.MSG_GET_MAX_FLIGHT_HEIGHT): {

                return new GetMaxFlightHeight(data, messenger);
            }
            case (MessageType.MSG_SET_MAX_FLIGHT_HEIGHT): {

                return new SetMaxFlightHeight(data, messenger);
            }
            case (MessageType.MSG_GET_MAX_FLIGHT_RADIUS): {

                return new GetMaxFlightRadius(data, messenger);
            }
            case (MessageType.MSG_SET_MAX_FLIGHT_RADIUS): {

                return new SetMaxFlightRadius(data, messenger);
            }
            case (MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP): {

                return new GetFlightFailSafeOp(data, messenger);
            }
            case (MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP): {

                return new SetFlightFailSafeOp(data, messenger);
            }
            case (MessageType.MSG_GET_LED_ENABLED): {

                return new GetLedEnabled(data, messenger);
            }
            case (MessageType.MSG_SET_LED_ENABLED): {

                return new SetLedEnabled(data, messenger);
            }
            case (MessageType.MSG_GET_VP_ENABLED): {

                return new GetVPEnabled(data, messenger);
            }
            case (MessageType.MSG_SET_VP_ENABLED): {

                return new SetVPEnabled(data, messenger);
            }
            case (MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD): {

                return new GetGoHomeBatteryThreshold(data, messenger);
            }
            case (MessageType.MSG_SET_GO_HOME_BATTERY_THRESHOLD): {

                return new SetGoHomeBatteryThreshold(data, messenger);
            }
            case (MessageType.MSG_GET_LANDING_BATTERY_THRESHOLD): {

                return new GetLandingBatteryThreshold(data, messenger);
            }
            case (MessageType.MSG_SET_LANDING_BATTERY_THRESHOLD): {

                return new SetLandingBatteryThreshold(data, messenger);
            }
            case (MessageType.MSG_WATCH_DOWN_LINK_SIGNAL_QUALITY): {

                return new WatchDownLinkSignalQuality(data, messenger);
            }
            case (MessageType.MSG_WATCH_UP_LINK_SIGNAL_QUALITY): {

                return new WatchUpLinkSignalQuality(data, messenger);
            }
            case (MessageType.MSG_ROTATE_GIMBAL_BY_ANGLE): {

                return new RotateByAngle(data, messenger);
            }
            case (MessageType.MSG_WATCH_FLY_FORBID_STATUS): {

                return new WatchFlyForbidStatus(data, messenger);
            }
            case (MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION): {

                return new GetFirmwarePackageVersion(data, messenger);
            }
            case (MessageType.MSG_WATCH_DIAGNOSTIS): {

                return new WatchDiagnostics(data, messenger);
            }
            case (MessageType.MSG_FORMAT_SDCARD): {

                return new FormatSDCard(data, messenger);
            }
        }
        return null;
    }


}
