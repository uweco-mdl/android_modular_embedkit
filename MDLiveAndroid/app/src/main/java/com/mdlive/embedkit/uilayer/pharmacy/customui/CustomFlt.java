package com.mdlive.embedkit.uilayer.pharmacy.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by srinivasan_ka on 5/21/2015.
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