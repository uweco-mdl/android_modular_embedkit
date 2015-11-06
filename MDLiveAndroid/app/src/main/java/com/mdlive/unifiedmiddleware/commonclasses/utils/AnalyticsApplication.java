/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdlive.unifiedmiddleware.commonclasses.utils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;

import java.util.HashMap;

/**
 * This is a subclass of {@link ApplicationController} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AnalyticsApplication extends ApplicationController{
    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        MDLIVE_APP_TRACKER, // Tracker used by MDLIVE.
        AFFILIATE_TRACKER   // Tracker used by affiliate.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    /**
     * Gets the {@link Tracker} for this {@link TrackerName}.
     * @return tracker
     */
    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            String tid = (trackerId == TrackerName.AFFILIATE_TRACKER)?getString(R.string.ga_customTrackingId):
                    getString(R.string.enable_mdlive_ga_tracking).equalsIgnoreCase("true")?
                    getString(R.string.ga_mdlive_trackingId):null;
            if(tid != null) {
                Tracker t = analytics.newTracker(R.xml.analytics);
                t.set("&tid", tid);
                mTrackers.put(trackerId, t);
            }
        }
        return mTrackers.get(trackerId);
    }
}
