package com.pigcms.library.capture;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pigcms.library.R;

/**
 * Created by win7 on 2016-11-14.
 */

public class InputDialog extends Dialog  implements View.OnClickListener{
    private Button bt_cancel;
    private Button bt_ok;
    private OnResultListener mListener;// 回调
    private EditText ed_member_value;
    public InputDialog(Context context) {
        super(context);
        init();
    }

    public InputDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected InputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setContentView(R.layout.layout_dialog_input);
        ed_member_value= (EditText) findViewById(R.id.ed_member_value);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_cancel.setOnClickListener(this);
        bt_ok.setOnClickListener(this);
    }

    public void setOnResultListener(OnResultListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 监听回调
     */
    public interface OnResultListener {
        void Ok(String code);

        void Cancel();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_ok) {
            String code=ed_member_value.getText().toString();
            mListener.Ok(code);

        } else if (i == R.id.bt_cancel) {
            mListener.Cancel();

        }
    }
}
