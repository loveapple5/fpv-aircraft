package com.dji.FPVDemo.dji;


import android.os.Bundle;
import android.os.Messenger;

import com.dji.FPVDemo.dji.flightcontroller.GetGoHomeAltitude;
import com.dji.FPVDemo.dji.flightcontroller.SetGoHomeAltitude;
import com.dji.FPVDemo.dji.gimbal.GetGimbalSmoothingOnAxis;
import com.dji.FPVDemo.dji.gimbal.GetPitchExtension;
import com.dji.FPVDemo.dji.gimbal.SetGimbalSmoothingOnAxis;
import com.dji.FPVDemo.dji.gimbal.SetPitchExtension;
import com.dji.FPVDemo.dji.remotecontroller.GetRCMode;
import com.dji.FPVDemo.dji.remotecontroller.GetWheelSpeed;
import com.dji.FPVDemo.dji.remotecontroller.SetRCMode;
import com.dji.FPVDemo.dji.remotecontroller.SetWheelSpeed;

public class TaskFactory {

    public static Task createTask(int messageId, Bundle data, Messenger messenger) {

        if (messageId == MessageType.MSG_GET_GO_HOME_ALTITUDE) {

            return new GetGoHomeAltitude(data, messenger);

        } else if (messageId == MessageType.MSG_SET_GO_HOME_ALTITUDE) {

            return new SetGoHomeAltitude(data, messenger);

        } else if (messageId == MessageType.MSG_GET_GIMBAL_WHEEL_SPEED) {

            return new GetWheelSpeed(data, messenger);

        } else if (messageId == MessageType.MSG_SET_GIMBAL_WHEEL_SPEED) {

            return new SetWheelSpeed(data, messenger);

        } else if (messageId == MessageType.MSG_GET_REMOTE_CONTROLLER_MODE) {

            return new GetRCMode(data, messenger);

        } else if (messageId == MessageType.MSG_SET_REMOTE_CONTROLLER_MODE) {

            return new SetRCMode(data, messenger);

        } else if (messageId == MessageType.MSG_GET_GIMBAL_PITCH_EXTENSION) {

            return new GetPitchExtension(data, messenger);

        } else if (messageId == MessageType.MSG_SET_GIMBAL_PITCH_EXTENSION) {

            return new SetPitchExtension(data, messenger);

        } else if (messageId == MessageType.MSG_GET_SMOOTHING_ON_AXIS) {

            return new GetGimbalSmoothingOnAxis(data, messenger);

        } else if (messageId == MessageType.MSG_SET_SMOOTHING_ON_AXIS) {

            return new SetGimbalSmoothingOnAxis(data, messenger);

        }
        return null;
    }


}
