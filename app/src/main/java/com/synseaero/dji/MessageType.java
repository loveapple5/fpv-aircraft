package com.synseaero.dji;


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

    public static final int MSG_GET_HOME_LOCATION = 100020;
    public static final int MSG_SET_HOME_LOCATION = 100021;

    public static final int MSG_GET_FC_STATE = 100022;

    public static final int MSG_WATCH_RC_HARDWARE_STATE = 100023;

    public static final int MSG_WATCH_CAMERA_EXPOSURE = 100024;

    public static final int MSG_WATCH_RC_BATTERY_STATE = 100025;

    public static final int MSG_WATCH_BATTERY_STATE = 100026;

    public static final int MSG_WATCH_GIMBAL_STATE = 100027;

    public static final int MSG_WATCH_SDCARD_STATE = 100028;

    public static final int MSG_GET_BATTERY_DISCHARGE_DAY = 100029;
    public static final int MSG_SET_BATTERY_DISCHARGE_DAY = 100030;

    public static final int MSG_GET_FC_INFO_STATE = 100031;

    public static final int MSG_GET_BATTERY_SERIES_NUMBER = 100032;

    //最大升限
    public static final int MSG_GET_MAX_FLIGHT_HEIGHT = 100033;
    public static final int MSG_SET_MAX_FLIGHT_HEIGHT = 100034;

    //获取最大距离
    public static final int MSG_GET_MAX_FLIGHT_RADIUS = 100035;
    public static final int MSG_SET_MAX_FLIGHT_RADIUS = 100036;

    public static final int MSG_GET_FLIGHT_FAIL_SAFE_OP = 100037;
    public static final int MSG_SET_FLIGHT_FAIL_SAFE_OP = 100038;

    public static final int MSG_GET_LED_ENABLED = 100039;
    public static final int MSG_SET_LED_ENABLED = 100040;
    //视觉定位开关
    public static final int MSG_GET_VP_ENABLED = 100041;
    public static final int MSG_SET_VP_ENABLED = 100042;

    //自动返航电量
    public static final int MSG_GET_GO_HOME_BATTERY_THRESHOLD = 100043;
    public static final int MSG_SET_GO_HOME_BATTERY_THRESHOLD = 100044;

    //自动降落电量
    public static final int MSG_GET_LANDING_BATTERY_THRESHOLD = 100045;
    public static final int MSG_SET_LANDING_BATTERY_THRESHOLD = 100046;

    //数据传输信号强度
    public static final int MSG_WATCH_DOWN_LINK_SIGNAL_QUALITY = 100047;
    public static final int MSG_WATCH_UP_LINK_SIGNAL_QUALITY = 100048;

    public static final int MSG_ROTATE_GIMBAL_BY_ANGLE = 100049;

    public static final int MSG_WATCH_FLY_FORBID_STATUS = 100050;

    public static final int MSG_GET_AIRCRAFT_FIRM_VERSION = 100051;

    public static final int MSG_WATCH_DIAGNOSTIS = 100052;

    public static final int MSG_FORMAT_SDCARD = 100053;

    public static final int MSG_GET_PHOTO_RATIO = 1000054;
    public static final int MSG_SET_PHOTO_RATIO = 1000055;

    public static final int MSG_GET_PHOTO_FORMAT = 1000056;
    public static final int MSG_SET_PHOTO_FORMAT = 1000057;

    public static final int MSG_GET_PHOTO_WB_AND_CT = 1000058;
    public static final int MSG_SET_PHOTO_WB_AND_CT = 1000059;

    public static final int MSG_GET_VIDEO_R_AND_FR = 1000060;
    public static final int MSG_SET_VIDEO_R_AND_FR = 1000061;

    public static final int MSG_GET_VIDEO_FORMAT = 1000062;
    public static final int MSG_SET_VIDEO_FORMAT = 1000063;

    public static final int MSG_GET_VIDEO_STANDARD = 1000064;
    public static final int MSG_SET_VIDEO_STANDARD = 1000065;

    public static final int MSG_WATCH_CAMERA_STATUS = 1000066;

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

    public static final int MSG_GET_HOME_LOCATION_RESPONSE = 200020;
    public static final int MSG_SET_HOME_LOCATION_RESPONSE = 200021;

    public static final int MSG_GET_FC_STATE_RESPONSE = 200022;

    public static final int MSG_GET_RC_HARDWARE_STATE_RESPONSE = 200023;

    public static final int MSG_GET_CAMERA_EXPOSURE_RESPONSE = 200024;

    public static final int MSG_GET_RC_BATTERY_STATE_RESPONSE = 200025;

    public static final int MSG_GET_BATTERY_STATE_RESPONSE = 200026;

    public static final int MSG_GET_GIMBAL_STATE_RESPONSE = 200027;

    public static final int MSG_GET_SDCARD_STATE_RESPONSE = 200028;

    public static final int MSG_GET_BATTERY_DISCHARGE_DAY_RESPONSE = 200029;
    public static final int MSG_SET_BATTERY_DISCHARGE_DAY_RESPONSE = 200030;

    public static final int MSG_GET_FC_INFO_STATE_RESPONSE = 200031;

    public static final int MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE = 200032;

    //最大升限
    public static final int MSG_GET_MAX_FLIGHT_HEIGHT_RESPONSE = 200033;
    public static final int MSG_SET_MAX_FLIGHT_HEIGHT_RESPONSE = 200034;

    //获取最大距离
    public static final int MSG_GET_MAX_FLIGHT_RADIUS_RESPONSE = 200035;
    public static final int MSG_SET_MAX_FLIGHT_RADIUS_RESPONSE = 200036;

    public static final int MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE = 200037;
    public static final int MSG_SET_FLIGHT_FAIL_SAFE_OP_RESPONSE = 200038;

    public static final int MSG_GET_LED_ENABLED_RESPONSE = 200039;
    public static final int MSG_SET_LED_ENABLED_RESPONSE = 200040;

    public static final int MSG_GET_VP_ENABLED_RESPONSE = 200041;
    public static final int MSG_SET_VP_ENABLED_RESPONSE = 200042;

    //自动返航电量
    public static final int MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE = 200043;
    public static final int MSG_SET_GO_HOME_BATTERY_THRESHOLD_RESPONSE = 200044;

    //自动降落电量
    public static final int MSG_GET_LANDING_BATTERY_THRESHOLD_RESPONSE = 200045;
    public static final int MSG_SET_LANDING_BATTERY_THRESHOLD_RESPONSE = 200046;

    //数据传输信号强度
    public static final int MSG_GET_DOWN_LINK_SIGNAL_QUALITY_RESPONSE = 200047;
    public static final int MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE = 200048;

    public static final int MSG_ROTATE_GIMBAL_BY_ANGLE_RESPONSE = 200049;

    public static final int MSG_GET_FLY_FORBID_STATUS_RESPONSE = 200050;

    public static final int MSG_GET_AIRCRAFT_FIRM_VERSION_RESPONSE = 200051;

    public static final int MSG_GET_DIAGNOSTIS_RESPONSE = 200052;

    public static final int MSG_FORMAT_SDCARD_RESPONSE = 200053;

    public static final int MSG_GET_PHOTO_RATIO_RESPONSE = 2000054;
    public static final int MSG_SET_PHOTO_RATIO_RESPONSE = 2000055;

    public static final int MSG_GET_PHOTO_FORMAT_RESPONSE = 2000056;
    public static final int MSG_SET_PHOTO_FORMAT_RESPONSE = 2000057;

    public static final int MSG_GET_PHOTO_WB_AND_CT_RESPONSE = 2000058;
    public static final int MSG_SET_PHOTO_WB_AND_CT_RESPONSE = 2000059;

    public static final int MSG_GET_VIDEO_R_AND_FR_RESPONSE = 2000060;
    public static final int MSG_SET_VIDEO_R_AND_FR_RESPONSE = 2000061;

    public static final int MSG_GET_VIDEO_FORMAT_RESPONSE = 2000062;
    public static final int MSG_SET_VIDEO_FORMAT_RESPONSE = 2000063;

    public static final int MSG_GET_VIDEO_STANDARD_RESPONSE = 2000064;
    public static final int MSG_SET_VIDEO_STANDARD_RESPONSE = 2000065;

    public static final int MSG_GET_CAMERA_STATUS_RESPONSE = 2000066;
}
