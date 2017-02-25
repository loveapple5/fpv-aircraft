package com.dji.FPVDemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;
import com.dji.FPVDemo.StatusListActivity;

import java.util.ArrayList;

import dji.common.error.DJIError;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

/**
 * Created by ZXW on 2017/2/25.
 */

public class RemoteControllerSettingFragment extends Fragment {

    protected static final int GET_RC_MODE = 2;

    private DJIAircraft djiAircraft;
    private DJIRemoteController djiRemoteController;
    private DJIRCControlMode djircControlMode;

    private Spinner spRCMode;
    private ArrayList RCModeList;

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_RC_MODE:
                    String RCMode = msg.getData().getString("RCMode");
                    int position = RCModeList.indexOf(RCMode);
                    spRCMode.setSelection(position);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_remote_controller_setting, container, false);
        InitUI(view);
        InitDJI();
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void InitUI(View view){
        spRCMode = (Spinner)view.findViewById(R.id.sp_RemoteControllerMode);
        RCModeList = new ArrayList<String>();
        RCModeList.add("日本手");
        RCModeList.add("美国手");
        RCModeList.add("中国手");
        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, RCModeList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spRCMode.setAdapter(RCModeAdapter);
        spRCMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                DJIRCControlStyle style = DJIRCControlStyle.find(position + 1);
                DJIRCControlMode mode = new DJIRCControlMode();
                mode.controlStyle = style;
                djiRemoteController.setRCControlMode(mode, new SetRCControlModeCallback());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private void InitDJI(){

        //----------------------------B设置菜单--------------------------------
        //--------------------------B3遥控器设置--------------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiRemoteController = djiAircraft.getRemoteController();
        //遥控器校准校准什么？
//        djiRemoteController.getRCWheelControlGimbalSpeed(new GetRCWheelControlGimbalSpeedCallback());//云台滚轮控制速度X
        //摇杆模式
        djiRemoteController.getRCControlMode(new GetRCControlModeCallback());
//        djiRemoteController.setRCControlMode(djircControlMode,new SetRCControlModeCallback());//设置参数DJIRCControlStyle
    }

    class GetRCControlModeCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode> {

        @Override
        public void onSuccess(DJIRCControlMode djircControlMode) {
            int mode = djircControlMode.controlStyle.value();
            StringBuilder sb = new StringBuilder();
            if (mode == DJIRCControlStyle.Japanese.value()) {
                sb.append("日本手");
            } else if (mode == DJIRCControlStyle.Chinese.value()) {
                sb.append("中国手");
            } else if (mode == DJIRCControlStyle.American.value()) {
                sb.append("美国手");
            } 

                    Bundle bundle = new Bundle();
                    bundle.putString("RCMode", sb.toString());
                    Message msg = Message.obtain();
                    msg.what = GET_RC_MODE;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }
    class SetRCControlModeCallback implements DJICommonCallbacks.DJICompletionCallback {
        @Override
        public void onResult(DJIError djiError) {
            if(djiError == null) {
                Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "设置失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    class GetRCWheelControlGimbalSpeedCallback implements DJICompletionCallbackWith<>{}
}

