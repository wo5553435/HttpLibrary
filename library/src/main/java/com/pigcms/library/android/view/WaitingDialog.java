package com.pigcms.library.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import com.pigcms.library.R;


public class WaitingDialog extends Dialog {

	private Context context;
	private ImageView infoOperatingIV;
	private Animation operatingAnim;

	public WaitingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public WaitingDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public WaitingDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wait_content);
		infoOperatingIV = (ImageView) findViewById(R.id.content_img_wait);
		operatingAnim = AnimationUtils.loadAnimation(context, R.anim.waiting);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
	}

	@Override
	public void show() {
		if (null != context) {
			super.show();
			if (operatingAnim == null) {
				infoOperatingIV = (ImageView) findViewById(R.id.content_img_wait);
			}
			infoOperatingIV.startAnimation(operatingAnim);
		}
	}

	@Override
	public void dismiss() {
		if (null != context) {
			super.dismiss();
			if (operatingAnim != null) {
				infoOperatingIV.clearAnimation();
			}
		}
	}

	@Override
	public void cancel() {
		if (null != context) {
			super.dismiss();
			if (operatingAnim != null) {
				infoOperatingIV.clearAnimation();
			}
		}
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	//
	// super.onConfigurationChanged(newConfig);
	//
	// if (operatingAnim != null && infoOperatingIV != null &&
	// operatingAnim.hasStarted()) {
	// infoOperatingIV.clearAnimation();
	// infoOperatingIV.startAnimation(operatingAnim);
	// }
	// }

}
