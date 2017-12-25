package com.synseaero.fpv;


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

import com.synseaero.fpv.dji.DJIService;

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

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                StringBuilder sb = new StringBuilder();
                try {
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());
                    sb.append("\r\n" + date + "\n");

                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    ex.printStackTrace(printWriter);
                    Throwable cause = ex.getCause();
                    while (cause != null) {
                        cause.printStackTrace(printWriter);
                        cause = cause.getCause();
                    }
                    printWriter.flush();
                    printWriter.close();
                    String result = writer.toString();
                    sb.append(result);


                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String time = formatter.format(new Date());
                    String fileName = "crash-" + time + ".log";
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash" + File.separator;
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(path + fileName, true);
                    fos.write(sb.toString().getBytes());
                    fos.flush();
                    fos.close();

                } catch (Exception e) {

                }
            }
        });
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
