package com.pigcms.library.android.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.pigcms.library.R;

import java.util.ArrayList;

/**
 * Created by win7 on 2016-11-18.
 */

public abstract class CustomWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private View parent;
    private ArrayList<String> menulist;

    public CustomWindow(View parent, Context context, ArrayList<String> strings){
        super(LayoutInflater.from(context).inflate(R.layout.layout_popwindow_menu, null), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        this.context=context;
        this.parent=parent;
        this.menulist=strings;
        init();
    }




    /**
     * 初始化控件操作
     */
    private void init(){
//        getContentView()
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        this.setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.circle_white_bg));
        this.setAnimationStyle(R.style.popwin_citymenu_anim_style);

        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                ChangeBackgroudAlpha(false);
                CloseEvent();
            }
        });
        if(menulist!=null){
            for (int i = 0; i < menulist.size(); i++) {
                TextView textview=new TextView(context);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textview.setLayoutParams(layoutParams);
                textview.setGravity(Gravity.CENTER);
                textview.setTag(i);
                textview.setText(menulist.get(i));
                textview.setOnClickListener(this);
            }
        }

        ShowWindows();
    }

    /**
     * 展开窗口
     */
    public void ShowWindows(){
        if(parent!=null&&!this.isShowing()){
            this.showAtLocation(parent, Gravity.BOTTOM,0,0);
            ChangeBackgroudAlpha(true);
            ShowEvent();
        }
    }
    /**
     *
     *@Description:关闭窗口
     */
    public void CloseWindows(){
        if(this.isShowing()){
            this.dismiss();
        }
    }


    public abstract void OnEventClick(int position);

    /**
     *
     *@Description: 展开后的动作
     */
    public abstract void ShowEvent();
    /**
     *
     *@Description: 关闭后的动作
     */
    public abstract void CloseEvent();


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param flag
     *            是否开启
     */
    public void ChangeBackgroudAlpha(boolean flag) {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        if (flag) {
            lp.alpha = 0.7f; // 0.0-1.0
            ((Activity)context).getWindow().setAttributes(lp);
        } else {
            lp.alpha = 1.0f; // 0.0-1.0
            ((Activity)context).getWindow().setAttributes(lp);
        }
    }


    @Override
    public void onClick(View view) {
        OnEventClick((int)view.getTag());
    }
}
