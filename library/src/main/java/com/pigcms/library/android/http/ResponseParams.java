package com.pigcms.library.android.http;

public class ResponseParams {
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
	
	
	
}
