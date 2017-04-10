package com.dji.FPVDemo.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.sdk.products.DJIAircraft;


public class TPVFragment extends Fragment {

    private DJIAircraft djiAircraft;

    private int wWidth;
    private int wHeight;

    private RelativeLayout rlTop;
    private RelativeLayout rlLeft;
    private RelativeLayout rlBottom;
    private RelativeLayout rlRight;

    private RelativeLayout rlCraftSignal;
    private RelativeLayout rlControllerSignal;
    private TextView tvSafeInfo;

    private ImageView ivCraftSignal;
    private ImageView ivControllerSignal;

    private ImageView ivHelmetEnergy;
    private ImageView ivPhoneEnergy;
    private ImageView ivControllerEnergy;
    private ImageView ivCraftEnergy;

    private TextView tvFlightHeight;
    private TextView tvFlightDistance;
    private TextView tvFlightSpeed;
    private TextView tvFlightVerticalSpeed;
    private ImageView ivFlightVerticalSpeed;


    private static final int SIGNAL_ICON[] = {
            R.drawable.signal_icon_0,
            R.drawable.signal_icon_1,
            R.drawable.signal_icon_2,
            R.drawable.signal_icon_3,
            R.drawable.signal_icon_4
    };

    private static final int ENERGY_ICON[] = {
            R.drawable.energy_icon_0,
            R.drawable.energy_icon_10,
            R.drawable.energy_icon_20,
            R.drawable.energy_icon_30,
            R.drawable.energy_icon_40,
            R.drawable.energy_icon_50,
            R.drawable.energy_icon_60,
            R.drawable.energy_icon_70,
            R.drawable.energy_icon_80,
            R.drawable.energy_icon_90,
            R.drawable.energy_icon_100,
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();

        WindowManager wm = getActivity().getWindowManager();
        wWidth = wm.getDefaultDisplay().getWidth();
        wHeight = wm.getDefaultDisplay().getHeight();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tpv, container, false);
        rlTop = (RelativeLayout) view.findViewById(R.id.layout_tpv_top);
        rlLeft = (RelativeLayout) view.findViewById(R.id.layout_tpv_left);
        rlBottom = (RelativeLayout) view.findViewById(R.id.layout_tpv_bottom);
        rlRight = (RelativeLayout) view.findViewById(R.id.layout_tpv_right);
        rlCraftSignal = (RelativeLayout) view.findViewById(R.id.layout_craft_signal);
        rlControllerSignal = (RelativeLayout) view.findViewById(R.id.layout_controller_signal);
        tvSafeInfo = (TextView) view.findViewById(R.id.tv_safe_info);
        ivCraftSignal = (ImageView) view.findViewById(R.id.iv_craft_signal);
        ivControllerSignal = (ImageView) view.findViewById(R.id.iv_controller_signal);
        ivHelmetEnergy = (ImageView) view.findViewById(R.id.iv_helmet_energy);
        ivPhoneEnergy = (ImageView) view.findViewById(R.id.iv_phone_energy);
        ivControllerEnergy = (ImageView) view.findViewById(R.id.iv_controller_energy);
        ivCraftEnergy = (ImageView) view.findViewById(R.id.iv_craft_energy);
        tvFlightHeight = (TextView) view.findViewById(R.id.tv_flight_height);
        tvFlightDistance = (TextView) view.findViewById(R.id.tv_flight_distance);
        tvFlightSpeed = (TextView) view.findViewById(R.id.tv_flight_speed);
        tvFlightVerticalSpeed = (TextView) view.findViewById(R.id.tv_flight_vertical_speed);
        ivFlightVerticalSpeed = (ImageView) view.findViewById(R.id.iv_flight_vertical_speed);

        int paddingText = (int) (wWidth * 0.013);
        tvSafeInfo.setPadding(paddingText, 0, paddingText, 0);

        //top
        RelativeLayout.LayoutParams lpTop = (RelativeLayout.LayoutParams) rlTop.getLayoutParams();
        lpTop.width = (int) (wWidth * 0.221);
        lpTop.height = (int) (wHeight * 0.062);
        lpTop.topMargin = (int) (wHeight * 0.07);
        rlTop.setLayoutParams(lpTop);

