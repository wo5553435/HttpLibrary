package com.pigcms.library.android.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.pigcms.library.R;

/**
 * Created by win7 on 2017-02-18.
 * 这是一个基于glide图片下载管理器
 * 鉴于下载中使用的context参数是使用上下文，在回调使用时请注意activity的状态，只为兼容老版本的下载
 * 如果需要设计到下载建议直接使用glide
 * 目前已发现的问题在这：1.封装imageview类不能使用占位符，2动画不能使用过场动画
 */

public class ImageLoader {
    boolean isGif=false, isAnimate=false;
    private volatile static ImageLoader instance;

    /**
     * Returns singleton class instance
     */
    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    protected ImageLoader() {
        initImageloader();
    }

    /**
     * 初始化管理器
     */
    private void initImageloader() {

    }


    /**
     * 关于加载gif模式， 这个模式需要注意的是如果是自定义imageview可能无法实现gif效果，非要实现截取和变换 你可以自己编写回调的transform来定义
     * imageview的回调
     * @param isAnimate
     * @param isgif
     * @return
     */
    public  ImageLoader SetParams(boolean isAnimate,boolean isgif){
        this.isGif=isgif;this.isAnimate=isAnimate;
        return  this;
    }




    //默认无动画
    public void displayImage(Context context, String downloadUrl, ImageView imageView) {
        if (imageView instanceof ImageView) {//此控件为imageview
            Glide.with(imageView.getContext())
                    .load(downloadUrl)
                    .crossFade(500)
                    .error(R.drawable.icon)
                    .into(imageView);
        } else {//此控件为封装imageview，封装view在直接显示上与占位图有冲突
            Glide.with(imageView.getContext())
                    .load(downloadUrl)
                    .crossFade(500)
                    .dontAnimate()
                    .error(R.drawable.icon)
                    .into(imageView);
        }
    }



    //有默认动画的方式
    public void displayImage(Context context, String downloadUrl, ImageView imageView,int maxwidth,int maxheight ) {
        if (imageView instanceof ImageView) {//此控件为imageview
            Glide.with(imageView.getContext())
                    .load(downloadUrl)
                    .crossFade(500)
                    .override(maxwidth,maxheight)
                    .error(R.drawable.icon)
                    .into(imageView);
        } else {//此控件为封装imageview，封装view在直接显示上与占位图有冲突
            Glide.with(imageView.getContext())
                    .load(downloadUrl)
                    .crossFade(500)
                    .dontAnimate()
                    .error(R.drawable.icon)
                    .into(imageView);
        }
    }

    /**
     * 获取glide的context参数
     *
     * @param isApplication 影响全局相应
     * @return
     */
    private RequestManager ContextWith(Context context, boolean isApplication) {
        if (isApplication) return Glide.with(context.getApplicationContext());
        else return Glide.with(context);
    }


    public void displayImage(Context context, String downloadUrl, final ImageView imageView, Bitmap bitmap) {
        if (imageView instanceof ImageView) {//此控件为imageview
            ContextWith(context, false).load(downloadUrl).dontAnimate().into(imageView);
        } else {//此控件为封装imageview，封装view在直接显示上与占位图有冲突
            ContextWith(context, false).load(downloadUrl).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    imageView.setImageDrawable(resource.getCurrent());
                }
            });
        }
    }


    /**
     * 兼容老版本图片下载请求
     * @param uri
     * @param imageView
     */
    public void displayImage(String uri, ImageView imageView){
        if(ImageView.class.isInstance(imageView)){//如果直接是imageview可以用占位符

        }else{//集成类都不能使用，不然第一次是出不来结果的

        }
    }

    public void displayImage(String uri, ImageView imageView,boolean useholder){

    }

    /**
     * 下载返回bitmap类型图片 （暂无返回拓展）
     *
     * @param context
     * @param downloadUrl
     * @return
     */
    public void loadImageDrawable(Context context, String downloadUrl, final ImageLoaderListener imageLoaderListener) {
        Glide.with(context).load(downloadUrl).asBitmap().into(new Target<Bitmap>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public void onLoadStarted(Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                imageLoaderListener.OnFail(e.getMessage());
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageLoaderListener.OnSuccess(resource);
            }


            @Override
            public void onLoadCleared(Drawable placeholder) {

            }

            @Override
            public void getSize(SizeReadyCallback cb) {

            }

            @Override
            public void setRequest(Request request) {

            }

            @Override
            public Request getRequest() {
                return null;
            }

        });

    }

    /**
     * imageloader的构参模式,其实说实话这样有点舍本求末了
     */
    public class ImageLoaderBuilder {
        private boolean isAnimate = false;
        private boolean isGif=false;

        public ImageLoaderBuilder animate(boolean isanimate){
            this.isAnimate=isanimate;
            return  this;
        }

        public ImageLoaderBuilder isGif(boolean isgif){

            this.isGif=isgif;
            return  this;
        }
        public ImageLoader Builder(){
            return  ImageLoader.getInstance().SetParams(isAnimate,isGif);
        }
    }



}
