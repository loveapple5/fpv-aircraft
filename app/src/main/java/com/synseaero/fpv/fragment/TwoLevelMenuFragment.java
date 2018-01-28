package com.synseaero.fpv.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.synseaero.fpv.R;
import com.synseaero.fpv.model.Menu;
import com.synseaero.fpv.model.MenuItem;
import com.synseaero.util.DensityUtil;
import com.synseaero.view.SectionTextView;
import com.synseaero.view.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import cn.feng.skin.manager.entity.AttrFactory;
import cn.feng.skin.manager.entity.DynamicAttr;
import cn.feng.skin.manager.loader.SkinManager;

public class TwoLevelMenuFragment extends MenuFragment{

    private LinearLayout llMenu;
    private LinearLayout llSubMenu;

    private int mCurrentMenuIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("MenuFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        llMenu = (LinearLayout) view.findViewById(R.id.ll_menu);
        llSubMenu = (LinearLayout) view.findViewById(R.id.ll_sub_menu);

        int size = menuData.items.size();
        for (int i = 0; i < size; i++) {
            MenuItem data = menuData.items.get(i);
            if (data.subMenu != null) {
                View menu = createMenuView(data);
                menu.setOnClickListener(menuClickListener);
                llMenu.addView(menu);
                View subMenu = createMenuView(data.subMenu);
                subMenu.setVisibility(View.GONE);
                llSubMenu.addView(subMenu);
            } else {
                View subMenu = createMenuView(data);
                subMenu.setVisibility(View.GONE);
                llSubMenu.addView(subMenu);
            }
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        int menuCount = llMenu.getChildCount();
        for (int i = 0; i < menuCount; i++) {
            View child = llMenu.getChildAt(i);
            child.setSelected(i == mCurrentMenuIndex);
        }
        int subMenuCount = llSubMenu.getChildCount();
        for (int i = 0; i < subMenuCount; i++) {
            View child = llSubMenu.getChildAt(i);
            child.setVisibility(View.GONE);
        }
    }


    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            //当前菜单就是选中菜单则切换子菜单可见性
            if (v.isSelected()) {
                MenuItem menuData = ((MenuItem) tag);
                View subMenu = llSubMenu.getChildAt(menuData.key);
                if (subMenu.getVisibility() == View.GONE) {
                    subMenu.setVisibility(View.VISIBLE);
                } else {
                    subMenu.setVisibility(View.GONE);
                }
            }
            //当前菜单不是选中菜单且当前菜单未弹出子菜单则选中当前菜单
            else if (llSubMenu.getChildAt(mCurrentMenuIndex).getVisibility() == View.GONE) {
                int menuCount = llMenu.getChildCount();
                for (int i = 0; i < menuCount; i++) {
                    View child = llMenu.getChildAt(i);
                    child.setSelected(child == v);
                    if (child == v) {
                        mCurrentMenuIndex = i;
                    }
                }
            }
        }
    };

    //响应back键
    public boolean onBackPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return false;
        }
        if (llSubMenu.getChildAt(mCurrentMenuIndex).getVisibility() == View.VISIBLE) {
            llSubMenu.getChildAt(mCurrentMenuIndex).setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    //响应confirm键
    public void onConfirmPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        final View subMenu = llSubMenu.getChildAt(mCurrentMenuIndex);
        //没展示二级菜单则展示
        if (subMenu.getVisibility() == View.GONE) {
            MenuItem data = (MenuItem) subMenu.getTag();
            data.setFetchCallback(new MenuItem.FetchCallback() {
                @Override
                public void onFetch(MenuItem menuItem) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshSubMenu(subMenu);
                            subMenu.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
            data.fetchCurValue();

        }
        //展示了二级菜单则提交当前值
        else {
            MenuItem data = (MenuItem) subMenu.getTag();
            data.submitCurValue();
        }
    }

    //响应+键
    public void onUpPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        View subMenu = llSubMenu.getChildAt(mCurrentMenuIndex);
        //没有子菜单则切换母菜单
        if (subMenu.getVisibility() == View.GONE) {
            int upIndex = mCurrentMenuIndex - 1;
            int menuCount = llMenu.getChildCount();
            if (upIndex >= 0 && upIndex <= menuCount - 1) {
                mCurrentMenuIndex = upIndex;
                for (int i = 0; i < menuCount; i++) {
                    View child = llMenu.getChildAt(i);
                    child.setSelected(i == mCurrentMenuIndex);
                }
            }
        } else {
            MenuItem menuData = ((MenuItem) subMenu.getTag());
            menuData.up();
            refreshSubMenu(subMenu);
        }
    }

    //响应-键
    public void onDownPressed() {
        //尚未初始化
        if (llSubMenu == null || llSubMenu.getChildCount() == 0) {
            return;
        }
        View subMenu = llSubMenu.getChildAt(mCurrentMenuIndex);
        //没有子菜单则切换母菜单
        if (subMenu.getVisibility() == View.GONE) {
            int downIndex = mCurrentMenuIndex + 1;
            int menuCount = llMenu.getChildCount();
            if (downIndex >= 0 && downIndex <= menuCount - 1) {
                mCurrentMenuIndex = downIndex;
                for (int i = 0; i < menuCount; i++) {
                    View child = llMenu.getChildAt(i);
                    child.setSelected(i == mCurrentMenuIndex);
                }
            }
        } else {
            MenuItem menuData = ((MenuItem) subMenu.getTag());
            menuData.down();
            refreshSubMenu(subMenu);
        }
    }
}
