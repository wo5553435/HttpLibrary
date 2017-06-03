package com.pigcms.library.view.searchview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pigcms.library.R;
import com.pigcms.library.view.searchview.adapter.SearchAdapter;
import com.pigcms.library.view.searchview.animation.CircularRevealAnim;
import com.pigcms.library.view.searchview.animation.IOnSearchClickListener;
import com.pigcms.library.view.searchview.utils.KeyBoardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2017-05-19.
 * 这个dialogfragment不建议你直接使用方法做样式展示 你需要是用一个util类来操作整个类
 */

public class SearchDialog extends DialogFragment implements CircularRevealAnim.AnimListener, DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    private View itemview;

    private ImageView ivSearchBack;
    public EditText etSearchKeyword;
    private ImageView ivSearchSearch;

    private RecyclerView rvSearchHistory;
    private LinearLayout layout_search_progress;

    private View searchUnderline;
    private View viewSearchOutside;
    private TextView tv_showresult;
    private List<String> strs_data;


    private SearchAdapter adapter;


    private IOnSearchClickListener iOnSearchClickListener;
    private SearchAdapter.OnEventClick onItemClick;

    public static SearchDialog newInstance() {
        Bundle bundle = new Bundle();
        SearchDialog searchFragment = new SearchDialog();
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    public void setOnSearchClickListener(IOnSearchClickListener iOnSearchClickListener) {
        this.iOnSearchClickListener = iOnSearchClickListener;
    }

    //动画
    private CircularRevealAnim mCircularRevealAnim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        itemview=inflater.inflate(R.layout.layout_dialog_search, container, false);
        initView();
        return itemview;
    }

    public void ShowWaitDialog(boolean isshow){
        layout_search_progress.setVisibility(isshow?View.VISIBLE:View.GONE);
        rvSearchHistory.setVisibility(isshow?View.GONE:View.VISIBLE);
    }


    public  void  ShowResult(List<String> data){
        strs_data.clear();
        strs_data.addAll(data);
        ShowWaitDialog(false);
        if(adapter==null) {
            adapter=new SearchAdapter(strs_data,onItemClick);
            rvSearchHistory.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }



    /**
     * 初始化控件
     */
    private void initView() {
        ivSearchBack = (ImageView) itemview.findViewById(R.id.iv_search_back);
        etSearchKeyword = (EditText) itemview.findViewById(R.id.et_search_keyword);
        ivSearchSearch = (ImageView) itemview.findViewById(R.id.iv_search_search);
        rvSearchHistory = (RecyclerView) itemview.findViewById(R.id.rv_search_history);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        rvSearchHistory.setLayoutManager(linearLayoutManager);

        layout_search_progress= (LinearLayout) itemview.findViewById(R.id.layout_search_progress);
        tv_showresult= (TextView) itemview.findViewById(R.id.tv_search_result);
        searchUnderline = (View) itemview.findViewById(R.id.search_underline);
        viewSearchOutside = (View) itemview.findViewById(R.id.view_search_outside);

        //实例化动画效果
        mCircularRevealAnim = new CircularRevealAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(this);

        getDialog().setOnKeyListener(this);//键盘按键监听
        ivSearchSearch.getViewTreeObserver().addOnPreDrawListener(this);//绘制监听

        //监听编辑框文字改变
        etSearchKeyword.addTextChangedListener(new TextWatcherImpl());
        //监听点击
        ivSearchBack.setOnClickListener(this);
        viewSearchOutside.setOnClickListener(this);
        ivSearchSearch.setOnClickListener(this);
        strs_data=new ArrayList<>();

        onItemClick=new SearchAdapter.OnEventClick() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("onItemClick","--"+position);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    private void initDialog() {
        Window window = getDialog().getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消默认过渡动画
    }

    @Override
    public void onHideAnimationEnd() {
        etSearchKeyword.setText("");
        dismiss();
    }

    public void ShowResultText(String string){
        tv_showresult.setText(string);
    }

    @Override
    public void onShowAnimationEnd() {
        if (isVisible()) {
            KeyBoardUtils.openKeyboard(getContext(), etSearchKeyword);
        }
    }

    /**
     * 监听编辑框文字改变
     */
    private class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String keyword = editable.toString();
            if (TextUtils.isEmpty(keyword.trim())) {
               // setAllHistorys();
               // searchHistoryAdapter.notifyDataSetChanged();
            } else {
               // setKeyWordHistorys(editable.toString());
            }
        }
    }


    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            hideAnim();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            search();
        }
        return false;
    }

    private void search() {
        String searchKey = etSearchKeyword.getText().toString();
        if (!TextUtils.isEmpty(searchKey.trim())) {
            iOnSearchClickListener.OnSearchClick(searchKey);//接口回调
            //searchHistoryDB.insertHistory(searchKey);//插入到数据库
            hideAnim();
        }
    }





    private void hideAnim() {
        KeyBoardUtils.closeKeyboard(getContext(), etSearchKeyword);
        mCircularRevealAnim.hide(ivSearchSearch, itemview);
    }

    @Override
    public boolean onPreDraw() {
        ivSearchSearch.getViewTreeObserver().removeOnPreDrawListener(this);
        mCircularRevealAnim.show(ivSearchSearch, itemview);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
            hideAnim();
        } else if (view.getId() == R.id.iv_search_search) {
            search();
        }
    }
}
