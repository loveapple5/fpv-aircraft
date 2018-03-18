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
import android.widget.Toast;

import com.synseaero.dji.DJIService;
import com.synseaero.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.feng.skin.manager.config.SkinConfig;
import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;


public class FPVApplication extends Application {

    private Messenger mDJIServiceMessenger = null;

    public static final String DJI_SERVICE_CONNECTED = "DJI_SERVICE_CONNECTED";

    private static final String SKIN_DIR = "skin";

    public static final String ACTION_APP_SKIN_CHANGED = "ACTION_APP_SKIN_CHANGED";

    @Override
    public void onCreate() {
        super.onCreate();

        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();

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

        new Thread() {
            public void run() {
                File cacheDir = getExternalCacheDir();
                if (cacheDir != null) {
                    //skin目录下的皮肤文件拷贝到外部缓存目录
                    FileUtils.copyFiles(FPVApplication.this, SKIN_DIR, cacheDir.getAbsolutePath() + File.separator + SKIN_DIR);
                }
            }
        }.start();
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

    public void changeSkin(final int styleId) {
        if (styleId == 1) {
            SkinManager.getInstance().restoreDefaultTheme();

            Intent intent = new Intent();
            intent.setAction(ACTION_APP_SKIN_CHANGED);
            intent.putExtra("style", styleId);
            sendBroadcast(intent);

        } else if(styleId > 1){
            File cacheDir = getExternalCacheDir();
            if (cacheDir != null) {
                File skin = new File(cacheDir.getAbsolutePath() + File.separator + SKIN_DIR + File.separator + "skin" + styleId);

                if (!skin.exists()) {
                    Toast.makeText(getApplicationContext(), "请检查" + SKIN_DIR + styleId + "是否存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                SkinManager.getInstance().load(skin.getAbsolutePath(),
                        new ILoaderListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.setAction(ACTION_APP_SKIN_CHANGED);
                                intent.putExtra("style", styleId);
                                sendBroadcast(intent);
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(getApplicationContext(), "切换失败", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
    }

    public int getSkinStyle() {
        if(SkinConfig.isDefaultSkin(this)) {
            return 1;
        }
        String skinPath = SkinConfig.getCustomSkinPath(this);
        File skinFile = new File(skinPath);
        String fileName = skinFile.getName();
        int style = Integer.valueOf(fileName.substring(4));
        return style;
    }

}
