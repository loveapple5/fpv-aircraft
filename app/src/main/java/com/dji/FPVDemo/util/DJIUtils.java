package com.dji.FPVDemo.util;

import com.dji.FPVDemo.R;

import java.util.HashMap;

import dji.common.camera.DJICameraSettingsDef;

public class DJIUtils {

    public static HashMap photoResolutionMap = new HashMap();

    public static HashMap photoFormatMap = new HashMap();

    public static HashMap whiteBalanceMap = new HashMap();

    public static HashMap videoResolutionMap = new HashMap();

    public static HashMap frameRateMap = new HashMap();

    public static HashMap videoFormatMap = new HashMap();

    public static HashMap videoStandardMap = new HashMap();

    static {
        photoResolutionMap.put(DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_4_3, R.string.ratio43);
        photoResolutionMap.put(DJICameraSettingsDef.CameraPhotoAspectRatio.AspectRatio_16_9, R.string.ratio169);

        photoFormatMap.put(DJICameraSettingsDef.CameraPhotoFileFormat.JPEG, R.string.jpeg);
        photoFormatMap.put(DJICameraSettingsDef.CameraPhotoFileFormat.RAW, R.string.raw);

        whiteBalanceMap.put(DJICameraSettingsDef.CameraWhiteBalance.Auto, R.string.auto);
        whiteBalanceMap.put(DJICameraSettingsDef.CameraWhiteBalance.Sunny, R.string.sunny);
        whiteBalanceMap.put(DJICameraSettingsDef.CameraWhiteBalance.Cloudy, R.string.close);

        videoResolutionMap.put(DJICameraSettingsDef.CameraVideoResolution.Resolution_4096x2160, R.string.resolution_4k);
        videoResolutionMap.put(DJICameraSettingsDef.CameraVideoResolution.Resolution_1920x1080, R.string.resolution_1080p);
        videoResolutionMap.put(DJICameraSettingsDef.CameraVideoResolution.Resolution_1280x720, R.string.resolution_720p);

        frameRateMap.put(DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_24FPS, R.string.frame_rate_24);
        frameRateMap.put(DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_30FPS, R.string.frame_rate_30);
        frameRateMap.put(DJICameraSettingsDef.CameraVideoFrameRate.FrameRate_60FPS, R.string.frame_rate_60);

        videoFormatMap.put(DJICameraSettingsDef.CameraVideoFileFormat.MOV, R.string.mov);
        videoFormatMap.put(DJICameraSettingsDef.CameraVideoFileFormat.MP4, R.string.mp4);

        videoStandardMap.put(DJICameraSettingsDef.CameraVideoStandard.PAL, R.string.pal);
        videoStandardMap.put(DJICameraSettingsDef.CameraVideoStandard.NTSC, R.string.ntsc);
    }

    public static int getMapValue(HashMap map, Object key) {
        Object value = map.get(key);
        if (value != null && value instanceof Integer) {
            return (int) map.get(key);
        } else {
            return -1;
        }
    }

    public static Object getMapKey(HashMap map, Object value) {
        Object key = null;
        for (Object getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                key = getKey;
            }
        }
        return key;
    }
}
