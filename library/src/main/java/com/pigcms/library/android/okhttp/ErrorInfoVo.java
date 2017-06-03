package com.pigcms.library.android.okhttp;

/**
 * Created by win7 on 2017-04-18.
 */

public class ErrorInfoVo extends BaseVo{
    private String errorcode;
    private String errorMsg;


    public ErrorInfoVo(String code,String msg){
        this.errorcode=code;this.errorMsg=msg;
    }
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }
}
