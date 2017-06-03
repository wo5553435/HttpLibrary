package com.pigcms.library.android.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pigcms.library.utils.Logs;

import java.util.ArrayList;

/**
 * 该回调是基于普通版本的加强版，减少了代码冗余，但后台返回数据并不是很统一，故开放两个入口来规定返回值问题
 * @param <T>
 */
public abstract class MyRequestCallback<T> implements ResponseCallback {

	/*private final String rightstatue = "error";

	private final String rightmsg = "msg";

	private final String rightdata = "data";

	private final String right_statue = "0";*/

	// 当前json返回状态
	private String Statu = null, Message = "";

	// 网络连接错误
	private static final String ERROR_CODE_NET = "-900";
	// 无可信证书
	private static final String ERROR_CODE_NO_PEER_CER = "-901";

	// Json字符串转对象错误
	private static final String ERROR_CODE_FROM_JSON_TO_OBJECT = "-800";
	// Json字符串解析错误
	private static final String ERROR_CODE_JSON_PARSE = "-801";

	// 结果字段key不匹配
	private static final String ERROR_CODE_RESULT_KEY_NOT_CORRECT = "-700";
	// 请求超时
	private static final String ERROR_CODE_SESSIONTIMEOUT = "-888";
	// 空请求数据
	private static final String ERROR_CODE_NORESULT = "-101";

	public abstract  void OnSuccess(String result,String introduction);

	public abstract void OnSuccess(T t);
	
	public abstract void OnSuccess(ArrayList<T> t);

	public abstract void onFailure(String errorCode, String errorMsg);

	private Class<T> classofT;

	private T t;
	private boolean isOnlyString=false;

	public MyRequestCallback(Class<T> classOfT) {
		this.classofT = classOfT;
	}

	/**
	 * 是否是启动只返回string模式，可惜我懒得写两个回调去 ，这个模式很鸡肋，但是没法子,为了兼容老版本的实体类框架
	 * @param classofT 实体类
	 * @param isString string模式
     */
	public MyRequestCallback(Class<T> classofT,boolean isString){
		this(classofT);
		this.isOnlyString=true;
	}

	@Override
	public void OnSuccess(String responseinfo) {
		// TODO Auto-generated method stub
		String resultstring = responseinfo;
		Logs.e("result is", resultstring);
		if(isOnlyString){
			OnSuccess(resultstring,"just want string");
			return;
		}
		JsonElement entitystring = Conversion(resultstring);
		ResultManager<T> entity = new ResultManager<T>();
		if(entitystring  instanceof JsonObject){//返回结果为对象
			Logs.e("result",entitystring.getAsString());
		 	T result = entity.resolveEntity(classofT, entitystring.getAsString(), "返回接口");
		 OnSuccess(result);
		}else if(entitystring instanceof JsonArray){//返回对象为数组
			ArrayList<T> results=new ArrayList<T>();
			JsonArray resultarray=entitystring.getAsJsonArray();
			for(JsonElement resulte:resultarray){
				 ResultManager<T> entity2 = new ResultManager<T>();

				 T result = entity2.resolveEntity(classofT, resulte.toString(), "返回接口");
				 results.add(result);
			}

			OnSuccess(results);
		}else{
			OnSuccess(responseinfo,"onsuccesss");
		}
	}

	@Override
	public void OnFail(String arg0, String arg1) {
		// TODO Auto-generated method stub
		onFailure(arg0, arg1);
	}

	/**
	 * 判断是否返回所需的data 并在statu和message中赋值
	 * 
	 * @param responseString
	 * @return
	 */
	public JsonElement Conversion(String responseString) {

		Gson gson = new Gson();
		JsonObject jsonObject = null;
		JsonElement element = new JsonParser().parse(responseString);
		JsonArray jsonArrary = null;
		jsonObject = element.getAsJsonObject();

		if (jsonObject.has(ResponseParams.getKeyStatus())) {
			JsonElement errorCodeJsonElement = jsonObject.get(ResponseParams.getKeyStatus());
			Statu = errorCodeJsonElement.getAsString();
		}
		if (jsonObject.has(ResponseParams.getKeyMessage())) {
			JsonElement errorMsgJsonElement = jsonObject.get(ResponseParams.getKeyMessage());
			if (errorMsgJsonElement.isJsonNull()) {
				Message = "";
			} else {
				Message = errorMsgJsonElement.getAsString();
			}
		}
		if (Statu == null) {// 空结果
			OnFail(ERROR_CODE_NORESULT, "服务器返回异常");
			return null;
		} else if (!Statu.equals(ResponseParams.getStatusSuccess())) {// 错误结果
			OnFail(Statu, Message);
			return null;
		}
		
		

		 /*if (jsonObject != null) {// 其返回结果为object

			return jsonObject.get("data").getAsJsonObject();
		} 
		 if (jsonArrary != null) {// 其返回结果为list
			return jsonObject.get("data").getAsJsonArray();
		}*/
		return jsonObject.get(ResponseParams.getKeyData()) ;

	}

}
