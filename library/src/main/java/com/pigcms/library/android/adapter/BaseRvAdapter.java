package com.pigcms.library.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**这个类在点击事件上并没有处理好，最好只是用来展示，选择上如果有局部更新 建议使用带tag的itemchange,使用两次itemchange来解决单选问题
 *
 * Created by win7 on 2016-12-01.
 */

public  abstract class BaseRvAdapter  extends RecyclerView.Adapter<BaseRvAdapter.ViewHolder> {

   public abstract int GetItemLayout();

    public  abstract OnEventClick OnItemClick();

    public  abstract View SetClickView();

    public abstract void InitItemView(View rootview);

    public abstract void SetItemData(ViewHolder holder,int position);

    public abstract int getDataItemCount();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(GetItemLayout(),parent,false),OnItemClick(),SetClickView()) {

            @Override
            void InitView(View itemView) {
                InitItemView(itemView);
            }
        };
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTag(position);
        SetItemData(holder,position);
    }

    @Override
    public int getItemCount() {
        return getDataItemCount();
    }
    //abstract int getItemLayout();



    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnEventClick onEventClick;
        private View item;
        abstract void InitView(View itemView);
        private View clickView;
        public ViewHolder(View itemView,OnEventClick onEventClick,View clickView) {
            super(itemView);
            item=itemView;
            InitView(itemView);
            this.clickView=clickView;
            this.onEventClick=onEventClick;
            if(clickView==null) itemView.setOnClickListener(this);
            else clickView.setOnClickListener(this);
        }

        public void setTag(int position){
            item.setTag(position);
        }

        @Override
        public void onClick(View view) {
            onEventClick.onItemClick(view,(int)item.getTag());
            //getPosition 位置出现偏差，我很纳闷
        }
    }

    public static abstract class OnEventClick {
        abstract public void onItemClick(View view,int position);
    }
}
