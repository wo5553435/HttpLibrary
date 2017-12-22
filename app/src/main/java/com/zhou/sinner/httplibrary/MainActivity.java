package com.zhou.sinner.httplibrary;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class MainActivity extends AppCompatActivity {
     TextView tv_tool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CollapsingToolbarLayout collapsingToolbarLayout;
        NestedScrollView nsv;
        tv_tool= (TextView) findViewById(R.id.tv_tool);
//        tv_tool.setAlpha(0);
        AppBarLayout appBar= (AppBarLayout) findViewById(R.id.appbarlayout);
        RecyclerView rv= (RecyclerView) findViewById(R.id.rv_test1);
        nsv= (NestedScrollView) findViewById(R.id.nestscroll);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        List<Object> data=new ArrayList();
        for (int i = 0; i < 20; i++) {
            data.add(new Object());
        }
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha=Math.abs(verticalOffset/(appBarLayout.getMeasuredHeight()*1.0f));
                Log.e("alpha:"+alpha,"-verticalOffset-"+verticalOffset);
                tv_tool.setAlpha(2*alpha-1f);
            }
        });
        testAdapter adapter=new testAdapter(data,null);
        rv.setAdapter(adapter);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Log.e("scrollX_"+scrollX,"scrollY_"+scrollY);
               // Log.e("oldScrollX--"+oldScrollX,"oldScrollY--"+oldScrollY);
            }
        });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_tool.setAlpha(msg.what*0.1f);
            super.handleMessage(msg);
        }
    };

    public class testAdapter extends RecyclerView.Adapter<testAdapter.ViewHolder> {

        private List<Object> data;
        private OnEventClick onEventClick;

        public testAdapter(List<Object> data, OnEventClick onEventClick){
            this.data=data;
            this.onEventClick=onEventClick;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false), onEventClick);

        }

        @Override
        public void onBindViewHolder (ViewHolder holder,int position){
            if(data!=null){

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
            public ViewHolder(View itemView,OnEventClick onEventClick) {
                super(itemView);
                this.onEventClick=onEventClick;
                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View view) {
                //getPosition 位置出现偏差，和adapterposition会因为你addview导致下标不正确
                if(onEventClick!=null)
                onEventClick.onItemClick(view,getAdapterPosition());
            }
        }

        public  abstract  class OnEventClick {
            public abstract void onItemClick(View view, int position);
        }
    }
}
