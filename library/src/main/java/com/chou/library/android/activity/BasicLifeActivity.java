package com.chou.library.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;

import com.pigcms.library.R;
import com.chou.library.android.view.WaitingDialog;
import com.chou.library.util.Logs;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Created by sinner on 2017-11-15.
 * mail: wo5553435@163.com
 * github: https://github.com/wo5553435
 */

public abstract class BasicLifeActivity extends RxAppCompatActivity {
    protected WaitingDialog dialog;
    protected Activity activity;
    // private Unbinder unbinder;
    protected SparseArray<View> mViews;//控件容器
    /**
     * 获取主布局文件
     * @return 布局R文件对应id
     */
    public  abstract  int getContentLayout();

    /**
     * 初始化控件
     */
    public abstract void InitView();

    /**
     * 初始化事件
     */
    public abstract  void InitAction();

    /**
     * 初始化数据和启动操作
     */
    public  abstract  void InitData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (this.getContentLayout() != 0) {
            setContentView(getContentLayout());

        }
        activity = this;
//        unbinder ButterKnife.bind(activity);
        InitView();
        InitAction();
        InitData();
    }

    protected  <E extends View>E findView(int viewId){
        E view=(E) mViews.get(viewId);
        if(view==null){
            view=(E) findViewById(viewId);
            mViews.put(viewId,view);
        }
        return view;
    }


    /**
     * 开启等待层
     */
    public void showProgressDialog() {
        if (dialog == null) {
            dialog = new WaitingDialog(this, R.style.WaitingDialogStyle);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        }
        dialog.show();
    }

    /**
     * 隐藏等待层
     */
    public boolean hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }
        return false;
    }

    private long lastClickTime;

    /**
     * 判断事件出发时间间隔是否超过预定值
     */
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void startActivity(Intent intent) {
        // 防止连续点击
        if (isFastDoubleClick()) {
            Logs.i("TAG", "startActivity() 重复调用");
            return;
        }
        super.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbinder.unbind();
    }

}
