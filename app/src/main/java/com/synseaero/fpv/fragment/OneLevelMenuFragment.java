package com.synseaero.fpv.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.synseaero.fpv.R;
import com.synseaero.fpv.model.MenuItem;

public class OneLevelMenuFragment extends MenuFragment {

    private LinearLayout llSubMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_one_level, container, false);

        llSubMenu = (LinearLayout) root.findViewById(R.id.ll_sub_menu);
        MenuItem data = menuData.items.get(0);
        View subMenu = createMenuView(data);
        //subMenu.setVisibility(View.GONE);
        llSubMenu.addView(subMenu);
        return root;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onConfirmPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        //提交当前值
        View subMenu = llSubMenu.getChildAt(0);
        MenuItem data = (MenuItem) subMenu.getTag();
        data.submitCurValue();
    }

    @Override
    public void onUpPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        View subMenu = llSubMenu.getChildAt(0);
        MenuItem menuData = ((MenuItem) subMenu.getTag());
        menuData.up();
        refreshSubMenu(subMenu);
    }

    @Override
    public void onDownPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        View subMenu = llSubMenu.getChildAt(0);
        MenuItem menuData = ((MenuItem) subMenu.getTag());
        menuData.down();
        refreshSubMenu(subMenu);
    }
}
