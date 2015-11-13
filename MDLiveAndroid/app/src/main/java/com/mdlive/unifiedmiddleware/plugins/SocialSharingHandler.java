package com.mdlive.unifiedmiddleware.plugins;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Telephony;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mdlive.embedkit.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the social sharing functionality.
 */
public class SocialSharingHandler {

    private static final String SHARE_MSG = "The MDLIVE App lets you have a virtual doctor's appointment from your smartphone, 24/7/365. mdlive.com/getapp";
    private static final String SHARE_SUBJECT = "Virtual Doctor visits with MDLIVE App";

    /**
     *
     * This method triggers the sharing action.
     *
     * @param activity Activity
     * @throws java.io.IOException Exception occuring while performing IO Operation.
     */
    public void doSendIntent(final Activity activity) throws IOException {
        ArrayList<String> shareList = new ArrayList<String>();
        shareList.add("SMS");
        shareList.add("Twitter");
        shareList.add("Facebook");
        shareList.add("E-mail");

        final List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(shareIntent, 0);
        final ArrayList<String> installedApps = new ArrayList<String>();
        getinstalledAppList(resInfo, installedApps);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(activity, R.layout.share_list_item, shareList);
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Holo_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_share_dialog);
        ListView shareLv = (ListView)dialog.findViewById(R.id.shareLv);
        shareLv.setAdapter(listAdapter);
        shareLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onClickDialogList(i, activity, installedApps, targetedShareIntents, dialog);
            }
        });
        final Button cancelBtn = (Button)dialog.findViewById(R.id.shareCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        cancelBtn.setText(activity.getApplicationContext().getString(R.string.mdl_cancel));
        ((TextView)dialog.findViewById(R.id.shareHeaderTv)).setText(activity.getApplicationContext().getString(R.string.mdl_share));
        dialog.show();
    }

    private void onClickDialogList(int which, Activity activity, ArrayList<String> installedApps, List<Intent> targetedShareIntents, Dialog dialog) {
        Intent shareIntent;
        switch (which) {
            case 0:
                smsShare(activity);
                break;
            case 1:
                twitterShare(installedApps, targetedShareIntents, activity);
                break;
            case 2:
                shareIntent = new Intent(activity, FacebookShareUtil.class);
                shareIntent.putExtra("message", SHARE_MSG);
                activity.startActivity(shareIntent);
                break;
            case 3:
                emailShare(installedApps, activity);
                break;
            default:
                break;
        }
        dialog.dismiss();
    }

    /**
     *
     * This method provides the list of installed share applications.
     *
     */
    private void getinstalledAppList(List<ResolveInfo> resInfo, ArrayList<String> installedApps) {
        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                String packageName = resolveInfo.activityInfo.packageName;
                if (TextUtils.equals(packageName, "com.facebook.katana")) {
                    installedApps.add("Facebook");
                } else if (TextUtils.equals(packageName, "com.google.android.gm")) {
                    installedApps.add("GMail");
                } else if (TextUtils.equals(packageName, "com.twitter.android")) {
                    installedApps.add("Twitter");
                } else if(TextUtils.equals(packageName, "com.android.email")) {
                    installedApps.add("Email");
                }

            }
        }
    }

    /**
     *
     * This method performs the SMS sharing.
     *
     * @param activity Activity
     */
    private void smsShare(Activity activity) {
        Intent shareIntent;
        // For Android 4.4 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(activity);
            shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            shareIntent.putExtra("sms_body", SHARE_MSG);
            if (defaultSmsPackageName != null)  {
            // Can be null in case that there is no default, then the user would be able to choose any app that supports this intent.
                shareIntent.setPackage(defaultSmsPackageName);
            }
        } else {
            shareIntent = new Intent(Intent.ACTION_VIEW);
            shareIntent.putExtra("sms_body", SHARE_MSG);
            shareIntent.setType("vnd.android-dir/mms-sms");
        }
        activity.startActivity(shareIntent);
    }

    /**
     *
     * This method performs the email sharing.
     *
     * @param installedApps ArrayList containing the list of installed sharing apps.
     * @param activity Activity
     */
    private void emailShare(ArrayList<String> installedApps, Activity activity) {
        try {
            Intent shareIntent;
            if (installedApps.contains("GMail")) {
                shareIntent = new Intent(Intent.ACTION_VIEW);
                shareIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_MSG);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
                activity.startActivity(shareIntent);
            } else if (installedApps.contains("Email")) {
                shareIntent = new Intent(Intent.ACTION_VIEW);
                shareIntent.setClassName("com.android.email", "com.android.email.activity.MessageCompose");
                shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_MSG);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
                activity.startActivity(shareIntent);
            } else {
                Toast.makeText(activity,
                        activity.getResources().getString(R.string.mdl_share_failed),
                        Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.mdl_share_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     *
     * @param installedApps ArrayList containing the list of installed sharing apps.
     * @param targetedShareIntents The share intent
     * @param activity Activity
     */
    private void twitterShare(ArrayList<String> installedApps, List<Intent> targetedShareIntents, Activity activity) {
        if (installedApps.contains("Twitter")) {
            Intent targetedShareIntent = new Intent(Intent.ACTION_SEND);
            targetedShareIntent.setType("text/plain");
            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_MSG);
            targetedShareIntent.setPackage("com.twitter.android");
            targetedShareIntents.add(targetedShareIntent);
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
            activity.startActivity(chooserIntent);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(activity);
            TwitterShareUtil d = new TwitterShareUtil(activity, progressDialog, "http://twitter.com/share?text=" + SHARE_MSG);
            d.show();
            progressDialog.setMessage(activity.getResources().getString(R.string.mdl_loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

}