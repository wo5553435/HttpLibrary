//package com.pigcms.library.android.http;
//
//
//import org.xutils.ex.HttpException;
//import org.xutils.http.RequestParams;
//
//import java.util.Map;
//
//public class DefaultHttpUtil extends HttpUtils {
//
//
//	public void Get( String url, final ResponseCallback callback) {
//		RequestParams params=new RequestParams(url);
//		new APPRestClient().getHttpClient().send(HttpMethod.GET,params,callback);
//
//	}
//
//	/**
//	 * 暂时只支持的string类型的参数输入
//	 * @param params 请求参数
//	 * @param callback 回调方法
//	 */
//	public void Post(Map<String, String> params,final ResponseCallback callback) {
//		RequestParams reParams = new RequestParams();
//		for(String param :params.keySet()){
//			reParams.addBodyParameter(param,params.get(param));
//		}
//
//		new APPRestClient().getHttpClient().send(HttpMethod.POST, reParams,callback);
//	}
//
//}
