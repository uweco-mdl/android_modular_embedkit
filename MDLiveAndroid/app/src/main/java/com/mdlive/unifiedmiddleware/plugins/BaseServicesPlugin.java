package com.mdlive.unifiedmiddleware.plugins;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.BitmapLruCache;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * The BaseServicePlugin class handles the top level service calls. This has separate
 * methods to handle different types of service requests.
 *
 */
public abstract class BaseServicesPlugin {

    private Context context;
    private ProgressDialog pDialog;
    private static final int MAX_IMAGE_CACHE_ENTRIES  = 100;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static int WEBSERVICE_TIMEOUT = 60000;  // max. 60 sec allowed for a web service call to respond
    private static int WEBSERVICE_TIMEOUT_ONCALL = 300000;

    public BaseServicesPlugin(Context context, ProgressDialog pDialog){
       this.context = context;
       this.pDialog = pDialog;
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(MAX_IMAGE_CACHE_ENTRIES));
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
            if (MdliveUtils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(url, obj,responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout;
                if(url.contains("/appointments/oncall_consultation")){
                    socketTimeout = WEBSERVICE_TIMEOUT_ONCALL;
                }else{
                    socketTimeout = WEBSERVICE_TIMEOUT;
                }
                 RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req,context);
            } else {
                hideDialogIfShowing();
                MdliveUtils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /* Put Request */
    public void jsonObjectPutRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (MdliveUtils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout = WEBSERVICE_TIMEOUT;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req,context);
            } else {
                hideDialogIfShowing();
                MdliveUtils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*Get Request*/
    public void jsonObjectGetRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (MdliveUtils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout = WEBSERVICE_TIMEOUT;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req,context);
            } else {
                hideDialogIfShowing();
                MdliveUtils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*Get Request*/
    public void jsonObjectDeleteRequest(String url, String params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            if (MdliveUtils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, url, obj, responseListener
                        ,errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout = WEBSERVICE_TIMEOUT;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req, context);
            } else {
                hideDialogIfShowing();
                MdliveUtils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /* JSON ARRAY Get Request */
    public void jsonArrayGetRequst(String url, String params, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener){
        try {
            if (MdliveUtils.isNetworkAvailable(context)) {
                JSONObject obj = params !=null ? new JSONObject(params) : null;
                JsonArrayRequest req = new JsonArrayRequest(url, responseListener, errorListener) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getAuthHeader(context);
                    }
                };
                int socketTimeout = 60000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                req.setRetryPolicy(policy);
                ApplicationController.getInstance().addToRequestQueue(req, context);
            } else {
                hideDialogIfShowing();
                MdliveUtils.connectionTimeoutError(pDialog, context);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static Map getAuthHeader(Context context){
        Map<String, String> headerMap = new HashMap<String, String>();
        String creds = String.format("%s:%s", AppSpecificConfig.API_KEY,AppSpecificConfig.SECRET_KEY);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        SharedPreferences sharedpreferences = context.getSharedPreferences(PreferenceConstants.USER_PREFERENCES,Context.MODE_PRIVATE);

        headerMap.put("Authorization", auth);
        headerMap.put("RemoteUserId", sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
        String dependentId = sharedpreferences.getString(PreferenceConstants.DEPENDENT_USER_ID, null);
        Log.e("Authorization",auth);
        Log.e("RemoteUserId",sharedpreferences.getString(PreferenceConstants.USER_UNIQUE_ID, AppSpecificConfig.DEFAULT_USER_ID));
        if(dependentId != null) {
            headerMap.put("DependantId", dependentId);
            Log.e("DependentID", dependentId);
        }

        sharedpreferences = context.getSharedPreferences(PreferenceConstants.DEVICE_OS,Context.MODE_PRIVATE);
        String deviceOS = sharedpreferences.getString(PreferenceConstants.DEVICE_OS_KEY, null);
        if (deviceOS != null) {
            headerMap.put("Device_OS", deviceOS);
            Log.e("Device_OS", deviceOS);
        }

        sharedpreferences = context.getSharedPreferences(PreferenceConstants.DEVICE_ID,Context.MODE_PRIVATE);
        String deviceID = sharedpreferences.getString(PreferenceConstants.DEVICE_ID, null);
        if (deviceID != null) {
            headerMap.put("device_id", deviceID);
            Log.e("device_id", deviceID);
        }
        return headerMap;
    }

    private void hideDialogIfShowing() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
