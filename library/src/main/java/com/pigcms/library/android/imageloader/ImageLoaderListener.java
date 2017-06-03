package com.pigcms.library.android.imageloader;

import android.graphics.Bitmap;

/**
 * Created by win7 on 2017-02-20.
 */

public abstract class ImageLoaderListener {
    abstract  void OnSuccess(Bitmap bitmap);
    abstract  void OnFail(String errormsg);
}
