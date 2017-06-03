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

package com.pigcms.library.capture.encode;

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 根据字符串生成二维码图片
 * @author tongxu_li
 * Copyright (c) 2014 Shanghai P&C Information Technology Co., Ltd.
 */
class QRCodeEncoder {

	private static final String TAG = QRCodeEncoder.class.getSimpleName();

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	private BarcodeFormat format;
	private int dimension;
	private Map<EncodeHintType, Object> encodeHints;

	public QRCodeEncoder(int dimension) { 
		this(BarcodeFormat.QR_CODE, null, dimension);
	}

	public QRCodeEncoder(BarcodeFormat format, Map<EncodeHintType, Object> hints, int dimension) {
		this.format = format;
		this.encodeHints = hints;
		if (encodeHints == null) {
			encodeHints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			encodeHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			encodeHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);			
		}
		this.dimension = dimension;
	}
	
	/**
	 * 生成二维码图片
	 */
	public Bitmap encodeAsBitmap(String contents) throws WriterException {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}

		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(contentsToEncode, format,
					dimension, dimension, encodeHints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
