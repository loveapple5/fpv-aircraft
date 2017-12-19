package com.dji.FPVDemo.dji;


public class MessageType {

    //通讯系统消息 0 - 100000
    //添加信使

    public static final int MSG_REGISTER_MESSENGER = 1;
    //移除信使
    public static final int MSG_UNREGISTER_MESSENGER = 2;

    //大疆sdk消息 100001 - 200000
    public static final int MSG_DJI_REQUEST_BASE = 100000;
    public static final int MSG_REGISTER_SDK = 100001;

    public static final int MSG_GET_GO_HOME_ALTITUDE = 100006;
    public static final int MSG_SET_GO_HOME_ALTITUDE = 100007;

    public static final int MSG_GET_WIFI_NAME = 100008;
    public static final int MSG_SET_WIFI_NAME = 100009;

    public static final int MSG_GET_WIFI_PASSWORD = 100010;
    public static final int MSG_SET_WIFI_PASSWORD = 100011;

    public static final int MSG_GET_GIMBAL_WHEEL_SPEED = 100012;
    public static final int MSG_SET_GIMBAL_WHEEL_SPEED = 100013;

    public static final int MSG_GET_REMOTE_CONTROLLER_MODE = 100014;
    public static final int MSG_SET_REMOTE_CONTROLLER_MODE = 100015;

    public static final int MSG_GET_GIMBAL_PITCH_EXTENSION = 100016;
    public static final int MSG_SET_GIMBAL_PITCH_EXTENSION = 100017;

    public static final int MSG_GET_SMOOTHING_ON_AXIS = 100018;
    public static final int MSG_SET_SMOOTHING_ON_AXIS = 100019;

    //大疆sdk返回消息 200001 - 300000
    public static final int MSG_DJI_RESPONSE_BASE = 200000;

    public static final int MSG_REGISTER_SDK_RESULT = 200001;
    public static final int MSG_PRODUCT_CHANGED = 200002;

    public static final int MSG_PRODUCT_CONNECTIVITY_CHANGED = 200003;
    public static final int MSG_COMPONENT_CHANGED = 200004;
    public static final int MSG_COMPONENT_CONNECTIVITY_CHANGED = 200005;

    public static final int MSG_GET_GO_HOME_ALTITUDE_RESPONSE = 200006;
    public static final int MSG_SET_GO_HOME_ALTITUDE_RESPONSE = 200007;

    public static final int MSG_GET_WIFI_NAME_RESPONSE = 200008;
    public static final int MSG_SET_WIFI_NAME_RESPONSE = 200009;

    public static final int MSG_GET_WIFI_PASSWORD_RESPONSE = 200010;
    public static final int MSG_SET_WIFI_PASSWORD_RESPONSE = 200011;

    public static final int MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE = 200012;
    public static final int MSG_SET_GIMBAL_WHEEL_SPEED_RESPONSE = 200013;

    public static final int MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE = 200014;
    public static final int MSG_SET_REMOTE_CONTROLLER_MODE_RESPONSE = 200015;

    public static final int MSG_GET_GIMBAL_PITCH_EXTENSION_RESPONSE = 200016;
    public static final int MSG_SET_GIMBAL_PITCH_EXTENSION_RESPONSE = 200017;

    public static final int MSG_GET_SMOOTHING_ON_AXIS_RESPONSE = 200018;
    public static final int MSG_SET_SMOOTHING_ON_AXIS_RESPONSE = 200019;
}
