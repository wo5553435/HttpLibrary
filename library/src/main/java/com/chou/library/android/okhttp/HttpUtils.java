package com.chou.library.android.okhttp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.chou.library.android.okhttp.callback.ProgressCallBack;
import com.chou.library.android.okhttp.https.HttpsManager;
import com.chou.library.android.okhttp.https.MyTrustManager;
import com.chou.library.android.okhttp.https.SSLParams;
import com.chou.library.android.okhttp.https.UnSafeTrustManager;
import com.chou.library.util.EncryptUtil;
import com.chou.library.util.FileUtil;
import com.chou.library.util.Logs;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.chou.library.android.okhttp.callback.PollingCallback;
import com.chou.library.android.okhttp.callback.filecallback.MyFileRequestCallback;
import com.chou.library.android.okhttp.callback.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.chou.library.android.okhttp.https.HttpsManager.chooseTrustManager;

/**
 * 这些接口都是一次性的 所以 在归类上 后期我会添加针对于单一call的请求 目前只添加简单请求的单一请求，请求队列将在后期有时间开放
 * 本身因为okhttp的request在enqueue中就有线程池（因为当初在看到enqueue隐约感觉一定有优先级设置 在源码中AsyncCall便有priority）
 * 但本身并没有开放api，总觉得在线程池外再包线程池有点浪费。
 * Created by win7 on 2016/9/13.
 */

public class HttpUtils {


    private static HttpUtils instance;

    private OkHttpClient mOkHttpClient;

    private Call callinstance;

    private static Map<String, String> mParamsmap;

    private InputStream is_certificates[] = null;

    private ExecutorService servicepool;//线程池执行者

    private boolean isZip = true;//是否压缩文件
    private boolean isUsepool = false;

    private final int MAX_POOLSIZE = 10;//线程池大小

    private String filename = "";
    //    private ArrayList<Disposable> allSub;
    private HashMap<Integer, Call> callpools = new HashMap<>();//请求池  暂时会在
    private boolean isuseSingle = false;//关于请求池的开启的标志

    private HttpUtils() {
        if (mOkHttpClient == null) {
            SSLParams sslParams = getSslSocketFactory(is_certificates, null, null);//  信任指定证书
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .hostnameVerifier(new HostnameVerifier()//
                    {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;//信任所有域名
                        }
                    })
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .build();
            // allSub=new ArrayList<>();

        }
        if (isUsepool) {
            servicepool = Executors.newFixedThreadPool(MAX_POOLSIZE);

        }
    }


    public void getInputStream(Context context) {
    }

    public static HttpUtils getInstance() {
        if (instance == null)
            instance = new HttpUtils();
        return instance;
    }

    /**
     * 马德让加的接口 全部要加lgcode（曾经出现没删除问题）
     *
     * @param Paramsmap
     */
    public static void SetParamsMap(Map<String, String> Paramsmap) {
        mParamsmap = Paramsmap;
    }

