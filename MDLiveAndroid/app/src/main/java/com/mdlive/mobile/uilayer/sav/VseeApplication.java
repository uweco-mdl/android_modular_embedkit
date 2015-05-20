package com.mdlive.mobile.uilayer.sav;

import android.app.Application;


public class VseeApplication extends Application {

    // Set in assets/login.properties
    private static String username = null;
    private static String password = null;
    private static boolean firstTimeOnly = true;

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
