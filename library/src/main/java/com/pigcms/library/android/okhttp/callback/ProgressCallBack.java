package com.pigcms.library.android.okhttp.callback;

/**这个接口是给rxjava请求回调用的回调
 * Created by win7 on 2017-04-14.
 */

public interface ProgressCallBack extends ResponseCallback {
    void OnStart();//在原有的基础上添加开始结构，区别于rxjava的onstart （线程机制） 这个类我从来没有机会用过，如果以后你拓展了，记得在http的请求中 切ob
}
