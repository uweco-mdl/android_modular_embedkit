package com.mdlive.symptomchecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

//import com.mdlive.embedkit.uilayer.sav.MDLiveGetStarted;

import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Exposes various "functions" to Javascript domain
 */
public class WebAppInterface
{
    private static WeakReference<Activity> parentContext = null;

    // Instantiate the interface and set the context
    public WebAppInterface(Activity ctx) {
        parentContext = new WeakReference<>(ctx);
    }

    /**
     * Start a new instance of SAV.
     * This is designed to be invoked from a webview.
     */
    @JavascriptInterface
    public static void startSAV()
    {
        Activity ctx = parentContext.get();
        if(ctx != null){
            final User user = User.getSelectedUser(ctx);

            try {
                Class clazz_mdlGetStarted = Class.forName(ctx.getString(R.string.mdl_mdlive_sav_module));
                Intent i = new Intent(ctx.getApplicationContext(), clazz_mdlGetStarted);

                if (user == null) {
                    ctx.startActivity(i);
                    /*startActivityWithClassName(MDLiveGetStarted.class, ctx);  */
                } else {
                    Method method = clazz_mdlGetStarted.getMethod("getGetStartedIntentWithUser", Context.class, User.class);
                    ctx.startActivity((Intent) method.invoke(null, ctx.getBaseContext(), user));
                    /*ctx.startActivity(MDLiveGetStarted.getGetStartedIntentWithUser(ctx, user));  */
                }
                /*Intent i = new Intent(ctx, MDLiveGetStarted.class); */
                ctx.startActivity(i);
            }catch(ClassNotFoundException cex){
                // SAV module not present, so exit.
            }
            catch(Exception ex){
                // IllegalAccessException, NoSuchMethodException, InvocationTargetException
            }

            // purge symptom activity off activity stack if it exists
            Activity sc_activity = MDLiveSymptomCheckerFragment.parentActivity.get();
            if(sc_activity!=null)
                sc_activity.finish();
        }
        else{
            // cannot start SAV without a context obj, so just exit
            //
        }
    }

    /*
    private static void startActivityWithClassName(final Class clazz, Context ctx)
    {
        final Intent intent = new Intent(ctx, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(intent);
    }
    */

}
