package com.mdlive.unifiedmiddleware.commonclasses.customUi;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mdlive.unifiedmiddleware.commonclasses.font.Typefaces;

public class CustomTv extends TextView{

	public CustomTv(Context context) {
        super(context);
        initText();
    }

    public CustomTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public CustomTv(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initText();
    }

    public final void initText() {
        Typeface tf = Typefaces.get(getContext(), "fonts/HelveticaNeue_extended.ttf");
        setTypeface(tf);
    }
}
