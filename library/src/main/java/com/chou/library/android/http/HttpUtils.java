//package com.pigcms.library.android.http;
//
//import com.chou.library.android.capture.utils.Logs;
//
//import org.xutils.HttpManager;
//import org.xutils.common.Callback;
//import org.xutils.ex.HttpException;
//import org.xutils.http.RequestParams;
//import org.xutils.x;
//
//import java.util.List;
//
///**
// * Created by win7 on 2016/9/9.
// */
//
//public class HttpUtils {
//    protected HttpManager httpManager;
//
//    public HttpUtils(){
//        httpManager= x.http();
//    }
//    public void send(int method, RequestParams requestParams, final ResponseCallback callback){
//        switch (method){
//            case 1://get请求
//                requestParams.setConnectTimeout(10*1000);
//                httpManager.get(requestParams, new Callback.CommonCallback<String>() {
//
//                    @Override
//                    public void onSuccess(String result) {
//                        callback.OnSuccess(result);
//                    }
//
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//                        if (ex instanceof HttpException) { // 网络错误
//                            HttpException httpEx = (HttpException) ex;
//                            int responseCode = httpEx.getCode();
//                            String responseMsg = httpEx.getMessage();
//                            String errorResult = httpEx.getResult();
//                            // ...
//                            callback.OnFail(""+responseCode,responseMsg);
//                        } else { // 其他错误
//                            // ...我在回调中做了解析错误判断
//                           // callback.OnFail();
//                            Logs.e("ex error",""+ex.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(CancelledException cex) {
//                        Logs.e("onCancelled","---");
//                    }
//
//                    @Override
//                    public void onFinished() {
//                        Logs.e("onFinished","----");
//                    }
//                });
//                break;
//            case 0://post请求
//                requestParams.setConnectTimeout(10*1000);
//                httpManager.post(requestParams, new Callback.CommonCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        callback.OnSuccess(result);
//                    }
//
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//                        if (ex instanceof HttpException) { // 网络错误
//                            HttpException httpEx = (HttpException) ex;
//                            int responseCode = httpEx.getCode();
//                            String responseMsg = httpEx.getMessage();
//                            String errorResult = httpEx.getResult();
//                            // ...
//                            callback.OnFail(""+responseCode,responseMsg);
//                        } else { // 其他错误
//                            // ...我在回调中做了解析错误判断
//                            // callback.OnFail();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(CancelledException cex) {
//
//                    }
//
//                    @Override
//                    public void onFinished() {
//
//                    }
//                });
//                break;
//        }
//    }
//
//}
