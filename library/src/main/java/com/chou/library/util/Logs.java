package com.chou.library.util;


/**
 * 日志管理工具类
 */
public class Logs {

	private  static boolean LOG_FLAG=false;

	/*static {
		LOG_FLAG=Constant.isDebug;
	}*/

	public static void v(String tag, String msg) {
		if (LOG_FLAG) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (LOG_FLAG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_FLAG) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (LOG_FLAG) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (LOG_FLAG) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOG_FLAG) {
			android.util.Log.e(tag, msg, tr);
		}
	}
}
