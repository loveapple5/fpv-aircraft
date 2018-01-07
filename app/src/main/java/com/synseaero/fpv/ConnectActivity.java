package com.synseaero.fpv;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.WaveView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;
import cn.feng.skin.manager.util.L;

public class ConnectActivity extends DJIActivity implements View.OnClickListener {

    private static final String TAG = ConnectActivity.class.getName();

    private WaveView wcConnect;
    private ImageView ivLauncher;
    private View btnSetting;
    private View btnPrepareFlight;
    private TextView tvModel;

    private static final String SKIN_NAME = "blackfantacy";
    private static final String SKIN_DIR = Environment
            .getExternalStorageDirectory() + File.separator + SKIN_NAME;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//            int what = msg.what;
//            //识别出当前产品
//            if (what == MessageType.MSG_PRODUCT_CHANGED) {
//                Bundle bundle = msg.getData();
//                boolean hasProduct = bundle.getBoolean("hasProduct", false);
//                String productName = bundle.getString("productName", "");
//                if (!hasProduct) {
//                    btnSetting.setEnabled(false);
//                    btnPrepareFlight.setEnabled(false);
//                    tvModel.setText(R.string.connecting);
//                } else {
//                    btnSetting.setEnabled(true);
//                    btnPrepareFlight.setEnabled(true);
//                    tvModel.setText(productName);
//                }
//            }
//            //产品连接状态改变
//            else if (what == MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED) {
//                Bundle bundle = msg.getData();
//                String productName = bundle.getString("productName", "");
//                boolean isConnected = bundle.getBoolean("isConnected");
//                if (!isConnected) {
//                    btnSetting.setEnabled(false);
//                    btnPrepareFlight.setEnabled(false);
//                    tvModel.setText(R.string.connecting);
//                } else {
//                    btnSetting.setEnabled(true);
//                    btnPrepareFlight.setEnabled(true);
//                    tvModel.setText(productName);
//                }
//            }

        }
    };

    private Messenger messenger = new Messenger(handler);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_connect);
        ivLauncher = (ImageView) findViewById(R.id.iv_launcher);
        wcConnect = (WaveView) findViewById(R.id.wv_connect);
        wcConnect.setInitialRadius(150);
        wcConnect.setMaxRadiusRate(1);
        wcConnect.setDuration(5000);
        wcConnect.setStyle(Paint.Style.FILL);
        wcConnect.setColor(getResources().getColor(R.color.blue));
        wcConnect.start();

        btnSetting = findViewById(R.id.tv_setting);
        btnSetting.setOnClickListener(this);

        btnPrepareFlight = findViewById(R.id.tv_prepare_flight);
        btnPrepareFlight.setOnClickListener(this);

        tvModel = (TextView) findViewById(R.id.tv_craft_model);

        handler.postDelayed(hideLauncher, 3000);

        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVApplication.DJI_SERVICE_CONNECTED);
        registerReceiver(mReceiver, filter);
        //((FPVDemoApplication) getApplication()).notifyStatusChange();

        registerDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
        registerDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);

    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //onCreate的时候注册有可能失败
            registerDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
            registerDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);

            Message message = Message.obtain();
            message.what = MessageType.MSG_REGISTER_SDK;
            sendDJIMessage(message);

        }
    };

    private Runnable hideLauncher = new Runnable() {
        @Override
        public void run() {
            ivLauncher.setImageDrawable(null);
            ivLauncher.setVisibility(View.GONE);
        }
    };

    private int i = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);

                break;
            case R.id.tv_prepare_flight:
//                Intent fpvIntent = new Intent(this, BluetoothActivity.class);
//                startActivity(fpvIntent);

//                Intent fpvIntent = new Intent(this, FPVActivity.class);
//                startActivity(fpvIntent);

                Intent prepareIntent = new Intent(this, PrepareActivity.class);
                startActivity(prepareIntent);
