package com.mdlive.unifiedmiddleware.commonclasses.customUi;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import com.mdlive.unifiedmiddleware.commonclasses.font.Typefaces;


public class CustomActv extends AutoCompleteTextView {


    public CustomActv(Context context) {
        super(context);
        init();
    }

    public CustomActv(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomActv(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typefaces.get(getContext(), "fonts/font.ttf");
            setTypeface(tf);
        }
    }


}
