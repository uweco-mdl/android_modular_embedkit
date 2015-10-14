package com.mdlive.unifiedmiddleware.commonclasses.application;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mdlive.unifiedmiddleware.commonclasses.utils.BitmapLruCache;
import com.mdlive.unifiedmiddleware.plugins.BaseServicesPlugin;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/***
 *
 * This is the controller class for Volley.
 *
 */
public class ApplicationController extends android.support.multidex.MultiDexApplication {
    private static ImageLoader mImageLoader;
    private static final int MAX_IMAGE_CACHE_ENTIRES  = 100;
    private static Context context;
    private static BitmapLruCache bitmapLruCache = new BitmapLruCache(MAX_IMAGE_CACHE_ENTIRES);

    public BitmapLruCache getBitmapLruCache(){
        return bitmapLruCache;
    }
    /**
     * Log or request TAG
     */
    public static final String TAG = "Request - ";
    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static ApplicationController sInstance;
    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * @return ApplicationController :: ApplicationController singleton instance
     */
    public static synchronized ApplicationController getInstance() {
        if(sInstance == null){
            sInstance = new ApplicationController();

        }
        return sInstance;
    }

    /**
     * @return RequestQueue :: The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue(Context context) {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
            mImageLoader = getImageLoader(context.getApplicationContext());
        }

        return mRequestQueue;
    }



    public ImageLoader getImageLoader(final Context context)
    {
        HurlStack stack = new HurlStack() {
            @Override
            public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                    throws IOException, AuthFailureError {
                headers.putAll(BaseServicesPlugin.getAuthHeader(context));
                return super.performRequest(request, headers);
            }
        };
        if(mImageLoader == null)
            mImageLoader = new ImageLoader(Volley.newRequestQueue(context.getApplicationContext(), stack), bitmapLruCache);
        return mImageLoader;
    }


    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req :: Request
     */
    public <T> void addToRequestQueue(Request<T> req, Context context) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue(context).add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag :: Tag Object
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
