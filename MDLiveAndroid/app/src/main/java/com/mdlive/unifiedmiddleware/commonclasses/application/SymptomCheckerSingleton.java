package com.mdlive.unifiedmiddleware.commonclasses.application;

/**
 * Singleton class providing Symptom Checker APIs
 */
public class SymptomCheckerSingleton {

    private static SymptomCheckerSingleton instance = null;
    private static final Object syncObj = new Object();

    public static SymptomCheckerSingleton getInstance()
    {
        // restrict access to a single thread only
        // [ START CRITICAL SECTION ]
        synchronized(syncObj)
        {
            // create instance of this class if necessary...
            if (instance == null) {
                instance = new SymptomCheckerSingleton();
            }

        }
        // [ END CRITICAL SECTION ]

        return instance;
    }
}
