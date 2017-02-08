package com.dji.FPVDemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import dji.common.remotecontroller.DJIRCHardwareState;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

/**
 * Created by LEX on 2016/11/2.
 */
public class RemoteControl {

//
//    protected DJIRemoteController remoteController = null;
//    final public static int MSG_REMOTE_CONTROLLER_CURRENT_STATE =1;
//    final public static int MSG_REMOTE_CONTROLLER_ERROR =3;
//    public static Boolean switchView = false;
//
//    protected void initRemoteController(){
//        try {
//            DJIAircraft djiAircraft = (DJIAircraft)FPVDemoApplication.getProductInstance();
//            if(djiAircraft!=null){
//                remoteController = djiAircraft.getRemoteController();
////                remoteController.getBatteryStateUpdateCallback().onBatteryStateUpdate();
//                remoteController.setHardwareStateUpdateCallback(
//                        new DJIRemoteController.RCHardwareStateUpdateCallback() {
//                            @Override
//                            public void onHardwareStateUpdate(DJIRemoteController djiRemoteController, DJIRCHardwareState djircHardwareState) {
//                                Boolean CusButton1 = djircHardwareState.customButton1.buttonDown;
//                                Boolean CusButton2 = djircHardwareState.customButton2.buttonDown;
//                                Message msg0 = new Message();
//                                Bundle bundle0 = new Bundle();
//                                bundle0.putBoolean ("CusButton1", CusButton1);
//                                bundle0.putBoolean ("CusButton2", CusButton2);
//                                msg0.what = MSG_REMOTE_CONTROLLER_CURRENT_STATE;
//                                msg0.setData(bundle0);
//                                remoteControllHandler.sendMessage(msg0);
//                            }
//
//                        }
//                );
//            }
//
//        }catch (Exception e){
//            Message msg0 = new Message();
//            msg0.what = MSG_REMOTE_CONTROLLER_ERROR;
//            remoteControllHandler.sendMessage(msg0);
//
//        }
//
//
//    }

//
//    protected Handler remoteControllHandler    = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg0) {
//            Bundle bundle0=msg0.getData();
//            Boolean CustButton1=false;
//            Boolean CustButton2=false;
//
//            switch (msg0.what){
//                case MSG_REMOTE_CONTROLLER_CURRENT_STATE:
//                    CustButton1 = bundle0.getBoolean("CusButton1");
//                    CustButton2 = bundle0.getBoolean("CusButton2");
//                    break;
//                case MSG_REMOTE_CONTROLLER_ERROR:
////                        showToast(getString(R.string.remotecontrolerror));
//                    break;
//            }
//
//            switchView = CustButton2;
//
//            changeView();
//
////        generate tone for LCD valve
//
//            if (switchView ==true && viewTPV!=true) {
//                mytone.genTone2();
//                mytone.sendSig();
//            } else if (switchView ==true && viewTPV==true){
//                mytone.genTone1();
//                mytone.sendSig();
//            }
//            else{
//                mytone.stopSig();
//            }
//
//            return false;
//        }
//
//        private void changeView() {
//            if (switchView==true) {
//
//                if (viewTPV == true) {
//                    setContentView(R.layout.fpv_view);
//
//                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                    addContentView(mGLView,params);
//
//                    viewTPV = false;
//
////             TODO       reset compass, check if angle is fault
//                    firstCall=true;
//
//                } else {
//
//                    ((ViewGroup)mGLView.getParent()).removeView(mGLView);
//                    setContentView(R.layout.tpv_view);
//                    initPreviewer();
//
//
//
//                    viewTPV = true;
//                }
//                initUI();
//                initCodec();
//            }
//        }
//    });

}
