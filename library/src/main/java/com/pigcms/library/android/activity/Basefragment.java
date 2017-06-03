package com.pigcms.library.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pigcms.library.utils.Logs;

//import butterknife.ButterKnife;
//import butterknife.Unbinder;

/**
 * Created by win7 on 2016/8/25.
 * 这个fragment是用fragmentmanage处理的 主要用的是manager的switch开关
 */

public abstract class Basefragment extends Fragment {//

    private static final String LOG_TAG = "YTBaseFragment";
    protected View contentView = null;
    protected BaseFragmentActivity activity = null;
//    private Unbinder unbinder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseFragmentActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(getContentLayout(), container, false);
//        unbinder= ButterKnife.bind(this,contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initAction();
        initData();
    }

	/*@Override//在以hide切换fragmetn中执行生命周期以外的活动
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
	}*/

    @Override
    public void onResume() {
        super.onResume();
        Logs.d(LOG_TAG, this.toString()+"registerKeyDownNotify");
        //activity.registerKeyDownNotify(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.d(LOG_TAG, this.toString()+"removeKeyDownNotify");
        //activity.removeKeyDownNotify(this);
    }
    /**
     *
     * 清空webviewfragment
     */
    public abstract void onClear();
    /**
     * 设置布局文件
     *
     */
    public abstract int getContentLayout();

    /**
     * 控件初始化
     *
     */
    protected void initView() {};

    /**
     * 事件监听
     */
    protected void initAction() {};

    /**
     * 数据处理
     */
    protected void initData() {};

    /**
     * 处理硬键点击
     * 返回false，事件继续传递
     * 返回true，事件终止
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * 后退
     */
    public void finish() {
        activity.popBack();
    }

    /**
     * 查找控件
     */
    public View findViewById(int id) {
        View v = null;
        if (contentView != null) {
            v = contentView.findViewById(id);
        }
        return v;
    }

    /**
     * 启动Fragment
     * @param cls 需要启动Fragment的Class
     * @param params 需要向启动Fragment传递的参数
     */
    public boolean startFragment(Class<?> cls, Bundle params) {
        boolean isSuccess = false;
        try {
            Basefragment fragment = (Basefragment)cls.newInstance();
            if (params != null) {
                fragment.setArguments(params);
            }
            activity.changeFragment(fragment, true);
            isSuccess = true;
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
            isSuccess = false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 启动Activity
     */
    public void startActivity(Intent intent) {
        activity.startActivity(intent);
    }

    /**
     * 启动Activity，接收返回结果
     */
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if(unbinder!=null)
//        unbinder.unbind();
    }
}
