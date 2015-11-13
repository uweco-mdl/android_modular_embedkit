package com.mdlive.unifiedmiddleware.plugins;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.mdlive.embedkit.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FacebookShareUtil extends Activity {
    private ProgressDialog pDialog;
    private UiLifecycleHelper uiHelper;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_stream", "manage_pages");
    private boolean checkedPermission = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.mdl_loading));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        facebookAppShare();
    }

    /**
     * This method is responsible for facebook sharing after authentication. It will check weather the
     * facebook application is installed in the device. If installed, it will redirect the sharing to
     * FB app. Else, it will open a feed dialog for sharing.
     *
     */
    private void facebookAppShare() {
        final String logo = "https://www.mdlive.com/images/mdlive-chicklet-logo.png";
        final String message[] = getIntent().getStringExtra("message").split("[.]",2);
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {

            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setDescription(message[0])
                    .setCaption(message[1])
                    .setLink(message[1])
                    .setPicture(logo)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());

        } else {
            publishFeedDialog(logo, message);



        }
    }

    /**
     *
     * Feed dialog for facebook sharing. Displays a dialogbox for facebook sharing.
     *
     */
    private void publishFeedDialog(final String logo, final String[] message) {
        Session s = new Session(this);
        Session.setActiveSession(s);
        Session.OpenRequest request = new Session.OpenRequest(this);
        request.setPermissions(PERMISSIONS);
        request.setCallback( new Session.StatusCallback() {
            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    if (session != null) {
                        // Check for publish permissions
                        List<String> permissions = session.getPermissions();
                        if (!isSubsetOf(PERMISSIONS, permissions)) {
                            if(!checkedPermission) {
                                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(FacebookShareUtil.this,
                                        PERMISSIONS);
                                session.requestNewPublishPermissions(newPermissionsRequest);
                                checkedPermission = true;
                                return;
                            } else {
                                Toast.makeText(FacebookShareUtil.this,
                                        getResources().getString(R.string.mdl_share_failed),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                    Bundle params = new Bundle();
                    params.putString("name", "mdlive.com");
                    params.putString("caption", message[1]);
                    params.putString("description", message[0]);
                    params.putString("link", message[1]);
                    params.putString("picture",logo);
                    WebDialog feedDialog = (
                            new WebDialog.FeedDialogBuilder(FacebookShareUtil.this,
                                    Session.getActiveSession(),
                                    params))
                            .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                                @Override
                                public void onComplete(Bundle values,
                                                       FacebookException error) {
                                    onCompleteFeedDialog(values, error);
                                }
                            })
                            .build();
                    feedDialog.show();
                } else {
                    checkedPermission = true;
                }
            }
        });
        s.openForPublish(request);
    }

    private void onCompleteFeedDialog(Bundle values, FacebookException error) {
        pDialog.dismiss();

        if (error == null) {
            // When the story is posted, echo the success
            // and the post Id.
            final String postId = values.getString("post_id");
            if (postId != null) {
                Toast.makeText(FacebookShareUtil.this,
                        getResources().getString(R.string.mdl_share_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                // User clicked the Cancel button
                Toast.makeText(FacebookShareUtil.this,
                        getResources().getString(R.string.mdl_share_cancelled),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (error instanceof FacebookOperationCanceledException) {
            // User clicked the "x" button
            Toast.makeText(FacebookShareUtil.this,
                    getResources().getString(R.string.mdl_share_cancelled),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Generic, ex: network error
            Toast.makeText(FacebookShareUtil.this,
                    getResources().getString(R.string.mdl_share_failed),
                    Toast.LENGTH_SHORT).show();
        }
        FacebookShareUtil.this.finish();
    }
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }


    /**
     *
     * This must be called if facebook api is used. Once the user is logged in,
     * the session is set here.
     *
     */
    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                pDialog.dismiss();
                FacebookShareUtil.this.finish();
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                pDialog.dismiss();
                FacebookShareUtil.this.finish();
            }
        });

        if (resultCode == 0) {
            Toast.makeText(FacebookShareUtil.this, getResources().getString(R.string.mdl_share_failed), Toast.LENGTH_LONG).show();
            pDialog.dismiss();
            finish();
        }
        try {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        pDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
