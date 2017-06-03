package com.pigcms.library.android.view;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


import com.pigcms.library.R;
import com.pigcms.library.utils.ToastUtil;

import java.io.File;


/**
 * Created by win7 on 2016-10-15.
 */

public class DownLoadNotification {
    NotificationCompat.Builder builder;
    private static DownLoadNotification instance;
    private Context appcontext;
    private boolean isNeedInstall = false;
    private boolean isOver=false;
    private String saveurl;
    private String filename;
    private String downloadroute;
    NotificationManager notificationManager;

    //这里鱼和熊掌不可兼得，指定路径后偷懒 对于多线程是个麻烦，因为单例了，可是设成两个set去设置路径和文件名.所以嘛，两个法子吧 单例用单文件，多线程用builder吧
    private DownLoadNotification(Activity activity,Builder builder){
        this.appcontext=activity.getApplicationContext();
        this.isNeedInstall=builder.isNeedInstall;
        this.downloadroute=builder.downloadroute;
        this.saveurl=builder.saveurl;
        this.filename=builder.filename;
    }

    public Context getAppcontext() {
        return appcontext;
    }

    public void setAppcontext(Context appcontext) {
        this.appcontext = appcontext;
    }

    public boolean isNeedInstall() {
        return isNeedInstall;
    }

    public void setNeedInstall(boolean needInstall) {
        isNeedInstall = needInstall;
    }

    public String getSaveurl() {
        return saveurl;
    }

    public void setSaveurl(String saveurl) {
        this.saveurl = saveurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadroute() {
        return downloadroute;
    }

    public void setDownloadroute(String downloadroute) {
        this.downloadroute = downloadroute;
    }

    public static  class Builder{

        private boolean isNeedInstall=false;
        private String saveurl;
        private String filename;
        private String downloadroute;

        public Builder isNeedInstall(boolean isNeedInstall){
            this.isNeedInstall=isNeedInstall;
            return  this;
        }


        public Builder saveurl(String saveurl){
            this.saveurl=saveurl;
            return this;
        }

        public Builder filename(String name){
            this.filename=name;
            return this;
        }

        public Builder downloadroute(String route){
            this.downloadroute=route;
            return this;
        }

        public DownLoadNotification Buidler(Activity activity){
            return new DownLoadNotification(activity,this);
        }
    }

    public static DownLoadNotification getInstance(Activity activity, String saveurl, boolean isNeedInstall) {
        if (instance == null) {
            instance = new DownLoadNotification(activity, saveurl,isNeedInstall);
        }
        return instance;
    }

    public static DownLoadNotification getInstance(Context appcontext, String saveurl, boolean isNeedInstall) {
        if (instance == null) {
            instance = new DownLoadNotification(appcontext, saveurl,isNeedInstall);
        }
        return instance;
    }

    public static DownLoadNotification getInstance(Activity activity, String saveurl) {
        if (instance == null) {
            instance = new DownLoadNotification(activity, saveurl);

        }
        return instance;
    }

    private DownLoadNotification(Activity activity, String saveurl) {
        this(activity,saveurl,false);
    }

    private DownLoadNotification(Activity activity,String saveurl,boolean isNeedInstall){
        appcontext = activity.getApplicationContext();
        this.saveurl = saveurl;
        notificationManager = (
                NotificationManager) appcontext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        this.isNeedInstall=isNeedInstall;

    }

    private DownLoadNotification(Context appcontext,String saveurl,boolean isNeedInstall){
        appcontext = appcontext;
        this.saveurl = saveurl;
        notificationManager = (
                NotificationManager) appcontext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        this.isNeedInstall=isNeedInstall;

    }

    public void Show(int progress) {

        if (builder == null) {
            builder = new NotificationCompat.Builder(appcontext);
            builder.setContentTitle("正在下载");

            builder.setSmallIcon(R.drawable.icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(appcontext.getResources(), R.drawable.icon));
            builder.setOngoing(true);
            builder.setAutoCancel(true);

            //Intent intent = new Intent(this,appcontext.getClass());
            //PendingIntent pIntent = PendingIntent.getActivity(this,1,intent,0);
            // builder.setContentIntent(pIntent);
            //Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            // mContext.startActivity(intent);
            builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        }

        builder.setProgress(100, progress, false);
        builder.setContentText("请耐心等待-"+progress+"%");
        if (progress >= 100&&!isOver) {
            isOver=true;
            builder.setContentTitle("下载完成");
            builder.setContentText("稍后自行安装");
            builder.setOngoing(false);
            if (saveurl == null || saveurl == "") {
                ToastUtil.getInstance(appcontext).showToast("未找到指定路径文件");
                return;
            }
            File apkfile = new File(saveurl);
            if (!apkfile.exists()) {
                ToastUtil.getInstance(appcontext).showToast("安装失败,未找到安装apk");
                return;
            }
            // 通过Intent安装APK文件
            ;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            PendingIntent pIntent = PendingIntent.getActivity(appcontext,1,intent,0);
            builder.setContentIntent(pIntent);

            if(isNeedInstall)
            installApk();
        }


        Notification notification = builder.build();
        notificationManager.notify(101, notification);
    }

    /**
     * 安装APK文件
     */
    public  void installApk() {
        if (saveurl == null || saveurl == "") {
            ToastUtil.getInstance(appcontext).showToast("未找到指定路径文件");
            return;
        }
        File apkfile = new File(saveurl);
        if (!apkfile.exists()) {
            ToastUtil.getInstance(appcontext).showToast("安装失败,未找到安装apk");
            return;
        }
        // 通过Intent安装APK文件
        ;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        appcontext.startActivity(intent);

    }

}
