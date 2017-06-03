package com.pigcms.library.android.okhttp.https;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by win7 on 2017-02-03.
 * https的ssl管理封装类
 */

public class SSLParams {
    public SSLSocketFactory sSLSocketFactory;//
    public X509TrustManager trustManager;
}
