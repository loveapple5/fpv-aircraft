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

import cn.feng.skin.manager.base.BaseFragment;
import cn.feng.skin.manager.entity.AttrFactory;
import cn.feng.skin.manager.entity.DynamicAttr;
import cn.feng.skin.manager.loader.SkinManager;

public class MenuFragment extends BaseFragment {

    private Menu menuData = new Menu();

    private LinearLayout llMenu;
    private LinearLayout llSubMenu;

    private int mCurrentMenuIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setMenuData(Menu menuData) {
        this.menuData = menuData;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("MenuFragment", "onCreateView");
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

    private View createMenuView(MenuItem data) {
        View menu = null;
        int type = data.type;
        switch (type) {
            case MenuItem.TYPE_TEXT: {
                TextView tvMenu = new TextView(getContext());
                tvMenu.setGravity(Gravity.CENTER);
                tvMenu.setText(data.curValue);
                tvMenu.setTextColor(SkinManager.getInstance().getColor(R.color.blue));
                tvMenu.setBackground(SkinManager.getInstance().getDrawable(R.drawable.bg_menu));
                tvMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.dip2px(getContext(), 12));
                menu = tvMenu;

                List<DynamicAttr> mDynamicAttr = new ArrayList<DynamicAttr>();
                mDynamicAttr.add(new DynamicAttr(AttrFactory.BACKGROUND, R.drawable.bg_menu));
                mDynamicAttr.add(new DynamicAttr(AttrFactory.TEXT_COLOR, R.color.blue));
                dynamicAddView(menu, mDynamicAttr);

                break;
            }
            case MenuItem.TYPE_SWITCH: {
                SwitchButton sbMenu = new SwitchButton(getContext());
                //sbMenu.setText(data.values[0], data.values[1]);
                sbMenu.setChecked("true".equalsIgnoreCase(data.curValue));
                sbMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.dip2px(getContext(), 12));

                sbMenu.setBackDrawable(SkinManager.getInstance().getDrawable(R.drawable.bg_switch));
                sbMenu.setThumbDrawable(SkinManager.getInstance().getDrawable(R.drawable.switch_thumb));

                int size = DensityUtil.dip2px(getContext(), 20);
                int marginTop = DensityUtil.dip2px(getContext(), -7);
                int marginLeft = DensityUtil.dip2px(getContext(), -2);
                sbMenu.setThumbSize(size, size);
                sbMenu.setThumbMargin(marginLeft, marginTop, marginLeft, marginTop);
                menu = sbMenu;

                List<DynamicAttr> mDynamicAttr = new ArrayList<DynamicAttr>();
                //mDynamicAttr.add(new DynamicAttr(AttrFactory.TEXT_COLOR, R.color.lightGray2));
                mDynamicAttr.add(new DynamicAttr(AttrFactory.KSW_BACK_DRAWABLE, R.drawable.bg_switch));
                mDynamicAttr.add(new DynamicAttr(AttrFactory.KSW_THUMB_DRAWABLE, R.drawable.switch_thumb));
                dynamicAddView(menu, mDynamicAttr);
                break;
            }
            case MenuItem.TYPE_SELECT: {
                SectionTextView tvSelectMenu = new SectionTextView(getContext());
                tvSelectMenu.setGravity(Gravity.CENTER);
//                ColorStateList selectCsl = getResources().getColorStateList(R.color.menu_tv);
//                tvSelectMenu.setTextColor(selectCsl);

                tvSelectMenu.setValue(data.values);
                tvSelectMenu.setSelectedText(data.curValue);

                tvSelectMenu.setBackgroundResource(R.drawable.transparent);
                tvSelectMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.dip2px(getContext(), 12));

                tvSelectMenu.setSelectedTextColor(SkinManager.getInstance().getColor(R.color.blue));
                tvSelectMenu.setUnSelectedTextColor(SkinManager.getInstance().getColor(R.color.menuText));

                menu = tvSelectMenu;

                List<DynamicAttr> mDynamicAttr = new ArrayList<DynamicAttr>();
                mDynamicAttr.add(new DynamicAttr(AttrFactory.SELECTED_TEXT_COLOR, R.color.blue));
                mDynamicAttr.add(new DynamicAttr(AttrFactory.UNSELECTED_TEXT_COLOR, R.color.menuText));
                dynamicAddView(menu, mDynamicAttr);

                break;
            }
            case MenuItem.TYPE_PROGRESS: {
                SeekBar sbarMenu = new SeekBar(getContext());
                sbarMenu.setThumb(null);
                sbarMenu.setProgressDrawable(SkinManager.getInstance().getDrawable(R.drawable.progressbar));
                sbarMenu.setProgress(Integer.parseInt(data.curValue));
                menu = sbarMenu;

                List<DynamicAttr> mDynamicAttr = new ArrayList<DynamicAttr>();
                mDynamicAttr.add(new DynamicAttr(AttrFactory.PROGRESS_DRAWABLE, R.drawable.progressbar));
                dynamicAddView(menu, mDynamicAttr);
                break;
            }
        }
        LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 25));
        if (menu instanceof SeekBar) {
            menuParams.height = DensityUtil.dip2px(getContext(), 2);
        }
        menuParams.setMargins(0, 0, 0, DensityUtil.dip2px(getContext(), 5));
        menu.setLayoutParams(menuParams);
        menu.setFocusable(true);
        menu.setClickable(true);
        menu.setTag(data);

        return menu;
    }

    private void refreshSubMenu(View subMenuView) {
        Object tag = subMenuView.getTag();
        if (tag != null && tag instanceof MenuItem) {
            MenuItem data = (MenuItem) tag;
            int type = data.type;
            switch (type) {
                case MenuItem.TYPE_TEXT:
                    ((TextView) subMenuView).setText(data.curValue);
                    break;
                case MenuItem.TYPE_SWITCH:
                    ((SwitchButton) subMenuView).setChecked("true".equalsIgnoreCase(data.curValue));
                    break;
                case MenuItem.TYPE_SELECT:

                    ((SectionTextView) subMenuView).setValue(data.values);
                    ((SectionTextView) subMenuView).setSelectedText(data.curValue);
                    break;
                case MenuItem.TYPE_PROGRESS:
                    ((SeekBar) subMenuView).setProgress(Integer.parseInt(data.curValue));
                    break;
            }
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
