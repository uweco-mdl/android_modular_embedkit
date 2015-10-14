package com.mdlive.unifiedmiddleware.commonclasses.application;

/**
 * Singleton class providing Local Storage APIs
 */
public class LocalStorageSingleton
{
    private static LocalStorageSingleton instance = null;
    private static final Object syncObj = new Object();

    public static LocalStorageSingleton getInstance()
    {
        // restrict access to a single thread only
        // [ START CRITICAL SECTION ]
        synchronized(syncObj)
        {
            // create instance of this class if necessary...
            if (instance == null) {
                instance = new LocalStorageSingleton();
            }
        }
        // [ END CRITICAL SECTION ]

        return instance;
    }
}
