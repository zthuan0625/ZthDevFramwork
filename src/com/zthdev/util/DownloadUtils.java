package com.zthdev.util;

import com.zthdev.bean.VersionInfo;
import com.zthdev.receive.ZDevCompleteReceiver;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;

/**
 * 
 * 项目名称：ChellonaCarV0620<br>
 * 类名称：DownloadUtil <br>  
 * 类描述：调用系统的下载服务 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-6-23 上午9:21:33 <br> 
 * @version V1.0
 */
public class DownloadUtils
{
   /**
    * 调用系统的下载组件下载指定的内容
    * @param context 上下文
    * @param path 下载路径
    * @return
    */
   private static long doDownload(Context context,String path)
   {
	   DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);  
       
	    Uri uri = Uri.parse(path);  
	    
	    Request request = new Request(uri);  
	  
	    //设置允许使用的网络类型，这里是移动网络和wifi都可以    
	    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);    
	  
	    //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION    
	    request.setNotificationVisibility(0); 
	  
	    //不显示下载界面
	    request.setVisibleInDownloadsUi(false);  
	    /*
	     * 设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，
	     * 下载后的文件在/mnt/sdcard/Android/data/packageName/files目录下面，
	     * 如果sdcard不可用,设置了下面这个将报错，不设置，下载后的文件在/cache这个  目录下面
	     * 
	     */  
	    //request.setDestinationInExternalFilesDir(this, null, "tar.apk");  
	    long id = downloadManager.enqueue(request);  
	    return id;
	    //TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面  
   }
   
    /**
	 * 下载最新版程序,更新版本
	 */
	public static void doDownloadApk(Context context,VersionInfo info)
	{
		ZDevCompleteReceiver receiver = new ZDevCompleteReceiver();
		
		receiver.downloadID = doDownload(context, info.data.fileurl);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.DOWNLOAD_COMPLETE");
		filter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED");
		//注册广播,监听下载完毕事件
		context.registerReceiver(receiver, filter);
	}
}
