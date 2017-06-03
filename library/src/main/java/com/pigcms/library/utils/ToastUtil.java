package com.pigcms.library.utils;

/**
 * Created by win7 on 2016/8/25.
 */

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast mToast;
    private static Context mContext;
    private static ToastUtil instance;

    public ToastUtil() {

    }


    public static ToastUtil getInstance(Context context) {
        if (mContext == null || mContext != context) {
            mContext = context;
            instance = new ToastUtil();
        }
        return instance;
    }

    public void showToast(String text) {
        if (((Activity) mContext).isFinishing()) {//当前activity关闭时，不弹框
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            //mToast.setText(text);
        }
        mToast.show();
    }


    public void cancelToast() {//此方法在onkeydown的返回中调用
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 小技巧：防止连续多次点击之后Toast多次触发重复显示
     * 这样的体验其实是不好的，因为也许用户是手抖了一下多点了几次，导致Toast就长时间关闭不掉了
     *
     * @param context
     * @param content 需要提示的内容
     */
    public static void showToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }
}
