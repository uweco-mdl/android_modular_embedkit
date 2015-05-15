package com.mdlive.utils;

import android.app.Activity;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * Created by srinivasan_ka on 4/28/2015.
 * This class is used to config the path details of manifest, assets, res and package-name of Project.
 */
public class RobolectricGradleTestRunner extends RobolectricTestRunner {
    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    //Maximum SDK version supported by Robolectric
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        // Have to provide exact path of project folder
        String appRoot = "app/src/main/";

        /*  Linking to manifest, res, assets folders. Here no need that asset, res should be available.
            But manifest file is mandatory.     */

        String manifestPath = appRoot + "AndroidManifest.xml";
        String resDir = appRoot + "res";
        String assetsDir = appRoot + "assets";

       return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resDir), Fs.fileFromPath(assetsDir)) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }

            @Override
            public String getThemeRef(Class<? extends Activity> activityClass) {
                return "@style/RoboAppTheme";
            }
        };

    }
}
