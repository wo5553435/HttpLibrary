package com.chou.library.android.okhttp.callback;

/**这个接口没写完 理论上是要onfail的 ，但是轮询一般是不处理错误的，需要处理的是最后一次onfail
 * Created by win7 on 2017-04-15.
 */

public abstract class  PollingCallback implements ResponseCallback {
    private int currentcount=0;
    abstract void OnSuccess(int count ,String value);

    @Override
    public void OnSuccess(String classinfo){
        OnSuccess(currentcount,classinfo);
    }

}
