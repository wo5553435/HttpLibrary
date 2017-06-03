/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pigcms.library.capture;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.pigcms.library.R;
import com.pigcms.library.android.view.WaitingDialog;
import com.pigcms.library.capture.camera.CameraManager;
import com.pigcms.library.capture.constants.Constants;
import com.pigcms.library.capture.encode.QRCodeDecoder;
import com.pigcms.library.capture.utils.AmbientLightManager;
import com.pigcms.library.capture.utils.BeepManager;
import com.pigcms.library.capture.utils.BitmapDecodeUtil;
import com.pigcms.library.capture.utils.Logs;
import com.pigcms.library.capture.utils.TopBarManage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();
	private WaitingDialog dialog;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	private TextView tv_input;
	private TopBarManage topBarManage;
	private TextView statusView;
	private ImageButton btnCameraLight;
	private boolean isEnable=true;
	/**
	 * 从哪跳过来的0、微信或支付宝收银|1、卡券核销|2、扫一扫退款
	 */
	private int fromWhich = 0;

	private String backFlag = "";

	private boolean  isCanSelect=false;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture_nav);
		tv_input= (TextView) findViewById(R.id.btn_capture_input);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// 导航栏管理
		View topBar = (View) findViewById(R.id.topBar);
		/**
		 * 获取数据
		 */
		fromWhich = getIntent().getIntExtra("IS_FROM", 0);
		findViewById(R.id.icon_select).setVisibility(getIntent().getBooleanExtra("isManager",false)?View.VISIBLE:View.INVISIBLE);
		if (topBar != null) {
			topBarManage = new TopBarManage(this, topBar);

			if (fromWhich == 0) {
				topBarManage.initTopBarTitle("扫一扫");
			} else if (fromWhich == 1) {
				topBarManage.initTopBarTitle("卡券核销");
				// capture_bottom.setVisibility(View.VISIBLE);
			} else if (fromWhich == 2) {
				topBarManage.initTopBarTitle("扫一扫");
				// capture_bottom.setVisibility(View.VISIBLE);
			}


			topBarManage.setLeftButton("后退", true, new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
			/*topBarManage.setRightButton("相册", true, new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image*//*");
					startActivityForResult(
							Intent.createChooser(intent, "选择图片"),
							Constants.REQUEST_READ_ALBUM);
				}
			});*/
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		statusView = (TextView) findViewById(R.id.status_view);

		handler = null;

		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		decodeFormats = null;
		characterSet = null;
		tv_input.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ShowInputWindow();
			}
		});
		btnCameraLight = (ImageButton) findViewById(R.id.btnCameraLight);
		btnCameraLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!btnCameraLight.isSelected()) { // 打开闪光灯
					btnCameraLight.setSelected(true);
					cameraManager.setTorch(true);
				} else { // 关闭闪光灯
					btnCameraLight.setSelected(false);
					cameraManager.setTorch(false);
				}
			}
		});
	}


	private long lastClickTime;

	/**
	 * 判断事件出发时间间隔是否超过预定值
	 */
	public boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

    /**
	 * 展示输入框界面
	 */
	private  void ShowInputWindow(){
		if(isFastDoubleClick()){
			return;
		}
		SetEnable(false);
		final InputDialog mDialog = new InputDialog(this, R.style.MyDialog);
		//mDialog.setTextTitle(getResources().getString(R.string.dialog_wenxintishi));
		//mDialog.setTextContent(userInfoVo.getMsg());
		//mDialog.setOnlyOk(true);
		mDialog.setOnResultListener(new InputDialog.OnResultListener() {

			@Override
			public void Ok(String code) {
				SetEnable(true);
				if(code!=null&&!"".equals(code)){
					Intent data = getIntent();
					data.putExtra(Constants.SCAN_RESULT_KEY, code);
					setResult(Activity.RESULT_OK, data);
					finish();
				}

				mDialog.dismiss();
			}

			@Override
			public void Cancel() {
				SetEnable(true);
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	/**
	 * 设置是否可以正常工作扫描
	 * @param isenable 扫描开关
     */
	private  void SetEnable(boolean isenable){
		handler.SetEnable(isenable);
	}




	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, scaleFactor, rawResult);
		}
			handleDecodeInternally(rawResult, barcode);
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor,
			Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					if (point != null) {
						canvas.drawPoint(scaleFactor * point.getX(),
								scaleFactor * point.getY(), paint);
					}
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
					scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, Bitmap barcode) {

		feedbackScanResult(rawResult);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			if (requestCode == Constants.REQUEST_READ_ALBUM) { // 选择相册
				Uri originalUri = data.getData();
				if (originalUri == null) {
					return;
				}

				Bitmap cameraBitmap = BitmapDecodeUtil
						.decodeSampledBitmapFromUri(this, originalUri,
								Constants.MAX_BITMAP_SIZE);

				if (cameraBitmap == null) {
					return;
				}

				new QRCodeDecoder(CaptureActivity.this).decodeImageResource(
						cameraBitmap, new QRCodeDecoder.QRCodeDecoderListener() {

							@Override
							public void parseResult(Bitmap bitmap, Result result) {
								if (bitmap != null && !bitmap.isRecycled()) {
									bitmap.recycle();
									bitmap = null;
								}
								if (result != null) {
									Logs.e("enable","---");
									/*Toast.makeText(CaptureActivity.this,
											result.getText(),
											Toast.LENGTH_SHORT).show();*/
									feedbackScanResult(result);
								} else {
									Toast.makeText(CaptureActivity.this,
											"获取二维码信息失败！", Toast.LENGTH_SHORT)
											.show();
								}
							}
						});
			}
		}
	}

	private AlertDialog.Builder builder = null;
	// 分隔符
	final static char CHAR_SPLIT = 29;

	/**
	 * 返回扫描结果
	 */
	@SuppressWarnings("unused")
	private void feedbackScanResult(Result result) {
		Logs.d("TAG", "twocode---->" + result.getText());
		Intent data = getIntent();
		// / backFlag = data.getStringExtra("backFlag");
		data.putExtra("number", 4);
		if (result != null) {
			String string = result.getText().split(" ")[0]; // 取第一部分序列号

			if (fromWhich == 1) {
				// 卡券核销
					//verificationInterface(result.getText());
				data.putExtra("card","true");
			}
			data.putExtra(Constants.SCAN_RESULT_KEY, result.getText());
			setResult(Activity.RESULT_OK, data);
			finish();
			// if (!StringTools.isEmpty(backFlag)) { // 标志位不为空


		} else {
			showDialog();
		}
	}




	/**
	 * 开启等待层
	 */
	public void showProgressDialog() {
		if (dialog == null) {
			dialog = new WaitingDialog(this, R.style.WaitingDialogStyle);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);
		}
		dialog.show();
	}

	/**
	 * 隐藏等待层
	 */
	public void hideProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}


	public void showDialog() {
		builder = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage(getResources().getString(R.string.twocode_tip))
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
					}
				});
		builder.show();
	}
}