//                if(i++ % 2 == 0) {
//                    onSkinResetClick();
//                } else {
//                    onSkinSetClick();
//                }
                break;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterDJIMessenger(MessageType.MSG_PRODUCT_CHANGED, messenger);
        unregisterDJIMessenger(MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED, messenger);
    }

//    protected void onSkinResetClick() {
//        SkinManager.getInstance().restoreDefaultTheme();
//    }
//
//    private void onSkinSetClick() {
////        if(!isOfficalSelected) return;
//
//        File cacheDir = getExternalCacheDir();
//        copyFiles(this, "skin", cacheDir.getAbsolutePath());
//
//        File skin = new File(cacheDir.getAbsolutePath() + File.separator + SKIN_NAME);
//
//
//        if(!skin.exists()){
//            Toast.makeText(getApplicationContext(), "请检查" + SKIN_DIR + "是否存在", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        SkinManager.getInstance().load(skin.getAbsolutePath(),
//                new ILoaderListener() {
//                    @Override
//                    public void onStart() {
//                        L.e("startloadSkin");
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        L.e("loadSkinSuccess");
//                        Toast.makeText(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT).show();
////                        setNightSkinBtn.setText("黑色幻想(当前)");
////                        setOfficalSkinBtn.setText("官方默认");
////                        isOfficalSelected = false;
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        L.e("loadSkinFail");
//                        Toast.makeText(getApplicationContext(), "切换失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    /**
//     * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
//     *
//     * @param context
//     *            上下文
//     * @param inPath
//     *            文件目录，要拷贝的目录
//     * @param outPath
//     *            目标文件夹位置如：/sdcrad/mydir
//     */
//    public static boolean copyFiles(Context context, String inPath, String outPath) {
//        Log.i(TAG, "copyFiles() inPath:" + inPath + ", outPath:" + outPath);
//        String[] fileNames = null;
//        try {// 获得Assets一共有几多文件
//            fileNames = context.getAssets().list(inPath);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//            return false;
//        }
//        if (fileNames.length > 0) {//如果是目录
//            File fileOutDir = new File(outPath);
//            if(fileOutDir.isFile()){
//                boolean ret = fileOutDir.delete();
//                if(!ret){
//                    Log.e(TAG, "delete() FAIL:" + fileOutDir.getAbsolutePath());
//                }
//            }
//            if (!fileOutDir.exists()) { // 如果文件路径不存在
//                if (!fileOutDir.mkdirs()) { // 创建文件夹
//                    Log.e(TAG, "mkdirs() FAIL:" + fileOutDir.getAbsolutePath());
//                    return false;
//                }
//            }
//            for (String fileName : fileNames) { //递归调用复制文件夹
//                String inDir = inPath;
//                String outDir = outPath + File.separator;
//                if(!inPath.equals("")) { //空目录特殊处理下
//                    inDir = inDir + File.separator;
//                }
//                copyFiles(context,inDir + fileName, outDir + fileName);
//            }
//            return true;
//        } else {//如果是文件
//            try {
//                File fileOut = new File(outPath);
//                if(fileOut.exists()) {
//                    boolean ret = fileOut.delete();
//                    if(!ret){
//                        Log.e(TAG, "delete() FAIL:" + fileOut.getAbsolutePath());
//                    }
//                }
//                boolean ret = fileOut.createNewFile();
//                if(!ret){
//                    Log.e(TAG, "createNewFile() FAIL:" + fileOut.getAbsolutePath());
//                }
//                FileOutputStream fos = new FileOutputStream(fileOut);
//                InputStream is = context.getAssets().open(inPath);
//                byte[] buffer = new byte[1024];
//                int byteCount=0;
//                while((byteCount = is.read(buffer)) > 0) {
//                    fos.write(buffer, 0, byteCount);
//                }
//                fos.flush();//刷新缓冲区
//                is.close();
//                fos.close();
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//    }

}
