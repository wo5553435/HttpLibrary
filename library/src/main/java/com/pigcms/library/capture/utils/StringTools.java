package com.pigcms.library.capture.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * String操作工具类
 * 
 * @author 刘国山 (刘国山)
 * @version 1.0 2011-3-8
 * @class vc.cn.thewk.ssh.utils.StringUtils
 */
public abstract class StringTools {

	/**
	 * 统计字符串长度,一个双字节字符长度计2，ASCII字符计1
	 * 
	 * @param str 字符串
	 */
	public static int getLength(String str) {
		return str.replaceAll("[^\\x00-\\xff]", "aa").length();
	}

	public static String getStringByBytes(byte[] bs) {
		return new String(bs);
	}

	/**
	 * 判断字符串是否为空，即为null或""
	 */
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.length() == 0));
	}
	
	/**
	 * 判断字符串是否不为空，即不为null且不为""
	 */
	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}
	
	/**
	 * 判断字符串是否为空白，即为null或为" "
	 */
	public static boolean isBlank(String str) {
		return ((str == null) || (str.trim().length() == 0));
	}
	
	// 随机种子
	private static final char[] CHAR_RANDOMS = { '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z' };
	
	/**
	 * 生成长度为5到10的随机字符串. 随机字符串的内容包含[1-9 A-Z a-z]的字符.
	 * 
	 * @return 随机字符串.
	 */
	public static String buildRandomString() {
		return buildRandomString(5, 10);
	}

	/**
	 * 生成随机字符串. 随机字符串的内容包含[1-9 A-Z a-z]的字符.
	 * 
	 * @param length 必须为正整数 随机字符串的长度
	 * @return 随机字符串.
	 */
	public static synchronized String buildRandomString(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length只能是正整数!");
		}
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (random == null) {
			return null;
		}
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < length; i++) {
			ret.append(CHAR_RANDOMS[random.nextInt(CHAR_RANDOMS.length)]);
		}
		random = null;
		return ret.toString();
	}

	/**
	 * 生成随机字符串. 随机字符串的内容包含[1-9 A-Z a-z]的字符.
	 * 
	 * @param min 必须为正整数 随机字符串的最小长度
	 * @param max 必须为正整数 随机字符串的最大长度
	 * @return 随机字符串.
	 */
	public static synchronized String buildRandomString(int min, int max) {
		if (min <= 0) {
			throw new IllegalArgumentException("Min 只能是正整数!");
		} else if (max <= 0) {
			throw new IllegalArgumentException("Max 只能是正整数!");
		} else if (min > max) {
			throw new IllegalArgumentException("Min 必须小于或等于 Max!");
		}
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (random == null) {
			return null;
		}
		int length = random.nextInt(max - min + 1) + min;
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < length; i++) {
			ret.append(CHAR_RANDOMS[random.nextInt(CHAR_RANDOMS.length)]);
		}
		random = null;
		return ret.toString();
	}

	/**
	 * 截取固定超出长度,以"..."结尾
	 * 
	 * @param str 需要截图的string
	 * @param length 长度
	 */
	public static String subStringLength(String str, int length) {
		if (!isEmpty(str) && str.length() > length) {
			str = str.substring(0, length) + "...";
		}
		return str;
	}	
	
	/**
	 * 将html占位符转化为正常字符
	 * @param str
	 * @return
	 */
	public static String replaceHtmlCharacters(String str){
		String inputstr = str;
		String temptext = "";
		if(null != inputstr && !"".equals(inputstr)){
			if(inputstr.indexOf("&amp;")>-1){
				inputstr=inputstr.replaceAll("&amp;","&"); 
			}
			if(inputstr.indexOf("&lt;")>-1){
				inputstr=inputstr.replaceAll("&lt;","<"); 
			}
			if(inputstr.indexOf("&gt;")>-1){
				inputstr=inputstr.replaceAll("&gt;",">"); 
			}
			if(inputstr.indexOf("&apos;")>-1){
				inputstr=inputstr.replaceAll("&apos;","'");  
			}
			if(inputstr.indexOf("&quot;")>-1){
				inputstr=inputstr.replaceAll("&quot;","\""); 
			}
			if(inputstr.indexOf("&#034;")>-1){
				inputstr=inputstr.replaceAll("&#034;","\"");  
			}
			temptext =  inputstr;
		}
		return temptext;
	}
}