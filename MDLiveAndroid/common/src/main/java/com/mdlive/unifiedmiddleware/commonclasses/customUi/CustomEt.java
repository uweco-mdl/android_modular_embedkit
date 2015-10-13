package com.mdlive.unifiedmiddleware.commonclasses.customUi;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mdlive.unifiedmiddleware.commonclasses.font.Typefaces;

/**
 * Created by Unnikrishnan B on 03/04/15.
 */
public class CustomEt extends EditText{
    public CustomEt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomEt(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEt(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typefaces.get(getContext(), "fonts/font.ttf");
            setTypeface(tf);
        }
    }
}
