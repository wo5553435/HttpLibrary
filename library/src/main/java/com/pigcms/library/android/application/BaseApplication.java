package com.pigcms.library.android.application;

import android.app.Application;
import android.content.Intent;

//import org.xutils.x;

/**
 * Created by win7 on 2016/9/9.
 */

public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {

    @Override
    public void onCreate() {
        super.onCreate();
//        x.Ext.init(this);//Xutils初始化
//        x.Ext.setDebug(false);
       // Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        getCrushInfo(throwable);

        restartApplication();
    }

    /**
     * 这里的操作需要采集崩溃日志至服务器
     * @param throwable
     */
    public void getCrushInfo(Throwable throwable){
        throwable.printStackTrace();
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
