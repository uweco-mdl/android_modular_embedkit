package com.mdlive.unifiedmiddleware.commonclasses.application;

/**
 * Singleton class providing Geolocation APIs
 */
public class GeoLocationSingleton
{
    private static GeoLocationSingleton instance = null;
    private static final Object syncObj = new Object();

    public static GeoLocationSingleton getInstance()
    {
        // restrict access to a single thread only
        // [ START CRITICAL SECTION ]
        synchronized(syncObj)
        {
            // create instance of this class if necessary...
            if (instance == null) {
                instance = new GeoLocationSingleton();
            }
        }
        // [ END CRITICAL SECTION ]

        return instance;
    }
}
