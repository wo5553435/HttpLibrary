package com.pigcms.library.capture.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.R.color;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pigcms.library.R;
import com.pigcms.library.capture.RGBLuminanceSource;

public class BitmapUtils {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

	public static void saveToAlbum(Context ctx, Bitmap bitmap, String filepath) {
		// File file = ctx.getApplicationContext().getFilesDir();
		ContentResolver cr = ctx.getContentResolver();
		String titleName = "IMG_" + sdf.format(new Date()) + ".png";
		if (Environment.getExternalStorageDirectory() == null) {
			MessageTools.showDialogOk(ctx, "请插入外部存储卡");
			return;
		}
		// String titleName=titleNames[titleNames.length-1];
		String url = MediaStore.Images.Media.insertImage(cr, bitmap, titleName,
				"");

		if (url != null) {
			// Log.i("tag", filePath+"保存至相册成功");
			MessageTools.showDialogOk(ctx, "保存至相册成功");
			ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
					.parse("file://"
							+ Environment.getExternalStorageDirectory())));
			// 更新相册
			ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
					.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		} else {
			// Log.i(filePath, "保存至相册失败");
			MessageTools.showDialogOk(ctx, "保存至相册失败");
		}
	}

	// View是你需要截图的View
	public static Bitmap getViewBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		Bitmap b = Bitmap.createBitmap(b1);
		view.destroyDrawingCache();
		return b;
	}

	// 修改bitmap的背景色
	public static Bitmap changeBitmapBackColor(Context ctx, Bitmap bitmap) {
		final int bitmapWidth = bitmap.getWidth(); // 获取原始bitmap的宽度
		final int bitmapHeight = bitmap.getHeight();
		int bitmapBackColor = ctx.getResources().getColor(
				R.color.bitmapbackgroundcolor);
		// int bitmapBackColor
		// =getResources().getColor(android.R.color.transparent);
		Bitmap centered = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Bitmap.Config.ARGB_8888);
		centered.setDensity(bitmap.getDensity());
		Canvas canvas = new Canvas(centered);
		canvas.drawColor(bitmapBackColor); // 先绘制背景色
		Paint paint = new Paint();
		paint.setAlpha(200);
		paint.setColor(ctx.getResources().getColor(color.black));
		canvas.drawBitmap(bitmap, bitmapWidth, bitmapHeight, paint); // 通过Canvas绘制Bitmap
		bitmap = centered;
		return bitmap;
	}

	// 将当前的bitmap生成新的长宽的bitmap
	public static Bitmap convertBitmapPix(Bitmap bitmap, int newWidth,
			int newHeight) {
		Bitmap newBitmap = null;
		try {
			if (bitmap != null) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				// 计算缩放率
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				// 创建操作图片用的matrix对象
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				// 创建新的图片
				newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
						matrix, true);
			}
		} catch (Exception e) {
			Log.e("convertBitmapPix", e.getMessage());
		}
		return newBitmap;

	}

	// 从二维码图片中解析出数据
	@SuppressWarnings("unchecked")
	public static String getBitmapData(Bitmap bitmap) {
		String resultStr;
		Hashtable hints = new Hashtable();

		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// 设置二维码纠错级别为最高H
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);

		// LuminanceSource source = new RGBLuminanceSource(path);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		// 解码qrcode格式的二维码
		QRCodeReader reader2 = new QRCodeReader();
		// 试图解码所有格式的二维码
		// MultiFormatReader reader2 = new MultiFormatReader();
		Result result;

		try {
			result = reader2.decode(bitmap1, hints);
			// resultStr=new
			// String(result.getText().getBytes("GB2312"),"UTF-8");
			resultStr = result.getText().toString();
			return resultStr;
		} catch (com.google.zxing.NotFoundException e) {
			// e.printStackTrace();
			System.out.println("com.google.zxing.NotFoundException e");
			return null;
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 将图片旋转degree度
	public static Bitmap rotateBitmap(Bitmap bitmap, float degree) {
		Bitmap newBitmap = null;
		Matrix m = new Matrix();
		m.setRotate(degree);
		newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), m, true);
		return newBitmap;
	}
}
