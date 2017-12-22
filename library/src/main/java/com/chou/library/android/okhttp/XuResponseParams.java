package com.chou.library.android.okhttp;

import android.support.v4.util.ArrayMap;

public class XuResponseParams {
    private static ArrayMap<String, String> errorMap = new ArrayMap<>();

    static {
        errorMap.put("1001", "抱歉!该卡券信息不正确!");
    }

    private static final String KEY_CODE = "err_code";

    private static final String KEY_DOM = "err_dom";

    private static final String KEY_MSG = "err_msg";

    public static String getKeyCode() {
        return KEY_CODE;
    }

    public static String getKeyDom() {
        return KEY_DOM;
    }

    public static String getKeyMsg() {
        return KEY_MSG;
    }

    //成功标识
    private static final String STATUS_SUCCESS = "0";

    public static String getStatusSuccess() {
        return STATUS_SUCCESS;
    }

    public static String GetErrorMsg(String code) {
        if (errorMap.containsKey(code)) return errorMap.get(code);
        else return null;
    }
}
