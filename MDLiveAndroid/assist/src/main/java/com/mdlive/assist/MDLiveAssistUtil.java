package com.mdlive.assist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MDLiveAssistUtil{
    private static final String TAG = "MDLIVE_ASSIST";

    /**
     * Shows MD Live Assist dialog
     */
    public static  void showMDLiveAssistDialog(final Activity activity, final String assistPhoneNum) {
        try {
            final WeakReference<Activity> reference = new WeakReference<Activity>(activity);

            if (reference.get() == null) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(reference.get());
            builder.setCancelable(false);
            LayoutInflater layoutInflater = LayoutInflater.from(reference.get());
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            TextView alertText = (TextView) view.findViewById(R.id.alertdialogtextview);
            alertText.setText(reference.get().getText(R.string.mdl_call_text));

            builder.setView(view);
            builder.setPositiveButton(reference.get().getText(R.string.mdl_call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + assistPhoneNum.trim()));
                                reference.get().startActivity(intent);
                            } catch (Exception e) {
                            }

                        }
                    });
            builder.setNegativeButton(reference.get().getText(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {

                    }
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
