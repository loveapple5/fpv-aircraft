package com.dji.FPVDemo;


import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.multidex.MultiDex;

import com.dji.FPVDemo.dji.DJIService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FPVApplication extends Application {

    private Messenger mDJIServiceMessenger = null;

    public static final String DJI_SERVICE_CONNECTED = "DJI_SERVICE_CONNECTED";

    @Override
    public void onCreate() {
        super.onCreate();
        Intent dJIIntent = new Intent(this, DJIService.class);
        bindService(dJIIntent, dJIConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection dJIConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDJIServiceMessenger = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDJIServiceMessenger = new Messenger(service);
            Intent intent = new Intent(DJI_SERVICE_CONNECTED);
            sendBroadcast(intent);
        }
    };

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void onTerminate() {
        super.onTerminate();
        unbindService(dJIConnection);
    }

    public boolean sendDJIMessage(Message message) {
        try {
            mDJIServiceMessenger.send(message);
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
