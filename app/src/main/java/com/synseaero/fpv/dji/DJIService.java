package com.synseaero.fpv.dji;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

/***
 * 用户管理和大疆sdk的所有交互，屏蔽所有关于大疆sdk内部的细节与app进行交互
 */
public class DJIService extends Service {

    private static final String TAG = DJIService.class.getName();

    private HandlerThread mDJIThread = new HandlerThread("DJI_THREAD");

    private Handler mDJIHandler = null;

    private Messenger mDJIServiceMessenger = null;

    private HashMap<Integer, HashSet<Messenger>> mClientMessageMessengers = new HashMap<>();

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MessageType.MSG_REGISTER_SDK) {
                DJISDKManager.getInstance().initSDKManager(DJIService.this, mDJISDKManagerCallback);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDJIServiceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDJIThread.start();
        mDJIHandler = new Handler(mDJIThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //Toast.makeText(DJIService.this, "msg:" + msg.what, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "msg:" + msg.what);
                //客户端注册信使
                if (msg.what == MessageType.MSG_REGISTER_MESSENGER) {
                    Messenger clientMessenger = msg.replyTo;
                    Bundle bundle = msg.getData();
                    //如果没有registerMessageId，则注册到0号消息，0号消息是全量消息，接收所有的toClient消息
                    int messageId = bundle.getInt("registerMessageId", 0);
                    HashSet<Messenger> messengers = mClientMessageMessengers.get(messageId);
                    if (messengers == null) {
                        messengers = new HashSet<>();
                        messengers.add(clientMessenger);
                        mClientMessageMessengers.put(messageId, messengers);
                    } else {
                        messengers.add(clientMessenger);
                    }
                }
                //客户端移除信使
                else if (msg.what == MessageType.MSG_UNREGISTER_MESSENGER) {

                    Messenger clientMessenger = msg.replyTo;
                    Bundle bundle = msg.getData();
                    int messageId = bundle.getInt("unregisterMessageId", 0);
                    HashSet<Messenger> messengers = mClientMessageMessengers.get(messageId);
                    if (messengers != null) {
                        messengers.remove(clientMessenger);
                    }

                }
                //注册大疆sdk
                else if (msg.what == MessageType.MSG_REGISTER_SDK) {
                    mMainHandler.sendEmptyMessage(MessageType.MSG_REGISTER_SDK);
                }
                //请求大疆sdk
                else if (msg.what > MessageType.MSG_DJI_REQUEST_BASE && msg.what <= MessageType.MSG_DJI_RESPONSE_BASE) {
                    try {
                        //创建对应的任务
                        Task task = TaskFactory.createTask(msg.what, msg.getData(), mDJIServiceMessenger);
                        //运行任务
                        //同步任务
                        if (task != null) {
                            task.run();
                        }
                        //TODO:可以使用线程池支持异步任务
                    } catch (Exception e) {

                    }

                }
                //返回大疆sdk
                else if (msg.what > MessageType.MSG_DJI_RESPONSE_BASE && msg.what <= 300000) {
                    HashSet<Messenger> messengers = mClientMessageMessengers.get(msg.what);
                    if (messengers != null) {
                        for (Messenger clientMessenger : messengers) {
                            try {
                                Message message = Message.obtain();
                                message.copyFrom(msg);
                                clientMessenger.send(message);
                            } catch (RemoteException e) {
                                if(e.getLocalizedMessage() != null) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                }
            }
        };
        mDJIServiceMessenger = new Messenger(mDJIHandler);
    }

    private DJISDKManager.DJISDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.DJISDKManagerCallback() {

        //Listens to the SDK registration result
        @Override
        public void onGetRegisteredResult(DJIError error) {

            boolean registerResult = (error == DJISDKError.REGISTRATION_SUCCESS);
            if (registerResult) {
                DJISDKManager.getInstance().startConnectionToProduct();
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean("registerResult", registerResult);
            bundle.putString("DJI_DESC", error.getDescription());
            Message msg = Message.obtain();
            msg.what = MessageType.MSG_REGISTER_SDK_RESULT;
            msg.setData(bundle);
            mDJIHandler.sendMessage(msg);
        }

        //Listens to the connected product changing, including two parts, component changing or product connection changing.
        @Override
        public void onProductChanged(DJIBaseProduct oldProduct, DJIBaseProduct newProduct) {

            if (oldProduct != null) {
                Log.d(TAG,"oldProduct");
                oldProduct.destroy();
                oldProduct.setDJIBaseProductListener(null);
            }

            if (newProduct != null) {
                Log.d(TAG,"newProduct");
                newProduct.setDJIBaseProductListener(mDJIBaseProductListener);
            }
            Bundle bundle = new Bundle();
            boolean hasProduct = (newProduct != null && newProduct.getModel() != null);
            bundle.putBoolean("hasProduct", hasProduct);
            if (hasProduct) {
                bundle.putString("productName", newProduct.getModel().getDisplayName());
            }
            Message msg = Message.obtain();
            msg.what = MessageType.MSG_PRODUCT_CHANGED;
            msg.setData(bundle);
            mDJIHandler.sendMessage(msg);
        }
    };

    private DJIBaseProduct.DJIBaseProductListener mDJIBaseProductListener = new DJIBaseProduct.DJIBaseProductListener() {

        @Override
        public void onComponentChange(DJIBaseProduct.DJIComponentKey key, DJIBaseComponent oldComponent, DJIBaseComponent newComponent) {

            if (newComponent != null) {
                newComponent.setDJIComponentListener(mDJIComponentListener);
            }

            Bundle bundle = new Bundle();
            Message msg = Message.obtain();
            msg.what = MessageType.MSG_COMPONENT_CHANGED;
            msg.setData(bundle);
            mDJIHandler.sendMessage(msg);
        }

        @Override
        public void onProductConnectivityChanged(boolean isConnected) {

            if (!isConnected) {
                DJISDKManager.getInstance().startConnectionToProduct();
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean("isConnected", isConnected);
            if (isConnected) {
                DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
                if (product != null && product.getModel() != null) {
                    bundle.putString("productName", product.getModel().getDisplayName());
                }
            }
            Message msg = Message.obtain();
            msg.what = MessageType.MSG_PRODUCT_CONNECTIVITY_CHANGED;
            msg.setData(bundle);
            mDJIHandler.sendMessage(msg);

        }

    };

    private DJIBaseComponent.DJIComponentListener mDJIComponentListener = new DJIBaseComponent.DJIComponentListener() {

        @Override
        public void onComponentConnectivityChanged(boolean isConnected) {

            Bundle bundle = new Bundle();
            Message msg = Message.obtain();
            msg.what = MessageType.MSG_COMPONENT_CONNECTIVITY_CHANGED;
            msg.setData(bundle);
            mDJIHandler.sendMessage(msg);
        }

    };

    public void onDestroy() {
        super.onDestroy();
    }

}
