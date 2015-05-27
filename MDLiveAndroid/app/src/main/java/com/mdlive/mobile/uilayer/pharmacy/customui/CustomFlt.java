package com.mdlive.mobile.uilayer.pharmacy.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * This class is for customizing the frame layout.
 *
 * This class is used in MDLivePharmacyResult class to customizing google map to listen tap functions.
 *
 * @gestureDetector - is used to get onInterceptTouchEvent calls and update in activity ui.
 *
 */
public class CustomFlt extends FrameLayout
{
    private GestureDetector gestureDetector;
    private Context context;

    public CustomFlt(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;

        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        gestureDetector.onTouchEvent(ev);
        return false;
    }

    public GestureDetector getGestureListener(){
        return gestureDetector;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    }

}