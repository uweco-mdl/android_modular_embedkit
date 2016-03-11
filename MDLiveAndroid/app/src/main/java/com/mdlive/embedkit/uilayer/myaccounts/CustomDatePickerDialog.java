package com.mdlive.embedkit.uilayer.myaccounts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.mdlive.embedkit.R;

public class CustomDatePickerDialog extends DatePickerDialog
{
    private Context context;

    public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.context = context;
    }

    public void show(){

        setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.accent_material_light));
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.accent_material_light));
            }
        });

        super.show();
    }
}