package com.synseaero.view;


import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.synseaero.fpv.R;

public class SectionTextView extends TextView {

    private String divider = "/";

    private String[] value = null;

    private String selectedText = "";

    private int selectedTextColor = 0;

    private int unSelectedTextColor = 0;

    public SectionTextView(Context context) {
        super(context, null);
    }

    public SectionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SectionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        selectedTextColor = getContext().getResources().getColor(R.color.blue);
        unSelectedTextColor = getContext().getResources().getColor(R.color.menuText);
    }

    public void setSelectedTextColor(int color) {
        selectedTextColor = color;
        refreshUI();
    }

    public void setUnSelectedTextColor(int color) {
        unSelectedTextColor = color;
        refreshUI();
    }

    public void setDivider(String divider) {
        this.divider = divider;
        refreshUI();
    }

    public void setValue(String[] value) {
        this.value = value;
        refreshUI();
    }

    public void setSelectedText(String selected) {
        selectedText = selected;
        refreshUI();
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void refreshUI() {

        if (value == null || value.length == 0) {
            return;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i < value.length - 1) {
                sb.append(divider);
            }
        }
        int start = sb.toString().indexOf(selectedText);
        int selectedLength = selectedText.length();
        int allLength = sb.toString().length();
        ForegroundColorSpan allTextSpan = new ForegroundColorSpan(unSelectedTextColor);
        sb.setSpan(allTextSpan, 0, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(selectedTextColor);
        sb.setSpan(span, start, start + selectedLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan allTextSpan2 = new ForegroundColorSpan(unSelectedTextColor);
        sb.setSpan(allTextSpan2, start + selectedLength, allLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        setText(sb);
    }
}
