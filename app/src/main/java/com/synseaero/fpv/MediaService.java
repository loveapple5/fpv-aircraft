package com.synseaero.fpv;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;


public class MediaService extends Service {

    private MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resId = intent.getIntExtra("source", -1);
        if (resId != -1) {
            if(mp != null) {
                mp.release();
            }
            mp = MediaPlayer.create(this, resId);
            mp.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
