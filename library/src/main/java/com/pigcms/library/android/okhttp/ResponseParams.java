package com.pigcms.library.android.okhttp;

import android.support.v4.util.ArrayMap;

import java.util.HashMap;

public class ResponseParams {
	private static ArrayMap<String,String>  errorMap=new ArrayMap<>();

	static {
		errorMap.put("1001","抱歉!该卡券信息不正确!");
	}

	private  static final  String  KEY_STATUS="error";
	
	private static final  String  KEY_MESSAGE="msg";
	
	private  static final String KEY_DATA="data";
	
	//成功标识
	private static final String STATUS_SUCCESS = "0";

	public static String getStatusSuccess() {
		return STATUS_SUCCESS;
	}

	public static String getKeyStatus() {
		return KEY_STATUS;
	}

	public static String getKeyMessage() {
		return KEY_MESSAGE;
	}

	public static String getKeyData() {
		return KEY_DATA;
	}

	public static String GetErrorMsg(String code){
		if(errorMap.containsKey(code)) return errorMap.get(code);
		else return  null;
	}



	
	
}
