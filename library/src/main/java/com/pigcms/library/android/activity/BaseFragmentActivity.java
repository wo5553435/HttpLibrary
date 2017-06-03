package com.pigcms.library.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.pigcms.library.R;
import com.pigcms.library.utils.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2016/8/25.
 */

public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener,View.OnTouchListener {


    protected List<Basefragment> listKeyDownNotify = new ArrayList<Basefragment>();
    private  boolean isAnimation=false;
    private  boolean isFrist=true;
    private boolean isDefault=true;
    protected Activity activity;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedactivityState) {
        // 取消标题栏
        if (savedactivityState != null) {
            savedactivityState.putParcelable("android:support:fragments",null);
        }
        this.context = this;
        super.onCreate(savedactivityState);
        this.activity=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
        }

        initView();
        initData();
    }

	/*protected void onSaveInstanceState(Bundle outState)
	{
	super.onSaveInstanceState(outState);
	Parcelable p = this.mFragments.saveAllState();
	if (p != null)
	outState.putParcelable("android:support:fragments", p);
	}*/

    @Override
    protected void onStop() {
        super.onStop();
		/*boolean safe = AntiHijackingUtil.checkActivity(this);
		if (!safe) {
			ToastTools.showShort(this, R.string.HijackingTip);
		}*/
        onStopping();
    }
    /**
     *
     *@Description: 获取主布局id
     *@return ID号
     *@author: sinner
     *@date:2015-11-28 上午11:11:00
     */
    protected abstract int getContentLayout();

    /**
     *
     *@Description: 初始化控件
     *@author: sinner
     *@date:2015-11-28 上午11:11:24
     */
    protected abstract void initView();

    /**
     *
     *@Description: 初始化数据
     *@author: sinner
     *@date:2015-11-28 上午11:13:33
     */
    protected abstract void initData();
    /**
     *
     *@Description: 应用暂停后的保存或暂停动作
     *@author: sinner
     *@date:2015-11-28 上午11:13:46
     */
    protected abstract void onStopping();
    /**
     * 打开等待层
     */
    protected void showLoadingView() {};
    /**
     * 关闭等待层
     */
    protected void hideLoadingView() {};

    @Override
    public void onBackPressed() {
        if (fragmentCount() <= 0) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 设置左右动画
     * @param flag 是否向右
     */
    public void TurnToOtherSides(boolean flag){
        isDefault=((flag==true)?true:false);
        ChangeSwitchAnimation();
    }


    /**
     * 切换fragment动画
     */
    public void ChangeSwitchAnimation(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //	if(!isFrist){
       /* if(isDefault){
            //自定义动画
            ft.setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_right_exit);}
        else{
            ft.setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_right_exit);
        }*/
        ft.commit();
    }
    /**
     *
     * @param fromfragment 跳转前的fragment
     * @param tofragment   跳转后的fragment
     * @param isFirstLoad  跳转后的fragment是否是第一次加载
     */
    public void SwitchFragment(Basefragment fromfragment,Basefragment tofragment,boolean isFirstLoad){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //	if(!isFrist){
        //自定义动画
       /* ft.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_right_exit);
        //isFrist=false;
        // }
        if(fromfragment.getClass().getName().toString().equals(tofragment.getClass().getName().toString())){
            Log.e("请注意是否屏蔽同fragment跳转", "-----");
//			return;
        }*/
        if(isFirstLoad){
            ft.hide(fromfragment).add(R.id.fragments_contain,tofragment).commitAllowingStateLoss();
            registerKeyDownNotify(tofragment);
        }else{
            ft.hide(fromfragment).show(tofragment).commitAllowingStateLoss();
        }
    }

    public void changeFragment(Basefragment fragment, boolean backStackFlag) {
        changeFragment(R.id.fragments_contain, fragment, backStackFlag);
    }

    public void changeFragment(int containId, Basefragment fragment, boolean backStackFlag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // 标准动画
        //if(isAnimation){
        // fragmentTransaction
        // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // fragmentTransaction
        // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        // fragmentTransaction
        // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

        // 自定义动画

        // API LEVEL 11

	    /* ft.setCustomAnimations(
	     R.anim.fragment_slide_left_enter,
	     R.anim.fragment_slide_right_exit);*/

        // API LEVEL 13
//	    ft.setCustomAnimations(
//	     R.anim.fragment_left_enter,
//	     R.anim.fragment_left_exit,
//	     R.anim.fragment_pop_left_enter,
//	     R.anim.fragment_pop_left_exit);
//		}
        ft.replace(containId, fragment);
        if (backStackFlag) {
            ft.addToBackStack(null);
        } else {
            clearPopBackStack();
        }
        ft.commitAllowingStateLoss();
    }

    public static void setAnimationEnable(boolean enable){//开关动画，请在change之前调用
		/*if(enable){isAnimation=true;}else{
			isAnimation=false;
		}*/
    }

    public int fragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    public void popBack() {
        if (fragmentCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void clearPopBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logs.e("basefragmetn onkeydown"+getSupportFragmentManager().getBackStackEntryCount(), "----"+listKeyDownNotify.size());
        if (listKeyDownNotify != null) {
            //fragment的后退事件和 activity的后退事件分开处理，优先fragment，子fragment出return true消耗事件和return super都会终止activity的事件进行
            //这一块本来我是用于单独处理onkeydown事件，然后在主activity触发onpressback，但不知道哪里写错了没有成功判断，但逻辑应该正确，后期我全单独判断了

            boolean isHook = false;
            for (int i=0; i<listKeyDownNotify.size(); i++) {
                isHook = listKeyDownNotify.get(i).onKeyDown(keyCode, event);
                Logs.e("子fragment的onkey", ""+(i+1));
                if (isHook) {
                    Logs.e("ishook", "进入子fragment onkeydown");
                    return true;

                }

            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void registerKeyDownNotify(Basefragment fragment) {
        if (fragment != null) {

            listKeyDownNotify.add(fragment);
        }
    }

    public void removeKeyDownNotify(Basefragment fragment) {
        if (listKeyDownNotify.contains(fragment)) {
            listKeyDownNotify.remove(fragment);
        }
    }


}
