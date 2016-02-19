package com.mdlive.unifiedmiddleware.parentclasses.bean.response;

import com.google.gson.annotations.Expose;

public class LoginSuccessBean {

    @Expose
    private String uniqueid;
    @Expose
    private String msg;
    @Expose
    private String token;
    @Expose
    private String status;

    /**
     *
     * @return
     * The uniqueid
     */
    public String getUniqueid() {
        return uniqueid;
    }

    /**
     *
     * @param uniqueid
     * The uniqueid
     */
    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    /**
     *
     * @return
     * The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param msg
     * The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     *
     * @return
     * The token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
