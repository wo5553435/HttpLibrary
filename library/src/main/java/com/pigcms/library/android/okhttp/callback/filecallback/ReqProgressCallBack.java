package com.pigcms.library.android.okhttp.callback.filecallback;

/**
 * Created by win7 on 2016-10-15.
 */

public interface ReqProgressCallBack<T> {
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}