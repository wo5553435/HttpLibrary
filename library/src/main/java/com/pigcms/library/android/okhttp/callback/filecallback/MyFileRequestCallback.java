package com.pigcms.library.android.okhttp.callback.filecallback;

import com.pigcms.library.android.okhttp.callback.ResponseCallback;

import java.io.File;

/**
 * Created by win7 on 2016-10-15.
 */

public abstract class MyFileRequestCallback<T> implements ResponseCallback {

    @Override
    public void OnSuccess(String classinfo) {
    }


    public abstract void OnSuccess(File file);

    public abstract  void onPogress(long total, long current);

    @Override
    public void OnFinsh() {

    }
}
