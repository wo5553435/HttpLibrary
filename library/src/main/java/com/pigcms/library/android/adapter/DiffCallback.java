package com.pigcms.library.android.adapter;

import android.support.v7.util.DiffUtil;

import com.pigcms.library.android.entify.BasicVo;

import java.util.List;

/**这是一个rvadapter比较工具，对于不是xrecyclerview封装的 wrapdaadapter可以适用，若出现addheaderview错位，你需要重写AdapterDataObserver调整position 你问为什么不重写Callback
 * Created by win7 on 2017-03-31. 主要针对有结构顺序模块的数据，特别是重新刷新小部分或单个数据更变的情形
 */

public class DiffCallback extends DiffUtil.Callback {
    private List<BasicVo> mOldeDatas,mNewDatas;
    public DiffCallback (List<BasicVo> mOldeDatas,List<BasicVo> mNewDatas){
        this.mNewDatas=mNewDatas;
        this.mOldeDatas=mOldeDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldeDatas==null?0:mOldeDatas.size();
    }

    @Override
    public int getNewListSize() {
        return mNewDatas==null?0:mNewDatas.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //需要判断是否正确的问题
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
