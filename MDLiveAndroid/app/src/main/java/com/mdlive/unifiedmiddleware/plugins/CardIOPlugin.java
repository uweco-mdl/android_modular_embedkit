package com.mdlive.unifiedmiddleware.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.constants.IdConstants;

import io.card.payment.CardIOActivity;


/**
 * Created by unnikrishnan_b on 4/3/2015.
 */
public class CardIOPlugin {

    /**
     *
     * This function scans the credit card number by calling the CardIOActivity and
     * the results are passed to activity result of the context.
     *
     * @param context
     */
    public static void scanCard(Context context){
        try {
            Intent scanIntent = new Intent(context, CardIOActivity.class);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
            ((Activity) context).startActivityForResult(scanIntent, IdConstants.CREDITCARD_SCAN);
        }catch (NoClassDefFoundError e) {
            Log.e("CardIOPlugin:scanCard()", "Runtime crash: "+e.getMessage());

        }
    }
}
