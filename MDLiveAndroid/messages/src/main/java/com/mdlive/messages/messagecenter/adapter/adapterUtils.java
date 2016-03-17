package com.mdlive.messages.messagecenter.adapter;

import android.widget.ArrayAdapter;

import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.AbstractSendReceiveMessage;

/**
 * Convenience 'functions' for message center code
 */
public class adapterUtils {

    public static <T extends AbstractSendReceiveMessage> String eliminateLagging(ArrayAdapter<T> adapter, int position){

        // solve the lagging issue in scrolling starts :
        String time = adapter.getItem(position).date;
        if (time.contains(" ")) {
            String newTime = time.substring(0, time.indexOf(" "));
            if (newTime.equalsIgnoreCase(TimeZoneUtils.getCurrentDate
                    (time.substring(time.length()-4,time.length()-1)))) {
                newTime = adapter.getItem(position).time.substring(0, adapter.getItem(position).time.indexOf(" "));
            }
            time = newTime;
        }

        return time;
    }
}
