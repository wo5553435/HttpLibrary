package com.pigcms.library.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**这个fragment主要是配合viewpager来使用的，使用了懒加载
 * Created by win7 on 2017-03-30.
 */

public abstract class BaseVpFragment extends Fragment {
    protected boolean isVisible=false;
    protected boolean isInitView=false;
    protected boolean isFirstLoad=true;
    protected View convertView;
    private Activity activity;


    public abstract int getContentlayout();

    public abstract void initView();

    public abstract void initAction();

    public abstract void initData();
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible=true;
            LazyLoad();
        }else{
            isVisible=false;
        }
    }

    @Override
    public void onAttach(Context context) {
        activity= (Activity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        convertView=inflater.inflate(getContentlayout(),container,false);
        initView();
        isInitView=true;
        LazyLoad();
        return convertView;
    }


    private void LazyLoad(){
        if(!isFirstLoad||!isVisible||!isInitView){
            return;
        }
        initAction();
        initData();
        isFirstLoad=false;
    }
}
