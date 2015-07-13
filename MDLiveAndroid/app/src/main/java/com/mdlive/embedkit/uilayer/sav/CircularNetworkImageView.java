package com.mdlive.embedkit.uilayer.sav;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by dhiman_da on 6/23/2015.
 */
public class CircularNetworkImageView extends NetworkImageView {
    public CircularNetworkImageView(Context context) {
        super(context);
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) {
            return;
        }

        CircularDrawable circularBitmapDrawable = new CircularDrawable(bm);
        circularBitmapDrawable.setAntiAlias(true);
        setImageDrawable(circularBitmapDrawable);

        setScaleType(ScaleType.CENTER_CROP);
        invalidate();
    }

}
