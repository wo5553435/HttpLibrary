package com.pigcms.library.capture.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pigcms.library.R;


/**
 * 管理头部导航栏
 * 注意：头部导航栏标题id=tvTopTitle, 左边按钮id=btnTopLeft, 右边按钮id=btnTopRight
 * @author tongxu_li
 * Copyright (c) 2014 Shanghai P&C Information Technology Co., Ltd.
 */
public class TopBarManage
{
	private Context context;
    private View topBarView;
    private TextView tvTitle;
    private Button leftButton;
    private Button rightButton;
    
    /**
     * 构造函数，传人需要管理的头部view
     * @param context
     * @param view
     */
    public TopBarManage(Context context, View view) {
    	this.context = context;
    	commonInit(view);
    }
    
    /**
     * 获取头部导航栏内部的控件信息
     * @param view
     */
    private void commonInit(View view) {
    	this.topBarView = view;
    	tvTitle = (TextView) topBarView.findViewById(R.id.tvTopTitle);
    	leftButton = (Button) topBarView.findViewById(R.id.btnTopLeft);
    	rightButton = (Button) topBarView.findViewById(R.id.btnTopRight);
    }
    
    /**
     * 初始化导航栏名称
     * @param title
     */
	public void initTopBarTitle(String title) {

    	tvTitle.setText(title);
	}
	
	/**
	 * 设置导航栏左边按钮
	 * @param txt
	 * @param isShow
	 * @param listener
	 */
	public void setLeftButton(String txt, boolean isShow, OnClickListener listener) {
		setButton(leftButton, txt, isShow, listener);
	}
	
	/**
	 * 设置导航栏右边按钮
	 * @param txt
	 * @param isShow
	 * @param listener
	 */
	public void setRightButton(String txt, boolean isShow, OnClickListener listener) {
		setButton(rightButton, txt, isShow, listener);
	}
	
	private void setButton(Button btn, String txt, boolean isShow, OnClickListener listener) {
		if (!isShow) {
			btn.setVisibility(View.GONE);
			return;
		}
		btn.setText(txt);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(listener);		
	}
}