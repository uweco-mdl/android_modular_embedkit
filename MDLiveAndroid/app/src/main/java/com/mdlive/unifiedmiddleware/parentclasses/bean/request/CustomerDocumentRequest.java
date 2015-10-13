package com.mdlive.unifiedmiddleware.parentclasses.bean.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class CustomerDocumentRequest {
    @SerializedName("customer_document")
    @Expose
    public com.mdlive.unifiedmiddleware.parentclasses.bean.request.CustomerDocument customerDocument;
}
