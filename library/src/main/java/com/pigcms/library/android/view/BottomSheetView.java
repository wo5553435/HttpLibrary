package com.pigcms.library.android.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by win7 on 2017-03-13.
 *注意这个view  是基于CoordinatorLayout 的布局,调整了可选peek高度，另外手动关闭后，show起无界面bug（修改下状态就行）
 */

public class BottomSheetView extends BottomSheetDialog {
    private int mPeekHeight;
    private int mMaxHeight;
    private boolean mCreated;
    private Window mWindow;
    private BottomSheetBehavior mBottomSheetBehavior;

    public BottomSheetView(@NonNull Context context) {
        super(context);
        mWindow = getWindow();
    }

    // 便捷的构造器
    public BottomSheetView(@NonNull Context context, int peekHeight, int maxHeight) {
        this(context);

        mPeekHeight = peekHeight;
        mMaxHeight = maxHeight;
    }

    public BottomSheetView(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        mWindow = getWindow();
    }

    public BottomSheetView(@NonNull Context context, boolean cancelable,
                           OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreated = true;
        setPeekHeight();
        setMaxHeight();
        setBottomSheetCallback();
    }

    public void setPeekHeight(int peekHeight) {
        mPeekHeight = peekHeight;

        if (mCreated) {
            setPeekHeight();
        }
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;

        if (mCreated) {
            setMaxHeight();
        }
    }

    /**
     * 是否可以滑动取消
     * @param enabled
     */
    public void setBatterSwipeDismiss(boolean enabled) {
        if (enabled) {

        }
    }

    private void setPeekHeight() {
        if (mPeekHeight <= 0) {
            return;
        }

        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setPeekHeight(mPeekHeight);
        }
    }

    private void setMaxHeight() {
        if (mMaxHeight <= 0) {
            return;
        }

        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mMaxHeight);
        mWindow.setGravity(Gravity.BOTTOM);
    }

    private BottomSheetBehavior getBottomSheetBehavior() {
        if (mBottomSheetBehavior != null) {
            return mBottomSheetBehavior;
        }

        View view = mWindow.findViewById(android.support.design.R.id.design_bottom_sheet);
        // setContentView() 没有调用
        if (view == null) {
            return null;
        }
        mBottomSheetBehavior = BottomSheetBehavior.from(view);
        return mBottomSheetBehavior;
    }

    private void setBottomSheetCallback() {
        if (getBottomSheetBehavior() != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetCallback);
        }
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet,
                                   @BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
                BottomSheetBehavior.from(bottomSheet).setState(
                        BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
}
