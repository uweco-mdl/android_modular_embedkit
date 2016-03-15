package com.mdlive.unifiedmiddleware.plugins;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import com.mdlive.embedkit.R;

/**
 * This displays the twitter share inside a dialog. To be used if twitter is not installed by default.
 */
public class TwitterShareUtil extends Dialog {
    ProgressDialog progress;
    String shareURL;

    ImageView close;

    /**
     * The constructor accepts the preogress dailog and shareUrl.
     *
     * @param context Context where the Twitter share is shown
     * @param progress Progress dialog instance
     * @param shareURL Share URL
     */
    public TwitterShareUtil(Context context, ProgressDialog progress,
                            String shareURL) {
        super(context);
        this.progress = progress;
        this.shareURL = shareURL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_share_dialog);
        close = (ImageView) findViewById(R.id.close_button);
        WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(shareURL);
        mWebView.setWebViewClient(new TwitterWebViewClient());
        mWebView.setPictureListener(new WebView.PictureListener() {
            @Override
            public void onNewPicture(WebView view, Picture picture) {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*Toast.makeText(TwitterShareUtil.this.getContext(),
                        TwitterShareUtil.this.getContext().getResources().getString(R.string.mdl_share_cancelled),
                        Toast.LENGTH_SHORT).show();*/
                Snackbar.make(TwitterShareUtil.this.findViewById(android.R.id.content),
                        TwitterShareUtil.this.getContext().getResources().getString(R.string.mdl_share_cancelled),
                        Snackbar.LENGTH_SHORT).show();
                TwitterShareUtil.this.cancel();
            }
        });

    }

    /**
     *
     * The WebViewClient. When the sharing is completed, the webview is automactically closed.
     */
    class TwitterWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("/complete")){
                /*Toast.makeText(TwitterShareUtil.this.getContext(),
                        TwitterShareUtil.this.getContext().getResources().getString(R.string.mdl_share_success),
                        Toast.LENGTH_SHORT).show();*/
                Snackbar.make(TwitterShareUtil.this.findViewById(android.R.id.content),
                        TwitterShareUtil.this.getContext().getResources().getString(R.string.mdl_share_success),
                        Snackbar.LENGTH_SHORT).show();
                TwitterShareUtil.this.dismiss();
            }
            view.loadUrl(url);
            return true;
        }
    }
}
