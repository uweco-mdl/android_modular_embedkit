package com.mdlive.embedkit.uilayer.myhealth.activity;

/**
 * Created by sanjibkumar_p on 7/28/2015.
 */
public class ListItem {
    public String mString;
    public String mSubTitleString;
    public int mDrawableId;

    public ListItem() {
        super();
    }

    public ListItem(final String string, final int id , final String subTitleString) {
        mString = string;
        mDrawableId = id;
        mSubTitleString = subTitleString;
    }
}