        RelativeLayout.LayoutParams lpCraftSignal = (RelativeLayout.LayoutParams) rlCraftSignal.getLayoutParams();
        lpCraftSignal.width = (int) (wWidth * 0.058);
        lpCraftSignal.height = (int) (wHeight * 0.0409);
        rlCraftSignal.setLayoutParams(lpCraftSignal);

        RelativeLayout.LayoutParams lpControllerSignal = (RelativeLayout.LayoutParams) rlControllerSignal.getLayoutParams();
        lpControllerSignal.width = (int) (wWidth * 0.058);
        lpControllerSignal.height = (int) (wHeight * 0.0409);
        rlControllerSignal.setLayoutParams(lpControllerSignal);

        RelativeLayout.LayoutParams lpSafeInfo = (RelativeLayout.LayoutParams) tvSafeInfo.getLayoutParams();
        lpSafeInfo.width = (int) (wWidth * 0.126);
        lpSafeInfo.height = (int) (wHeight * 0.0409);
        tvSafeInfo.setLayoutParams(lpSafeInfo);
        tvSafeInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (lpSafeInfo.height * 0.7));

        RelativeLayout.LayoutParams lpIvCraftSignal = (RelativeLayout.LayoutParams) ivCraftSignal.getLayoutParams();
        lpIvCraftSignal.width = (int) (wWidth * 0.0147);
        lpIvCraftSignal.height = (int) (wHeight * 0.0195);
        lpIvCraftSignal.leftMargin = lpCraftSignal.width / 2;
        ivCraftSignal.setLayoutParams(lpIvCraftSignal);

        RelativeLayout.LayoutParams lpIvControllerSignal = (RelativeLayout.LayoutParams) ivControllerSignal.getLayoutParams();
        lpIvControllerSignal.width = (int) (wWidth * 0.0147);
        lpIvControllerSignal.height = (int) (wHeight * 0.0195);
        lpIvControllerSignal.leftMargin = lpControllerSignal.width / 2;
        ivControllerSignal.setLayoutParams(lpIvCraftSignal);

        //left
        RelativeLayout.LayoutParams lpLeft = (RelativeLayout.LayoutParams) rlLeft.getLayoutParams();
        lpLeft.width = (int) (wWidth * 0.15);
        lpLeft.height = (int) (wHeight * 0.52);
        lpLeft.leftMargin = (int) (wWidth * 0.06);
        rlLeft.setLayoutParams(lpLeft);

        //bottom
        RelativeLayout.LayoutParams lpBottom = (RelativeLayout.LayoutParams) rlBottom.getLayoutParams();
        lpBottom.width = (int) (wWidth * 0.390);
        lpBottom.height = (int) (wHeight * 0.107);
        lpBottom.bottomMargin = (int) (wHeight * 0.045);
        rlBottom.setLayoutParams(lpBottom);

        RelativeLayout.LayoutParams lpHelmetEnergy = (RelativeLayout.LayoutParams) ivHelmetEnergy.getLayoutParams();
        lpHelmetEnergy.width = (int) (wWidth * 0.032);
        lpHelmetEnergy.height = (int) (wHeight * 0.048);
        lpHelmetEnergy.leftMargin = (int)(wWidth * 0.039);
        lpHelmetEnergy.topMargin = (int)(wWidth * 0.015);
        ivHelmetEnergy.setLayoutParams(lpHelmetEnergy);

        RelativeLayout.LayoutParams lpPhoneEnergy = (RelativeLayout.LayoutParams) ivPhoneEnergy.getLayoutParams();
        lpPhoneEnergy.width = (int) (wWidth * 0.03);
        lpPhoneEnergy.height = (int) (wHeight * 0.045);
        lpPhoneEnergy.leftMargin = (int)(wWidth * 0.0365);
        lpPhoneEnergy.topMargin = (int)(wWidth * 0.015);
        ivPhoneEnergy.setLayoutParams(lpPhoneEnergy);

        RelativeLayout.LayoutParams lpControllerEnergy = (RelativeLayout.LayoutParams) ivControllerEnergy.getLayoutParams();
        lpControllerEnergy.width = (int) (wWidth * 0.032);
        lpControllerEnergy.height = (int) (wHeight * 0.048);
        lpControllerEnergy.rightMargin = (int)(wWidth * 0.036);
        lpControllerEnergy.topMargin = (int)(wWidth * 0.015);
        ivControllerEnergy.setLayoutParams(lpControllerEnergy);

        RelativeLayout.LayoutParams lpCraftEnergy = (RelativeLayout.LayoutParams) ivCraftEnergy.getLayoutParams();
        lpCraftEnergy.width = (int) (wWidth * 0.032);
        lpCraftEnergy.height = (int) (wHeight * 0.048);
        lpCraftEnergy.rightMargin = (int)(wWidth * 0.037);
        lpCraftEnergy.topMargin = (int)(wWidth * 0.015);
        ivCraftEnergy.setLayoutParams(lpCraftEnergy);

        //right
        RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) rlRight.getLayoutParams();
        lpRight.width = (int) (wWidth * 0.15);
        lpRight.height = (int) (wHeight * 0.52);
        lpRight.rightMargin = (int) (wWidth * 0.06);
        rlRight.setLayoutParams(lpRight);

        RelativeLayout.LayoutParams lpFlightHeight = (RelativeLayout.LayoutParams) tvFlightHeight.getLayoutParams();
        lpFlightHeight.topMargin = (int)(wHeight * 0.03);
        lpFlightHeight.leftMargin = (int)(wWidth * 0.071);
        tvFlightHeight.setLayoutParams(lpFlightHeight);
        tvFlightHeight.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(wHeight * 0.07));
        tvFlightHeight.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightHeight.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightDistance = (RelativeLayout.LayoutParams) tvFlightDistance.getLayoutParams();
        lpFlightDistance.topMargin = (int)(wHeight * 0.16);
        lpFlightDistance.leftMargin = (int)(wWidth * 0.093);
        tvFlightDistance.setLayoutParams(lpFlightDistance);
        tvFlightDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(wHeight * 0.07));
        tvFlightDistance.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightDistance.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightSpeed = (RelativeLayout.LayoutParams) tvFlightSpeed.getLayoutParams();
        lpFlightSpeed.topMargin = (int)(wHeight * 0.4);
        lpFlightSpeed.leftMargin = (int)(wWidth * 0.071);
        tvFlightSpeed.setLayoutParams(lpFlightSpeed);
        tvFlightSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(wHeight * 0.07));
        tvFlightSpeed.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightSpeed.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpFlightVerticalSpeed = (RelativeLayout.LayoutParams) tvFlightVerticalSpeed.getLayoutParams();
        lpFlightVerticalSpeed.topMargin = (int)(wHeight * 0.475);
        lpFlightVerticalSpeed.leftMargin = (int)(wWidth * 0.12);
        tvFlightVerticalSpeed.setLayoutParams(lpFlightVerticalSpeed);
        tvFlightVerticalSpeed.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(wHeight * 0.035));
        tvFlightVerticalSpeed.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
        tvFlightVerticalSpeed.getPaint().setFakeBoldText(true);//加粗

        RelativeLayout.LayoutParams lpIvFlightVerticalSpeed = (RelativeLayout.LayoutParams) ivFlightVerticalSpeed.getLayoutParams();
        lpIvFlightVerticalSpeed.width = (int)(wWidth * 0.01);
        lpIvFlightVerticalSpeed.height = (int)(wHeight * 0.0244);
        lpIvFlightVerticalSpeed.topMargin = (int)(wHeight * 0.488);
        lpIvFlightVerticalSpeed.leftMargin = (int)(wWidth * 0.11);
        ivFlightVerticalSpeed.setLayoutParams(lpIvFlightVerticalSpeed);

        return view;
    }


}