//    /**
//     * 新版post带参请求,这里需要注意，boolean的参数是指需不需要直接返回data中的数据，当为true时返回的不是外包msg和error参数的vo类
//     * @param activity
//     * @param url
//     * @param params
//     * @param callback
//     * @param objectflag 是否指定结果为object
//     */
//    public void Post(final Activity activity, String url, Map<String, String> params, final ResponseCallback callback, final boolean objectflag){
//        FormBody.Builder formBodyBuilder = new FormBody.Builder();
//        if (params != null && params.size() > 0) {
//            Set<Map.Entry<String, String>> entrySet = params.entrySet();
//            for (Map.Entry<String, String> entry : entrySet) {
//                formBodyBuilder.add(entry.getKey(), entry.getValue());
//            }
//        }
//        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
//        Call call = mOkHttpClient.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, final IOException e) {
//                if(!activity.isFinishing())
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Logs.e("onFailure","in okhttp");
//                            if(e instanceof SocketTimeoutException){
//                                callback.OnFail("-888","网络连接超时，请稍后重试");
//                            }else{
//                                callback.OnFail("-900",e.getMessage());
//                            }
//                        }
//                    });
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                final String str = response.body().string();
//                if(!activity.isFinishing())
//
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Logs.i("onResponse", str);
//                            if(response.isSuccessful())
//                                if(objectflag){
//                                }else
//                                callback.OnSuccess(str);
//                            else
//                                callback.OnFail(""+response.code(),response.message());
//                        }
//                    });
//
//            }
//
//        });
//       /* try {
//            return call.execute().body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;*/
//    }

    /**
     * 请注意 这里的取消只是在同步的时候取消
     */
    public void CancelAsynQue() {
        Logs.e("直接打断单例请求", "" + isRequestSync());
        if (callinstance != null) callinstance.cancel();
    }

    public boolean isRequestSync() {
        return callinstance != null ? callinstance.isExecuted() : false;
    }


    /**
     * 特殊超时时间请求
     *
     * @param activity
     * @param url
     * @param timeout
     * @param params
     * @param isEncrypt
     * @param callback
     */
    public void Post(final Activity activity, String url, int timeout, Map<String, String> params, boolean isEncrypt, final ResponseCallback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        JSONObject jsonObject = new JSONObject();
        if (isEncrypt) {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    try {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //formBodyBuilder.add(entry.getKey(), entry.getValue());
                formBodyBuilder.add("pdvs", EncryptUtil.getEncryptValue(jsonObject.toString()));
                formBodyBuilder.add("sign", EncryptUtil.getEncryptSign(jsonObject.toString()));
            }
        } else {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        SSLParams sslParams = getSslSocketFactory(is_certificates, null, null);//  信任指定证书
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier()//
                {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;//信任所有域名
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();

        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (activity != null && !activity.isFinishing())
                    activity.runOnUiThread(new Runnable() {//恕我愚钝，本在旧框架的问题，在返回后有些耗时操作（gson）也放到了主线程，也不敢加上Rxjava，智只能祈祷不要太耗时
                        @Override
                        public void run() {
                            Logs.e("onFailure", "in okhttp");
                            if (e instanceof SocketTimeoutException) {
                                callback.OnFail("-888", "网络连接超时，请稍后重试");
                            } else if (e instanceof ConnectException) {
                                callback.OnFail("-800", "网络连接错误，请稍后重试");
                            } else if (e instanceof UnknownHostException) {
                                callback.OnFail("-808", "无法连接到网络");
                            } else {
                                callback.OnFail("-900", e.getMessage());
                            }
                        }
                    });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String str = response.body().string();
                if (activity != null && !activity.isFinishing())

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Logs.i("onResponse", str);
                            if (response.isSuccessful())
                                callback.OnSuccess(str);
                            else
                                callback.OnFail("" + response.code(), response.message());
                        }
                    });

            }

        });
    }


    /**
     * 兼容老版本的post请求，返回为字符串类型
     *
     * @param activity
     * @param url
     * @param params
     * @param callback
     * @param isEncrypt 是否加密
     */
    public void Post(final Activity activity, String url, Map<String, String> params, boolean isEncrypt, final ResponseCallback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        JSONObject jsonObject = new JSONObject();
        if (isEncrypt) {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    try {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //formBodyBuilder.add(entry.getKey(), entry.getValue());
                formBodyBuilder.add("pdvs", EncryptUtil.getEncryptValue(jsonObject.toString()));
                formBodyBuilder.add("sign", EncryptUtil.getEncryptSign(jsonObject.toString()));
            }
        } else {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (activity != null && !activity.isFinishing())
                    activity.runOnUiThread(new Runnable() {//恕我愚钝，本在旧框架的问题，在返回后有些耗时操作（gson）也放到了主线程，也不敢加上Rxjava，智只能祈祷不要太耗时
                        @Override
                        public void run() {
                            Logs.e("onFailure", "in okhttp");
                            if (e instanceof SocketTimeoutException) {
                                callback.OnFail("-888", "网络连接超时，请稍后重试");
                            } else if (e instanceof ConnectException) {
                                callback.OnFail("-800", "网络连接错误，请稍后重试");
                            } else if (e instanceof UnknownHostException) {
                                callback.OnFail("-808", "无法连接到网络");
                            } else {
                                callback.OnFail("-900", e.getMessage());
                            }
                        }
                    });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String str = response.body().string();
                if (activity != null && !activity.isFinishing())

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Logs.i("onResponse", str);
                            if (response.isSuccessful())
                                callback.OnSuccess(str);
                            else
                                callback.OnFail("" + response.code(), response.message());
                        }
                    });

            }

        });


    }


    public void Post(final int flag, final Activity activity, String url, Map<String, String> params, final ResponseCallback callback) {
        if (flag > 0) {
            if (callpools.containsKey(flag) && !callpools.get(flag).isCanceled()) {
                callpools.get(flag).cancel();
            }
        }
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
            if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                for (String key : mParamsmap.keySet()) {
                    formBodyBuilder.add(key, mParamsmap.get(key));
                }
            }
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        Call call = mOkHttpClient.newCall(request);
        if (flag > 0) callpools.put(flag, call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (activity != null && !activity.isFinishing())
                    activity.runOnUiThread(new Runnable() {//恕我愚钝，本在旧框架的问题，在返回后有些耗时操作（gson）也放到了主线程，也不敢加上Rxjava，智只能祈祷不要太耗时
                        @Override
                        public void run() {
                            Logs.e("onFailure", "in okhttp");
                            if (e instanceof SocketTimeoutException) {
                                callback.OnFail("-888", "网络连接超时，请稍候再试");
                            } else if (e instanceof ConnectException) {
                                callback.OnFail("-800", "网络连接错误，请稍候再试");
                            } else if (e instanceof UnknownHostException) {
                                callback.OnFail("-800", "网络连接错误，请稍候再试");
                            } else if (e instanceof UnknownHostException) {
                                callback.OnFail("-888", "无法连接到服务器,请稍候再试");
                            } else {
                                callback.OnFail("-900", e.getMessage());
                            }
                        }
                    });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String str = response.body().string();
                if (activity != null && !activity.isFinishing())

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Logs.i("onResponse", str);
                            if (response.isSuccessful())
                                callback.OnSuccess(str);
                            else
                                callback.OnFail("" + response.code(), response.message());
                        }
                    });

            }

        });
    }


    /**
     * 兼容老版本的post请求，返回为字符串类型
     *
     * @param activity
     * @param url
     * @param params
     * @param callback
     */
    public void Post(final Activity activity, String url, Map<String, String> params, final ResponseCallback callback) {
        Post(0, activity, url, params, callback);
    }


    /**
     * 由post带标识位的请求所记录
     *
     * @param flag
     */
    public void CancelPost(int flag) {
        if (flag != 0) {
            if (callpools.containsKey(flag) && !callpools.get(flag).isCanceled())
                callpools.get(flag).cancel();
        } else {//取消全部
//            for(int index:callpools.keySet()){
//                if(!callpools.get(index).isCanceled())
//                    callpools.get(index).cancel();
//            }
        }
    }

    /**
     * 说实话 我懒的写同步get请求，不放formbody他就是get请求
     */
    public void Get() {

    }


    //测试apk http://dl.wandoujia.com/files/jupiter/latest/wandoujia-web_seo_baidu_homepage.apk
    //
    /**
     * 无参下载文件
     *
     * @param fileUrl     文件url
     * @param destFileDir 存储目标目录
     */
    public <T> void downLoadFile(String fileUrl, final String destFileDir, String filename, final MyFileRequestCallback<T> callBack) {
        //final String fileName = MD5.encode(fileUrl);
        final File file1 = new File(destFileDir);
        if (!file1.exists()) {
            file1.mkdir();
        }
        long downloadLength = 0;   //记录已经下载的文件长度
        final File file = new File(file1.getAbsolutePath() + "/" + filename);
        this.filename = filename;
        if (file.exists()) {//防止未下载完 的包 直接删除重新下 断点续传有风险哦
            file.delete();
               /* downloadLength=file.length();
                if(getContentLength(fileUrl)==downloadLength){
                    callBack.OnSuccess(file);
                    return;
                }else{
                    final Request request = new Request.Builder().url(fileUrl).addHeader("Accept-Encoding", "identity").addHeader("RANGE","bytes="+downloadLength+"-").build();
                }*/
        }


        final Request request = new Request.Builder().url(fileUrl).addHeader("Accept-Encoding", "identity").build();
        //很坑啊 okhttp默认的接受模式 是gzip压缩过的 所以拿不到contentlenght
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logs.e("onFailure", e.toString());

                callBack.OnFail("onFailure", "下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    long total_cut = total / 50;
                    long currentpercent = 0;

                    Log.e("request", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        // Log.e("onResponse", "current------>" + current);
                        if (current > currentpercent) {//当有进度更新超过2%时才触发回调
                            currentpercent = currentpercent + total_cut;
                            callBack.onPogress(total, current);
                        }
                    }
                    fos.flush();
                    callBack.OnSuccess(file);
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                    callBack.OnFail("下载失败",e.getMessage());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("exception", e.toString());
                    }
                }
            }
        });
    }


    /**
     * 事先得到下载内容的大小
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).addHeader("Accept-Encoding", "identity").build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static SSLParams getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        SSLParams sslParams = new SSLParams();
        try {
            TrustManager[] trustManagers = HttpsManager.prepareTrustManager(certificates);
            KeyManager[] keyManagers = HttpsManager.prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = null;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        }
    }


    /**
     * 设置证书
     *
     * @param certificates
     */
    public void setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            // mOkHttpClient.sslSocketFactory().setSslSocketFactory(sslContext.getSocketFactory());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 接下来的部分是 结合 rxjava2 写的请求 本身可以看到在上述的post请求中 引用到了activity，当然不知名高并发的情况下会出现内存泄漏问题
     * 然后我并不知道rxjava源码实现切换线程原理，就从劫持activity上面来说，rxjava不用引用activity对象,但要区别回调的地方还存在不
     */
    public void Post(String url, Map<String, String> params, boolean isEncode, final ResponseCallback callback) {
        Post(0, url, params, false, isEncode, callback);
    }


    /**
     * 加入标记的请求
     *
     * @param url
     * @param params
     */

    public void Post(int flag, String url, Map<String, String> params, boolean isEncode, final ResponseCallback callback) {
        if (flag != 0) {//map put操作前的检查
            if (callpools.containsKey(flag)) {//之前的请求如果没完成 则一定会cancel，仅当此模式下
                if (!callpools.get(flag).isCanceled()) callpools.get(flag).cancel();
            }

        }
    }


    /**
     * 此方法是无脑轮询 当请求500或者404错误时候就会请求
     *
     * @param url
     * @param params
     * @param retrytime
     * @param retrydelay
     * @param isEncode
     * @param callback
     */
    public void PostAndRetry(final String url, final Map<String, String> params, final int retrytime, final int retrydelay, final boolean isEncode, final ResponseCallback callback) {
        Post(retrydelay, url, params, isEncode).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.zipWith(Observable.range(1, retrytime + 1), new BiFunction<Throwable, Integer, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable, Integer integer) throws Exception {
                        Logs.e("稍候进行第" + integer + "次轮询", "---");
//                        if(integer==(retrytime+1)) //为什么这里不行呢 个人觉得因为在拆解拼装后的observable中 已经无法操作整体组成元素来控制 单一组成部分 ，也就是说 其实这里有三个 observable 操作上控制的只是无关紧要的
                        //第三个observable，这个observable意义就是用来把时间并在一起
//                            Observable.error(throwable);
                        return integer;
                    }
                }) /*.delay(retrydelay,TimeUnit.SECONDS)*/
                        .flatMap(new Function<Integer, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Integer retryCount) throws Exception {
                                if (retryCount == retrytime + 1) {//轮询次数完成，直接进error
                                    return Observable.error(new ConnectException());
                                } else
                                    return Observable.timer(retrydelay, TimeUnit.SECONDS);
                            }
                        })
                        ;

            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback instanceof ProgressCallBack && callback != null)
                            ((ProgressCallBack) callback).OnStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(Sub(callback));
    }

    /**
     * 此方法是条件判断轮询 主要未接受到指定值的时做归为错误后重复
     *
     * @param url
     * @param params
     * @param retrytime
     * @param isEncode
     * @param callback
     */
    public void PostAndRetry(final String url, final Map<String, String> params, final int retrytime, final boolean isEncode, final ResponseCallback callback) {
        PostByStatu(5, url, params, isEncode, true)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.zipWith(Observable.range(1, retrytime + 1), new BiFunction<Throwable, Integer, Integer>() {
                            @Override
                            public Integer apply(Throwable throwable, Integer integer) throws Exception {
                                Logs.e("第" + integer + "次轮询", "---");
                                return integer;
                            }
                        }).flatMap(new Function<Integer, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Integer retryCount) throws Exception {
                                if (retryCount == retrytime + 1) {//轮询次数完成，直接进error
                                    return Observable.error(new ConnectException());
                                } else
                                    return Observable.timer(5, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback instanceof ProgressCallBack && callback != null)
                            ((ProgressCallBack) callback).OnStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(Sub(callback));
    }


    /**
     * 此方法是条件判断轮询 主要未接受到指定值的时做归为错误后重复
     *
     * @param url
     * @param params
     * @param retrytime
     * @param isEncode
     * @param callback
     */
    public Observable<String> PostAndRepeat(final String url, final Map<String, String> params, final int retrytime, final boolean isEncode, final ResponseCallback callback) {
        Observable<String> observable = PostByStatu(5, url, params, isEncode, false).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return objectObservable.zipWith(Observable.range(1, retrytime + 1), new BiFunction<Object, Integer, Integer>() {
                    @Override
                    public Integer apply(Object object, Integer integer) throws Exception {
                        Logs.e("第" + integer + "次轮询", "---");
                        return integer;
                    }
                }).flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer retryCount) throws Exception {
                        if (retryCount == retrytime + 1) {//轮询次数完成，直接进error
                            return Observable.error(new ConnectException());
                        } else
                            return Observable.timer(5, TimeUnit.SECONDS);
                    }
                });

            }
        })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback instanceof ProgressCallBack && callback != null)
                            ((ProgressCallBack) callback).OnStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());


        observable.subscribe(Sub(callback));
        return observable;
    }

    /**
     * 对于请求的任务是单线程 意思是这个请求会在call中已经取消 不定期 我会添加队列模式
     */
    public void PostSync(String url, Map<String, String> params, boolean isEncrypt, boolean addqueue, final ResponseCallback callback) {
        PostSync(url, params, isEncrypt, callback);
    }

    public void PostSync(String url, Map<String, String> params, boolean isEncrypt, final ResponseCallback callback) {
        CancelAsynQue();
        PostSync(url, params, isEncrypt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("onSubscribe", "--" + d);
                        // if(!allSub.contains(d))
                        //     allSub.add(d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e("onNext", "--" + value);
                        callback.OnSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "--");
                        if (e instanceof SocketTimeoutException) {
                            callback.OnFail("-888", "网络连接超时，请稍候再试");
                        } else if (e instanceof ConnectException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-888", "无法连接到服务器,请稍候再试");
                        } else {
                            callback.OnFail("-900", e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        callback.OnFinsh();
                    }
                });
    }


    /**
     * 这个方法并不是取消请求，而是取消了订阅事件
     * 建议在activity的ondestroy中使用，取消所有因activity后退泄漏问题或者 在回调中先做activity.isfinish判断
     */
    private void CancelAllRequest() {
//        for (int i = 0; i < allSub.size(); i++) {
//            Disposable d= allSub.get(i);
//            if(!d.isDisposed()) d.dispose();
//        }
    }


    //无activity调用,此处需要注意的是 该处回调可以在子线程操作，不要忘记切换后再进行UI操作，这个方法适用于完全无ui操作的类型，但你要注意解绑操作
    public void Post(int flag, String url, Map<String, String> params, boolean callbackinIo, boolean isEncode, final ResponseCallback callback) {
        Post((flag == 0) ? false : true, flag, url, params, isEncode)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (callback instanceof ProgressCallBack && callback != null)
                            ((ProgressCallBack) callback).OnStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(callbackinIo ? Schedulers.io() : AndroidSchedulers.mainThread())
                .subscribe(Sub(callback));
    }


    /**
     * 上传 单张图片  名称写死的上传  (仅做面向过程使用)
     *
     * @param filepath
     * @param upurl
     * @param params
     * @param callback
     */
    public void UpLoadFile(String filepath, String upurl, HashMap<String, String> params, final ResponseCallback callback) {
        Post(upurl, filepath, params, true)
        /*        .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if(callback instanceof ProgressCallBack &&callback!=null)
                            ((ProgressCallBack) callback).OnStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())*/
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("onSubscribe", "--" + d);
//                        if(!allSub.contains(d))
//                            allSub.add(d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e("onNext", "--" + value);
                        callback.OnSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "--");
                        if (e instanceof SocketTimeoutException) {
                            callback.OnFail("-888", "网络连接超时，请稍候再试");
                        } else if (e instanceof ConnectException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-888", "无法连接到服务器,请稍候再试");
                        } else {
                            callback.OnFail("-900", e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        callback.OnFinsh();
                    }
                });
    }

    /**
     * 上传多张图片(不想写加密了) 如果你接着用 记得自己加密参数后再输入
     *
     * @param filepaths
     * @param upurl
     * @param params
     * @param callback
     */
    public void upLoadImages(List<String> filepaths, String file_upkey, String upurl, HashMap<String, String> params, final ResponseCallback callback) {
        Post(upurl,file_upkey, filepaths, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(Sub(callback));
    }

    /**
     * 上传单张图片（不仅仅是图片 文件也可以）
     *
     * @param filepath
     * @param upurl
     * @param file_upkey 这是和后台约定的文件key
     * @param params
     * @param callback
     */
    public void upLoadImage(String filepath,String file_upkey, String upurl, HashMap<String, String> params, final ResponseCallback callback) {
        upLoadImages(Arrays.asList(filepath),file_upkey, upurl, params, callback);
    }

    /**
     * 用来压缩图片
     * @param isZip
     * @return
     */
    public HttpUtils setUpLoadZipEnable(boolean isZip){
        this.isZip=isZip;
        return this;
    }

    /**
     * 【拼装参数请求体
     *
     * @param params
     * @param isEncode
     * @return
     */
    private FormBody.Builder Encode(Map<String, String> params, boolean isEncode) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        JSONObject jsonObject = new JSONObject();
        if (isEncode) {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    try {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                formBodyBuilder.add("pdvs", EncryptUtil.getEncryptValue(jsonObject.toString()));
                formBodyBuilder.add("sign", EncryptUtil.getEncryptSign(jsonObject.toString()));
            }
        } else {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
            }
        }
        return formBodyBuilder;
    }

    private MultipartBody.Builder Encode(Map<String, String> params, List<String> images,String ulfile_key) {
        MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder();
        formBodyBuilder.setType(MultipartBody.FORM);
        if(params!=null){
            for(String key:params.keySet()){
                formBodyBuilder.addFormDataPart(key,params.get(key));
            }
        }
        if(images!=null){
            for (int i = 0; i < images.size(); i++) {
                String filepath=images.get(i);
                File file = new File(filepath);
                if (file.exists() && file.length() > 1 * 1024 * 1024 && isZip) {//默认压缩
                    file = FileUtil.compressImage(filepath);
                } else {
                    return null;
                }
                if(file!=null){
                    RequestBody fileBody = RequestBody.create(MediaType.parse(/*"image/png"*/"application/octet-stream"), file);
                    formBodyBuilder.addFormDataPart(ulfile_key,file.getName(),fileBody);
                }
            }
        }

        return formBodyBuilder;
    }

    /**
     * 【拼装复合参数请求体
     *
     * @param params
     * @param isEncode
     * @return
     */

    private MultipartBody.Builder Encode(Map<String, String> params, String filepath, boolean isZip, boolean isEncode) {
        File file = new File(filepath);
        if (file.exists() && file.length() > 1 * 1024 * 1024 && isZip) {
            file = FileUtil.compressImage(filepath);
        } else {
            return null;
        }
        RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        String file1Name = "upload.png";//注意  这里我偷懒写法
        MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder();
        JSONObject jsonObject = new JSONObject();
        if (!isEncode) {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
        } else {
            if (params != null && params.size() > 0) {
                if (mParamsmap != null) {//覆盖全局请求事先约定的参数
                    params.putAll(mParamsmap);
                }
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    try {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //formBodyBuilder.add(entry.getKey(), entry.getValue());
                formBodyBuilder.addFormDataPart("pdvs", EncryptUtil.getEncryptValue(jsonObject.toString()));
                formBodyBuilder.addFormDataPart("sign", EncryptUtil.getEncryptSign(jsonObject.toString()));
            }
        }
        formBodyBuilder.setType(MultipartBody.FORM)//这里我写死了 文件参数名 要用记得放开这里
                .addFormDataPart("uploadfile", file1Name, fileBody1)
                .build();
        return formBodyBuilder;
    }

    private MultipartBody.Builder Encode(Map<String, String> params, String filepath, boolean isEncode) {
        return Encode(params, filepath, true, isEncode);
    }


    /**
     * 正常请求体带文件
     *
     * @param url
     * @param filepath
     * @param params
     * @param encode
     * @return
     */
    private Observable<String> Post(final String url, final String filepath, final Map<String, String> params, final boolean encode) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                Request request = new Request.Builder().url(url).post(Encode(params, filepath, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        }
                        subscriber.onComplete();
                    }
                });
            }
        });
    }

    /**
     * 上传多张图片
     * @param url
     * @param file_upkey
     * @param filepaths
     * @param params
     * @return
     */
    public Observable<String> Post(final String url, final String file_upkey, final List<String> filepaths, final Map<String, String> params) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                Request request = new Request.Builder().url(url).post(Encode(params, filepaths,file_upkey).build()).build();
                Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        }
                        subscriber.onComplete();
                    }
                });
            }
        });
    }


    /**
     * 加上延迟的请求（后期我会优化的）用于判断非200情况
     *
     * @param url
     * @param delay
     * @param params
     * @param encode
     * @return
     */
    private Observable<String> Post(int delay, final String url, final Map<String, String> params, final boolean encode) {
        return Observable.timer(delay, TimeUnit.SECONDS).create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {//只要不是200 全是归错
                            subscriber.onNext(response.body().string());
                        } else {
                            subscriber.onError(new IOException());//这里我的ErrorInfoVo没写完
                        }
                        subscriber.onComplete();
                    }
                });

            }
        });
    }


    /**
     * 这里是一个状态判断返回请求，当返回结果中包含的key的指定值后才判断为正确（功能和callback中的判断有重复了） 我无法在拓展和封装之间做平衡
     * 在此前提下
     *
     * @param delay
     * @param url
     * @param params
     * @param encode
     * @param isError 是否当做错误返回
     * @return
     */
    private Observable<String> PostByStatu(int delay, final String url, final Map<String, String> params, final boolean encode, final boolean isError) {
        return Observable.timer(delay, TimeUnit.SECONDS).create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {//200状态下 指定字段为预期值则当做完成
                            String result = response.body().string();
                            if (IsRightResult(result))
                                subscriber.onNext(result);
                            if (isError)
                                subscriber.onError(new IOException());
                            else subscriber.onComplete();//这个地方是用来判断入口回调的 repeat还是retry
                        } else {
                            if (isError)
                                subscriber.onError(new IOException());//这里我的ErrorInfoVo没写完
                            else subscriber.onComplete();
                        }
                        subscriber.onComplete();
                    }
                });

            }
        });
    }

    /**
     * 自定义返回类型错误
     *
     * @param result
     * @return
     */
    private boolean IsRightResult(String result) {
        JsonElement element = null;
        JsonObject jsonObject = null;
        element = new JsonParser().parse(result);
        if (element == null || element.isJsonNull()) return false;
        jsonObject = element.getAsJsonObject();

        if (jsonObject.has(ResponseParams.getKeyStatus())) {
            JsonElement errorCodeJsonElement = jsonObject.get(ResponseParams.getKeyStatus());
            String Statu = errorCodeJsonElement.getAsString();
            if (!"0".equals(Statu)) return false;
        }
        return true;
    }

    /**
     * 加上轮询的请求体
     *
     * @param url
     * @param times
     * @param timedelay
     * @param params
     * @param encode
     * @return
     */
    private Observable<String> Post(final String url, int times, int timedelay, final Map<String, String> params, final boolean encode) {
        return Observable.interval(0, timedelay, TimeUnit.SECONDS).take(times).create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        }
                        subscriber.onComplete();
                    }
                });

            }
        });
    }

