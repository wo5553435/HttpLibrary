package com.pigcms.library.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;

import com.pigcms.library.R;
import com.pigcms.library.android.view.WaitingDialog;
import com.pigcms.library.utils.Logs;

//import butterknife.ButterKnife;
//import butterknife.Unbinder;

/**
 * Created by win7 on 2016/8/25.
 */

public abstract class BABaseActivity extends AppCompatActivity implements View.OnClickListener {//不基于butterknife的框架
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
     * 点击事件
     */
    public abstract  void OnClickEvent(View view);

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

    protected  <E extends View> void  setOnClick(E view){
        view.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        OnClickEvent(view);
    }

}
