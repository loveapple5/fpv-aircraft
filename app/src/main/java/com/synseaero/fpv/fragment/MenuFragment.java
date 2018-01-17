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

public abstract class MenuFragment extends BaseFragment {

    protected Menu menuData = new Menu();

    public void setMenuData(Menu menuData) {
        this.menuData = menuData;
    }

    //响应back键
    public abstract boolean onBackPressed();

    //响应confirm键
    public abstract void onConfirmPressed();

    //响应+键
    public abstract void onUpPressed();

    //响应-键
    public abstract void onDownPressed();

    protected View createMenuView(MenuItem data) {
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

    protected void refreshSubMenu(View subMenuView) {
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

}
