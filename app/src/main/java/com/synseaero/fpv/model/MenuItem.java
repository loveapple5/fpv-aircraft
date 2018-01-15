package com.synseaero.fpv.model;

public class MenuItem {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_SELECT = 2;
    public static final int TYPE_PROGRESS = 3;

    //唯一值，用于查找
    public int key;
    //0:文字类型 1:开关类型 2:选择类型
    public int type;

    public String curValue;

    public String[] values;

    public MenuItem subMenu;

    protected FetchCallback fetchCallback;

    public MenuItem(int key, int type, String curValue, String[] values, MenuItem subMenu) {
        this.key = key;
        this.type = type;
        this.curValue = curValue;
        this.values = values;
        this.subMenu = subMenu;
    }

    public void up() {
        switch (type) {
            case TYPE_TEXT:
                try {
                    int progress = Integer.valueOf(curValue);
                    progress++;
                    curValue = String.valueOf(progress);
                } catch (Exception e) {

                }
                break;
            case TYPE_SWITCH:
                if ("true".equalsIgnoreCase(curValue)) {
                    curValue = "false";
                } else {
                    curValue = "true";
                }
                break;
            case TYPE_SELECT:
                int index = -1;
                for (int i = 0; i < values.length; i++) {
                    if (curValue.equals(values[i])) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    index--;
                    if (index >= 0 && index <= values.length - 1) {
                        curValue = values[index];
                    }
                }
                break;
            case TYPE_PROGRESS:
                try {
                    int progress = Integer.valueOf(curValue);
                    progress++;
                    if (progress >= 0 && progress <= 100) {
                        curValue = String.valueOf(progress);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    public void down() {
        switch (type) {
            case TYPE_TEXT:
                try {
                    int progress = Integer.valueOf(curValue);
                    if (progress > 0) {
                        progress--;
                        curValue = String.valueOf(progress);
                    }
                } catch (Exception e) {

                }
                break;
            case TYPE_SWITCH:
                if ("true".equalsIgnoreCase(curValue)) {
                    curValue = "false";
                } else {
                    curValue = "true";
                }
                break;
            case TYPE_SELECT:
                int index = -1;
                for (int i = 0; i < values.length; i++) {
                    if (curValue.equals(values[i])) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    index++;
                    if (index >= 0 && index <= values.length - 1) {
                        curValue = values[index];
                    }
                }
                break;
            case TYPE_PROGRESS:
                try {
                    int progress = Integer.valueOf(curValue);
                    progress--;
                    if (progress >= 0 && progress <= 100) {
                        curValue = String.valueOf(progress);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    public void submitCurValue() {

    }

    public void fetchCurValue() {

    }

    public void setFetchCallback(FetchCallback callback) {
        this.fetchCallback = callback;
    }

    public interface FetchCallback {
        void onFetch(MenuItem menuItem);
    }

}
