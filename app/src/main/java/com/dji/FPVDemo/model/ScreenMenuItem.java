package com.dji.FPVDemo.model;


import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class ScreenMenuItem extends MenuItem {

    private Activity activity;

    public ScreenMenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
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
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        //屏幕亮度返回0-1之间的小数
        float progress = params.screenBrightness * 100;
        curValue = String.valueOf((int)progress);
    }

    public void submitCurValue() {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = Float.valueOf(curValue) / 100;
        localWindow.setAttributes(params);
    }


}
