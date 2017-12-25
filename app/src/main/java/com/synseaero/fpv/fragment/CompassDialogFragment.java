package com.synseaero.fpv.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.synseaero.fpv.R;

public class CompassDialogFragment extends DialogFragment{

    private DialogInterface.OnClickListener listener = null;

    public void setDialogListener(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.hint)
                .setMessage(R.string.compass_calibrate_hint)
                .setPositiveButton(R.string.confirm, listener)  //设置回调函数
                .setNegativeButton(R.string.cancel,listener); //设置回调函数
        return b.create();
    }
}
