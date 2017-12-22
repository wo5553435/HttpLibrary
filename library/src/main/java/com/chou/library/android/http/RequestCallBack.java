//package com.pigcms.library.android.http;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.pigcms.library.utils.Logs;
//
//import org.xutils.ex.HttpException;
//
//import java.util.ArrayList;
//
//public abstract class RequestCallBack<T> extends MyRequestCallback {
//
//    public RequestCallBack(Class classOfT) {
//        super(classOfT);
//    }
//
//    abstract  public void onStart();
//
//    abstract public void onLoading(long total, long current, boolean isUploading);
//
//    abstract  public void onFailure(HttpException ex);
//}
//
