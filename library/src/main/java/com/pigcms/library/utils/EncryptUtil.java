package com.pigcms.library.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

	private static String signString = "app22serv1e695keysh5jg5h";

	/**
	 * 获取Json字符串加密
	 */
	public static String getEncryptValue(String jsonStr) {

		Logs.i("EncryptUtil", "Value加密结果:" + Base64.encodeToString(jsonStr.getBytes(), Base64.DEFAULT));
		return Base64.encodeToString(jsonStr.getBytes(), Base64.DEFAULT);
	}

	/**
	 * 获取Json字符串和Key加密
	 */
	public static String getEncryptSign(String jsonStr) {

		Logs.i("EncryptUtil", "Sign加密结果:" + MD5(Base64.encodeToString(jsonStr.getBytes(), Base64.DEFAULT) + signString));
		return MD5(Base64.encodeToString(jsonStr.getBytes(), Base64.DEFAULT) + signString);
	}

	/**
	 * MD5加密测试
	 */
	public static String MD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
}