//
//    public <T> Observable<T> PostRx(final String url, final Map<String,String>params, final boolean encode, RxResponseCallback<T> callback){
//        return Observable.create(new ObservableOnSubscribe<T>() {
//            @Override
//            public void subscribe(ObservableEmitter<T> e) throws Exception {
//                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
//                Call call = mOkHttpClient.newCall(request);
//
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        callback.onError(e);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            subscriber.onNext(response.body().string());
//                        }
//                        subscriber.onComplete();
//                    }
//                });
//            }
//        });
//    }

    /**
     * 正常请求体
     *
     * @param url
     * @param params
     * @param encode
     * @return
     */
    private Observable<String> Post(final boolean issingle, final int flag, final String url, final Map<String, String> params, final boolean encode) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);
                if (issingle) callpools.put(flag, call);//将正常请求放入map中
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        } else {
                            subscriber.onError(new IOException("Page Not Found!"));
                        }
                        subscriber.onComplete();
                    }
                });

            }
        });
    }

    private Observable<String> PostSync(final String url, final Map<String, String> params, final boolean encode) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                callinstance = mOkHttpClient.newCall(request);

                callinstance.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        }
                        subscriber.onComplete();
                    }
                });
            }
        });
    }

    /**
     * 这是自定义判断的订阅者方法，用于在repeat或retry中判断循环
     *
     * @param callback
     * @return
     */
    private Observer<String> Sub(final ResponseCallback callback) {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("onSubscribe", "--" + d);

                //  if(!allSub.contains(d)) allSub.add(d);
            }

            @Override
            public void onNext(String value) {
                Log.e("onNext", "--" + value);
                callback.OnSuccess(value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError", "--" + e.getMessage());
                if (e instanceof SocketTimeoutException) {
                    callback.OnFail("-888", "网络连接超时，请稍候再试");
                } else if (e instanceof ConnectException) {
                    callback.OnFail("-800", "网络连接错误，请稍候再试");
                } else if (e instanceof UnknownHostException) {
                    callback.OnFail("-800", "网络连接错误，请稍候再试");
                } else if (e instanceof UnknownHostException) {
                    callback.OnFail("-888", "无法连接到服务器,请稍候再试");
                } else {
                    callback.OnFail("-900", e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                callback.OnFinsh();
            }
        };
    }

    /**
     * 此接口用来做轮询的操作 只是轮询
     *
     * @param url
     * @param params
     * @param isEncode
     * @param callback
     */
    public void Polling(String url, Map<String, String> params, int times, boolean isEncode, final PollingCallback callback) {
        Post(url, times, 15, params, isEncode)//默认十五次
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("onSubscribe", "--" + d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e("onNext", "--" + value);
                        callback.OnSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", "--");
                        if (e instanceof SocketTimeoutException) {
                            callback.OnFail("-888", "网络连接超时，请稍候再试");
                        } else if (e instanceof ConnectException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-800", "网络连接错误，请稍候再试");
                        } else if (e instanceof UnknownHostException) {
                            callback.OnFail("-888", "无法连接到服务器,请稍候再试");
                        } else {
                            callback.OnFail("-900", e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        callback.OnFinsh();
                    }
                });
    }


    /**
     * 轮询请求接口，这里有个思路问题，我们需要在分清 定时和接受再请求的情况与区别，主体思想区别就是interval和takeutil 还有retrywhen
     *
     * @param url
     * @param params
     * @param encode
     * @return 被观察者的本体
     */
    private Observable<String> Poll(final String url, final Map<String, String> params, final boolean encode) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> subscriber) throws Exception {
                final Request request = new Request.Builder().url(url).post(Encode(params, encode).build()).build();
                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body().string());
                        }
                        subscriber.onComplete();
                    }
                });
            }
        });
    }


}
