package com.pigcms.library.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pigcms.library.R;
import com.pigcms.library.capture.utils.Logs;

/**
 * 最近发现可选数目控件开始需求变多 所以就准备封装下以后用免得以后改来改去
 *
 * Created by win7 on 2017-04-27.
 */

public class IndexNumberView extends LinearLayout {

    private int normalColor = 0;
    private int enableColor = 0;
    private Context context;
    private StateListDrawable addDrawable, reduceDrawable;
    private int CurrentCount=0;
    private int drawableheight=0;
    private int textpadding=0;
    private int textsize=0;
    private int drawablesize=0;
    private int maxvalue=0;
    private int minvalue=0;
    TextView textView;
    Button reduce, add;

    public IndexNumberView(Context context) {
        super(context);
        init(context,null);

    }

    public IndexNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public IndexNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexNumberView);
        CurrentCount= a.getInteger(R.styleable.IndexNumberView_text,0);
        addDrawable= (StateListDrawable) a.getDrawable(R.styleable.IndexNumberView_adddrawable);
        reduceDrawable= (StateListDrawable) a.getDrawable(R.styleable.IndexNumberView_reducedrawable);
        if (attrs == null) {
            normalColor = Color.parseColor("#E4B256");
            enableColor = Color.parseColor("#E7E7E7");
        }
        if(addDrawable==null)addDrawable =/* new StateListDrawable();*/ (StateListDrawable) context.getResources().getDrawable(R.drawable.selector_bg_btnstatu);
        if(reduceDrawable==null)
            reduceDrawable =/*new StateListDrawable();*/ (StateListDrawable) context.getResources().getDrawable(R.drawable.selector_bg_btnstatu);

        CurrentCount=a.getInteger(R.styleable.IndexNumberView_text,0);
        textsize=a.getInteger(R.styleable.IndexNumberView_text_Size,18);
        maxvalue=a.getInteger(R.styleable.IndexNumberView_max,Integer.MAX_VALUE);
        minvalue=a.getInteger(R.styleable.IndexNumberView_minum,0);
        Logs.e("width:"+getWidth(),"height:"+getHeight());
        LinearLayout.LayoutParams layoutParams =
                new LayoutParams(30,
                        30);
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.leftMargin = 10;


        //right button
        reduce= new Button(context);
        reduce.setBackgroundDrawable(reduceDrawable);
        layoutParams.leftMargin = 10;   //注意单位是px
        reduce.setLayoutParams(layoutParams);
        reduce.setText("-");
        reduce.setTextSize(textsize);
        addView(reduce);


        //text
        textView= new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setText(String.valueOf(CurrentCount));
        textView.setTextSize(textsize);
        textView.setTextColor(Color.BLACK);
        addView(textView);

        //left button
        add= new Button(context);
        add.setBackgroundDrawable(addDrawable);
        add.setText("+");
        add.setTextSize(textsize);
        //注意单位是px
        add.setLayoutParams(layoutParams);
        addView(add);


        a.recycle();

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNum();
            }
        });
        reduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ReduceNum();
            }
        });
        JudgeStatu();
    }

    private void AddNum(){
        textView.setText(""+(++CurrentCount));
        JudgeStatu();
        postInvalidate();
    }


    private void ReduceNum(){
        textView.setText(""+(--CurrentCount));
        JudgeStatu();
        postInvalidate();
    }

    private void JudgeStatu(){
        if(CurrentCount>=maxvalue)
            add.setEnabled(false);
        else
            add.setEnabled(true);
        if(CurrentCount<=minvalue)
            reduce.setEnabled(false);
        else
            reduce.setEnabled(true);
    }

    public int getCurrentCount(){
        return CurrentCount;
    }
}
