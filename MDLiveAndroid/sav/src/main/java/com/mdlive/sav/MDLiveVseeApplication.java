package com.mdlive.sav;

import android.app.Application;


public class MDLiveVseeApplication extends Application {

    // Set in assets/login.properties
    private static String username = null;
    private static String password = null;
    private static boolean firstTimeOnly = true;

    // Try two different files just for convenience.  I can check login.properties in with
    // no values and ignore private-login.properties.  If you aren't checking this in, use
    // either.
    public static final String LOGIN_PROPERTIES_FILENAME = "login.properties";
    public static final String PRIVATE_LOGIN_PROPERTIES_FILENAME = "private-login.properties";

    public static String getUsername() {
        return username;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    protected static void setCredentials(String uid, String pass)
    {
        username=uid;
        password=pass;
    }

    protected static boolean loginCredentialsValid()
    {
        return username != null && password != null && !username.isEmpty() && !password.isEmpty();
    }

    protected static boolean invokedForFirstTime()
    {
        return(firstTimeOnly);
    }

    protected static void disableFirstTimeOnly()
    {
        firstTimeOnly = false;
    }


}
