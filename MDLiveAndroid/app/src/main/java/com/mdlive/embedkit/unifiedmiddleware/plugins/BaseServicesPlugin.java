package com.mdlive.embedkit.unifiedmiddleware.plugins;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.utils.BitmapLruCache;
import com.mdlive.embedkit.unifiedmiddleware.commonclasses.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Unnikrishnan B
 */
public abstract class BaseServicesPlugin {

    private Context context;
    private ProgressDialog pDialog;
    private static final int MAX_IMAGE_CACHE_ENTIRES  = 100;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    public BaseServicesPlugin(Context context, ProgressDialog pDialog){
       this.context = context;
       this.pDialog = pDialog;
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(MAX_IMAGE_CACHE_ENTIRES));
    }

    /**
     *
     * This method will send a JSONObject Request and will retrieve a JSONObject Response.
     *
     * @param url       The Request URL
     * @param params    The POST Parameters
     * @param responseListener
     * @param errorListener
     */
    public void jsonObjectPostRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(url, obj,responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout = 60000;//30 seconds - change to what you want
                 RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req);
            } else {
                Utils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /* Put Request */
    public void jsonObjectPutRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                ApplicationController.getInstance().addToRequestQueue(req);
            } else {
                Utils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*Get Request*/
    public void jsonObjectGetRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                ApplicationController.getInstance().addToRequestQueue(req);
            } else {
                Utils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*Get Request*/
    public void jsonObjectDeleteRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                ApplicationController.getInstance().addToRequestQueue(req);
            } else {
                Utils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static Map getAuthHeader(Context context){
        Map<String, String> headerMap = new HashMap<String, String>();
        String creds = String.format("%s:%s", AppSpecificConfig.API_KEY, AppSpecificConfig.SECRET_KEY);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);

        headerMap.put("Authorization", auth);
        headerMap.put("RemoteUserId", sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
        String dependentId = sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        if(dependentId != null) {
            headerMap.put("DependantId", dependentId);
        }
        Log.d("String -- Header",headerMap.toString());
        return headerMap;
    }

}
