package com.pigcms.library.view.searchview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pigcms.library.R;

import java.util.List;

/**
 * Created by win7 on 2017-05-19.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<String> data;
    private OnEventClick onEventClick;

    public SearchAdapter(List<String> data, OnEventClick onEventClick){
        this.data=data;
        this.onEventClick=onEventClick;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_search, parent, false), onEventClick);

    }

    @Override
    public void onBindViewHolder (ViewHolder holder,int position){
        if(data!=null){
            holder.tv.setText(data.get(position));
        }
    }

    @Override
    public int getItemCount () {
        if (data != null) return data.size();
        return 0;
    }
//abstract int getItemLayout();


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnEventClick onEventClick;
        private TextView tv;
        public ViewHolder(View itemView, OnEventClick onEventClick) {
            super(itemView);
            this.onEventClick=onEventClick;
            tv= (TextView) itemView.findViewById(R.id.tv_search_name);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            //xrecyclerview 添加headview后的getPosition 位置出现偏差，和adapterposition会因为你addview导致下标不正确
            if(onEventClick!=null)
            onEventClick.onItemClick(view,getAdapterPosition());
        }
    }

    public static abstract  class OnEventClick {
        public abstract void onItemClick(View view, int position);
    }
}
