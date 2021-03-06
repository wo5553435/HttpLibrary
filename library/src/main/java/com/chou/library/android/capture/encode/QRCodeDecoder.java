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

package com.chou.library.android.capture.encode;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.chou.library.android.capture.constants.DecodeFormatManager;
import com.chou.library.android.capture.setting.PreferencesActivity;

/**
 * 二维码图片解析，
 * 说明：二维码解析比较慢，解析结果采用监听回调方式
 * @author tongxu_li
 * Copyright (c) 2014 Shanghai P&C Information Technology Co., Ltd.
 */
public class QRCodeDecoder {
	private static final String TAG = QRCodeDecoder.class.getSimpleName();
	
	private Context context;
	private MultiFormatReader multiFormatReader;
	private Map<DecodeHintType,Object> decodeHints;
  
	public QRCodeDecoder(Context context) {
		this(context, null, null, null);
	}
	
	public QRCodeDecoder(Context context, 
			Collection<BarcodeFormat> decodeFormats, 
			Map<DecodeHintType,Object> hints,
			String characterSet) {
		this.context = context;
		multiFormatReader = new MultiFormatReader();
		decodeHints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		if (hints != null) {
			decodeHints.putAll(hints);
		}

		if (decodeFormats == null || decodeFormats.isEmpty()) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_PRODUCT, true)) {
				decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
			}
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_INDUSTRIAL, true)) {
				decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
			}
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
				decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			}
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX,
					true)) {
				decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
			}
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_AZTEC, false)) {
				decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
			}
			if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_PDF417, false)) {
				decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
			}
		}
		decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		if (characterSet != null) {
			decodeHints.put(DecodeHintType.CHARACTER_SET, characterSet);
		}
		multiFormatReader.setHints(decodeHints);
	}	
	
	/**
	 * 解析图片资源
	 */
	public void decodeImageResource(int resId, QRCodeDecoderListener listener) {
		Bitmap bitmap = getBitmapFor(resId);
		decodeImageResource(bitmap, listener);
	}
	
	/**
	 * 解析图片bitmap
	 */
	public void decodeImageResource(Bitmap image, final QRCodeDecoderListener listener) {
		final Bitmap bm = image;

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (listener != null) {
						listener.parseResult(bm, (Result)msg.obj);
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (bm == null) {
					Message message = handler.obtainMessage(1, null);
					handler.sendMessage(message);
					return;
				}
				Result rawResult = null;
				RGBLuminanceSource source = buildRGBLuminanceSource(bm);
				if (source != null) {
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					try {
						rawResult = multiFormatReader.decodeWithState(bitmap);
					} catch (ReaderException re) {
						// continue
					} finally {
						multiFormatReader.reset();
					}
				}
				Message message = handler.obtainMessage(1, rawResult);
				handler.sendMessage(message);				
			}
			
		}).start();
	}
	
	private RGBLuminanceSource buildRGBLuminanceSource(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		return new RGBLuminanceSource(width, height, pixels); 
	}
	
	private Bitmap getBitmapFor(int resId) {
		return BitmapFactory.decodeResource(context.getResources(), resId);
	}
	
	public interface QRCodeDecoderListener {
		public void parseResult(Bitmap bitmap, Result result); 
	}
}
