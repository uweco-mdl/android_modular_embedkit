package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import com.google.gson.annotations.Expose;

/**
 * Encapsulates basic features of @see com.mdlive.unifiedmiddleware.parentclasses.bean.response SentMessage
 * and @see com.mdlive.unifiedmiddleware.parentclasses.bean.response ReceivedMessage classes
 */
public abstract class AbstractSendReceiveMessage {

    @Expose
    public String date;

    @Expose
    public String time;

}
