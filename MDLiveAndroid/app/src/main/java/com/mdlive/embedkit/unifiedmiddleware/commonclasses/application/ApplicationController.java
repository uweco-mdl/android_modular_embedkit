package com.mdlive.embedkit.unifiedmiddleware.commonclasses.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.utils.BitmapLruCache;
import com.mdlive.embedkit.unifiedmiddleware.plugins.BaseServicesPlugin;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class ApplicationController extends Application {
    private static ImageLoader mImageLoader;
    private static final int MAX_IMAGE_CACHE_ENTIRES  = 100;

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
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
    }

    /**
     * @return RequestQueue :: The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mImageLoader = getImageLoader(getApplicationContext());
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
            mImageLoader = new ImageLoader(Volley.newRequestQueue(getApplicationContext(), stack), new BitmapLruCache(MAX_IMAGE_CACHE_ENTIRES));
        return mImageLoader;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req :: Request
     * @param tag :: tag string
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req :: Request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
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
