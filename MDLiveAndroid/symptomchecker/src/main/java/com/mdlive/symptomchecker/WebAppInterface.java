package com.mdlive.symptomchecker;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

//import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;

import java.lang.ref.WeakReference;

/**
 * Exposes various "functions" to Javascript domain
 */
public class WebAppInterface
{
    private static WeakReference<Context> parentContext = null;

    // Instantiate the interface and set the context
    public WebAppInterface(Context ctx) {
        parentContext = new WeakReference<Context>(ctx);
    }

    /**
     * Start a new instance of SAV.
     * This is designed to be invoked from a webview.
     */
    @JavascriptInterface
    public static void startSAV()
    {
        Context ctx = parentContext.get();
        if(ctx != null){
            /*
            Intent i = new Intent(ctx, MDLiveGetStarted.class);
            ctx.startActivity(i);
            */
        }
        else{
            // cannot start SAV without a context obj, so just exit
            //
        }
    }

}
