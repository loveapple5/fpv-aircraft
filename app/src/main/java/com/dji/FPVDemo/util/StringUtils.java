package com.dji.FPVDemo.util;


import dji.common.camera.DJICameraSettingsDef;

public class StringUtils {

    public static String getISOString(DJICameraSettingsDef.CameraISO cameraISO) {
        String ISO = "0";

        if (cameraISO == DJICameraSettingsDef.CameraISO.Auto) {
            ISO = "AUTO";
        }else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_100) {
            ISO = "100";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_200) {
            ISO = "200";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_400) {
            ISO = "400";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_800) {
            ISO = "800";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_1600) {
            ISO = "1600";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_3200) {
            ISO = "3200";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_6400) {
            ISO = "6400";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_12800) {
            ISO = "12800";
        } else if (cameraISO == DJICameraSettingsDef.CameraISO.ISO_25600) {
            ISO = "25600";
        }
        return ISO;
    }

    public static String getEVString(DJICameraSettingsDef.CameraExposureCompensation compensation) {
        if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_5_0) {
            return "-0.5";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_4_7) {
            return "-0.47";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_4_3) {
            return "-0.43";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_4_0) {
            return "-0.40";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_3_7) {
            return "-0.37";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_3_3) {
            return "-0.33";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_3_0) {
            return "-0.3";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_2_7) {
            return "-0.27";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_2_3) {
            return "-0.23";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_2_0) {
            return "-0.2";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_1_7) {
            return "-0.17";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_1_3) {
            return "-0.13";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_1_0) {
            return "-0.1";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_0_7) {
            return "-0.07";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_0_3) {
            return "-0.03";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.N_0_0) {
            return "0";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_0_3) {
            return "+0.03";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_0_7) {
            return "+0.07";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_1_0) {
            return "+0.1";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_1_3) {
            return "+0.13";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_1_7) {
            return "+0.17";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_2_0) {
            return "+0.2";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_2_3) {
            return "+0.23";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_2_7) {
            return "+0.27";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_3_0) {
            return "+0.3";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_3_3) {
            return "+0.33";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_3_7) {
            return "+0.37";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_4_0) {
            return "+0.40";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_4_3) {
            return "+0.43";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_4_7) {
            return "+0.47";
        } else if (compensation == DJICameraSettingsDef.CameraExposureCompensation.P_5_0) {
            return "+0.5";
        }

        return "";
    }
}
