package com.dji.FPVDemo.model;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

public class VolumeMenuItem extends MenuItem{

    private Activity activity;

    public VolumeMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        super(key, type, curValue, values, subMenu);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void up() {
        super.up();
        submitCurValue();
    }

    public void down() {
        super.down();
        submitCurValue();
    }

    public void fetchCurValue() {
        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        //屏幕亮度返回0-1之间的小数
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        float percent = (float)current / max * 100;
        curValue = String.valueOf((int)percent);
    }

    public void submitCurValue() {
        float ratio = Float.valueOf(curValue) / 100;
        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = (int)(max * ratio);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, AudioManager.FLAG_SHOW_UI);
    }
}
