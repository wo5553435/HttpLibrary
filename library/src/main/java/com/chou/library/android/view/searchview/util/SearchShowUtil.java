package com.chou.library.android.view.searchview.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.chou.library.android.okhttp.BaseVo;
import com.chou.library.android.okhttp.HttpUtils;
import com.chou.library.android.okhttp.callback.MyRequestCallback;
import com.chou.library.android.view.searchview.SearchDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * 这个类是用来封装fragment的动作的 我的想法是所有的逻辑操作都在这里
 * 主要是在搜索结果和本地已存结果中切换展示
 * Created by win7 on 2017-05-19.
 */

public class SearchShowUtil {
    private SearchDialog dialog;
    private List<String> strs_alldata;
    private List<String> currentdata;
    private Call call;
    private boolean isDebug = true;
    List<String> noresult;

    public SearchShowUtil(SearchDialog instance) {
        this.dialog = instance;
        strs_alldata = new ArrayList<>();
        currentdata = new ArrayList<>();
        noresult = new ArrayList<>();
        noresult.add("无搜索结果");
        if (isDebug) {
            strs_alldata.add("111");
            strs_alldata.add("112");
            strs_alldata.add("113");
            strs_alldata.add("114");
            strs_alldata.add("115");
            strs_alldata.add("121");
            strs_alldata.add("122");
            strs_alldata.add("123");
            strs_alldata.add("131");
            strs_alldata.add("132");
            strs_alldata.add("133");
            strs_alldata.add("134");
            strs_alldata.add("141");
            strs_alldata.add("142");
            strs_alldata.add("143");
            strs_alldata.add("144");

        }
    }

    public void ShowProgress() {
        dialog.ShowResult(currentdata);
    }

    /**
     *
     * 这个方法是用来获取全部内容，此方法会在前几个字符出现时调用，本质上是保存64-128个左右的预留关键词
     */
    public void GetAllStrResult(final String url, final HashMap<String, String> map) {
                    //理论上这里可以用switchMap 这样就不用单线程取消前一次请求了
        getFillStr().flatMap(new Function<String, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(final String s) throws Exception {

                return Observable.create(new ObservableOnSubscribe<List<String>>() {
                    @Override
                    public void subscribe(final ObservableEmitter<List<String>> e) throws Exception {
                        HttpUtils.getInstance().CancelAsynQue();//放弃前一次请求
                        Fliter(s);
                        if (currentdata.size() != 0) {//当数组中
                            e.onNext(currentdata);
                        } else{
                            HttpUtils.getInstance().PostSync(url, map, true, new MyRequestCallback<BaseVo>(BaseVo.class) {
                                @Override
                                public void OnSuccess(String result, String introduction) {

                                }

                                @Override
                                public void OnSuccess(BaseVo baseVo) {

                                }

                                @Override
                                public void OnSuccess(ArrayList<BaseVo> t) {
                                    if (strs_alldata != null) strs_alldata.clear();
                                    if (currentdata != null) currentdata.clear();
                                    for (int i = 0; i < t.size(); i++) {
                                        strs_alldata.add(t.toString());//这里修改成正确
                                        currentdata.add(t.toString());
                                        if (strs_alldata.size() > 63) break;
                                    }
                                    e.onNext(currentdata);
                                }

                                @Override
                                public void onFailure(String errorCode, String errorMsg) {
                                    e.onNext(noresult);
                                }
                            });
                            //这个地方我以为在doonsub时候每次都会调用 然后并不是
                            dialog.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.ShowWaitDialog(true);
                                    dialog.ShowResultText("搜索中...");
                                }
                            });
                        }
                    }
                });
            }

        }).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<String> value) {
                        if (value == null) {
                            dialog.ShowWaitDialog(true);
                            dialog.ShowResultText("搜索失败!请检查网络");
                        } else
                            dialog.ShowResult(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 这个方法是用来无缓存的请求 完全只是延迟异步走网络请求
     */
    public void SearchAllWordBykey(final String url, final HashMap<String, String> map){
        getFillStr().switchMap(new Function<String, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<List<String>>() {
                    @Override
                    public void subscribe(final ObservableEmitter<List<String>> e) throws Exception {
                        Fliter(s);
                        if (currentdata.size() != 0) {//当数组中
                            e.onNext(currentdata);
                        } else
                        HttpUtils.getInstance().Post(url, map, true, new MyRequestCallback<BaseVo>(BaseVo.class){
                            @Override
                            public void OnSuccess(String result, String introduction) {

                            }

                            @Override
                            public void OnSuccess(BaseVo baseVo) {

                            }

                            @Override
                            public void OnSuccess(ArrayList<BaseVo> t) {
                                if (strs_alldata != null) strs_alldata.clear();
                                if (currentdata != null) currentdata.clear();
                                for (int i = 0; i < t.size(); i++) {
                                    strs_alldata.add(t.toString());//这里修改成正确
                                    currentdata.add(t.toString());
                                    if (strs_alldata.size() > 63) break;
                                }
                                e.onNext(currentdata);
                            }

                            @Override
                            public void onFailure(String errorCode, String errorMsg) {
                                e.onNext(noresult);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        dialog.ShowWaitDialog(true);
                        dialog.ShowResultText("搜索中...");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread());
            }
        }).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> value) {
                if (value == null) {
                    dialog.ShowWaitDialog(true);
                    dialog.ShowResultText("搜索失败!请检查网络");
                } else
                    dialog.ShowResult(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void Fliter(String key) {
        currentdata.clear();
        if (key.length() != 0)
            for (int i = 0; i < strs_alldata.size(); i++) {
                if (strs_alldata.get(i).contains(key))
                    currentdata.add(strs_alldata.get(i));
                if (currentdata.size() > 5) break;
            }
    }

    private Observable<String> getFillStr() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> e) throws Exception {
                        dialog.etSearchKeyword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if (editable != null)
                                    e.onNext(editable.toString());
                            }
                        });
                    }
                });
            }
        });
    }


    public interface ReqCallBack {
        void success();

        void fail();
    }
}
