package com.mdlive.unifiedmiddleware.commonclasses.font;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mdlive.embedkit.R;

/**
 * Created by sanjibkumar_p on 8/3/2015.
 */
public class TypefaceEditText extends EditText {
    public TypefaceEditText(final Context context) {
        this(context, null);
    }

    public TypefaceEditText(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceEditText(final Context context, final AttributeSet attrs,
                            final int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.TypefaceEditText);
            if (array != null) {
                final String typefaceAssetPath = array
                        .getString(R.styleable.TypefaceEditText_edittext_font);

                if (typefaceAssetPath != null) {
                    final AssetManager assets = context.getAssets();
                    try {
                        final Typeface typeface = Typeface.createFromAsset(
                                assets, typefaceAssetPath);
                        setTypeface(typeface);
                    } catch (Exception e) {

                    }
                }
                array.recycle();
            }
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        setFocusableInTouchMode(true);
        super.setOnTouchListener(l);
    }
}
