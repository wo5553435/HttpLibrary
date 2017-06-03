package com.pigcms.library.capture.utils;

/**
 * 日志管理工具类
 * 
 * @Description
 * @author 刘国山 lgs@yitong.com.cn
 * @version 1.0 2012-8-15
 * @class com.yitong.zjrc.mbank.android.logs.Logs
 */
public class Logs {

	private static boolean  CanLog=true;

	// public static final String TAG_PREFIX = "elife";

	public static void SetEnable(boolean flag){
		if(flag){
			CanLog=true;
		}else{
			CanLog=false;
		}
	}
	
	public static void v(String tag, String msg) {
		if (CanLog) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (CanLog) {
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (CanLog) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (CanLog) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (CanLog) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (CanLog) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (CanLog) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (CanLog) {
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (CanLog) {
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (CanLog) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (CanLog) {
			android.util.Log.e(tag, msg, tr);
		}
	}
}
