package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsultationHistoryDetails {

    @SerializedName("consultation_history")
    @Expose
    private List<ConsultationHistory> consultationHistory = new ArrayList<ConsultationHistory>();

    /**
     *
     * @return
     * The consultationHistory
     */
    public List<ConsultationHistory> getConsultationHistory() {
        return consultationHistory;
    }

    /**
     *
     * @param consultationHistory
     * The consultation_history
     */
    public void setConsultationHistory(List<ConsultationHistory> consultationHistory) {
        this.consultationHistory = consultationHistory;
    }

}
