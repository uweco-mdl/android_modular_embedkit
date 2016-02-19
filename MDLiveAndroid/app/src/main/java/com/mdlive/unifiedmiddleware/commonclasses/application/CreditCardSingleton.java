package com.mdlive.unifiedmiddleware.commonclasses.application;

/**
 * Singleton class providing Credit card scanning APIs
 */
public class CreditCardSingleton
{
    private static CreditCardSingleton instance = null;
    private static final Object syncObj = new Object();

    public static CreditCardSingleton getInstance()
    {
        // restrict access to a single thread only
        // [ START CRITICAL SECTION ]
        synchronized(syncObj)
        {
            // create instance of this class if necessary...
            if (instance == null) {
                instance = new CreditCardSingleton();
            }
        }
        // [ END CRITICAL SECTION ]

        return instance;
    }
}
