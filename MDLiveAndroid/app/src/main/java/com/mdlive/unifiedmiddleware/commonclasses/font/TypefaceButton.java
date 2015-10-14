package com.mdlive.unifiedmiddleware.commonclasses.font;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.mdlive.embedkit.R;


/**
 * Created by sanjibkumar_p on 8/3/2015.
 */
public class TypefaceButton extends Button {
    public TypefaceButton(final Context context) {
        this(context, null);
    }

    public TypefaceButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceButton(final Context context, final AttributeSet attrs,
                          final int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.TypefaceButton);
            if (array != null) {
                final String typefaceAssetPath = array
                        .getString(R.styleable.TypefaceButton_button_font);

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
}
