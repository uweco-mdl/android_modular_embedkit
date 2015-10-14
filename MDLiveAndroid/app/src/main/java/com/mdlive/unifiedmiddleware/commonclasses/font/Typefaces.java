package com.mdlive.unifiedmiddleware.commonclasses.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Typefaces {
    private static final String TAG = "Typefaces";
    private static final Map<String, Typeface> CACHE  = new HashMap<String, Typeface>();
    private Typefaces(){
    	// private constructor
    }
    public static Typeface get(Context c, String assetPath) {
        synchronized (CACHE) {
            if (!CACHE.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    CACHE.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath+ "' because ", e);
                    return null;
                }
            }
            return CACHE.get(assetPath);
        }
    }
}