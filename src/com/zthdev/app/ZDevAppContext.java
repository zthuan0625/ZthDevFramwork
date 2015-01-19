package com.zthdev.app;

import java.io.Serializable;
import java.util.ArrayList;
import com.zthdev.custom.view.NewDataToast;
import com.zthdev.exception.CrashHandler;
import com.zthdev.img.ZImgLoaders;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * 类名称：AppContext <br>
 * 类描述：应用上下文 <br>
 * 创建人：赵腾欢 创建时间：2014-6-20 下午4:55:08 <br>
 * 修改人： 修改时间： <br>
 * 修改备注：
 * 
 * @version V1.0
 */
public class ZDevAppContext extends Application
{

	/**
	 * 缓存activity实例,以便退出时逐个销毁
	 */
	public ArrayList<Activity> activities = new ArrayList<Activity>();

	/**
	 * 保存支付宝支付的所有密钥
	 */
	public AlipayKeys alipayKeys = null;
	
	/**
	 * 实例
	 */
	private static ZDevAppContext mInstance = null;

	/**
	 * 返回Application的唯一实例
	 * @return
	 */
	public static ZDevAppContext getInstance()
	{
		return mInstance;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		//initDebug();
	}
	
//	/**
//	 * 未捕获异常处理
//	 */
//	private void initDebug()
//	{
//		CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
//	}

	/**
	 * 处理网络连接失败
	 */
	public Handler netErrorHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			NewDataToast.makeText(getApplicationContext(), "网络连接失败...").show();
		}
	};
	
	/**
	 * 将activity添加到管理集合中(对于每一个activity在onCreate方法中都应该调用该方法)
	 */
	public void pushActivity(Activity activity)
	{
		activities.add(activity);
	}
	
	/**
	 * 将activity从管理集合中移除(对于每一个activity在onDestory方法中都应该调用该方法)
	 */
	public void popActivity(Activity activity)
	{
		activities.remove(activity);
	}
	
	/**
	 * 显示一条消息(非UI线程中使用)
	 * @param message
	 *              需要发送消息
	 */
	public void showMessage(String message)
	{
		NewDataToast.makeText(this, message).show();
	}
	
	/**
	 * 程序退出
	 */
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		
		//清理所有打开的activity
		for (Activity activity :activities)
		{
			activity.finish();
		}
		
		//清理临时图片缓存
		ZImgLoaders.with(this).destory();		
				
		//杀死程序进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * 退出应用
	 * 需要权限:<uses-permission android:name=”android.permission.RESTART_PACKAGES” />
	 * @param activity
	 *            退出功能所在的activity
	 */
	public void exit(Activity activity)
	{
		ActivityManager activityMgr=(ActivityManager)activity.getSystemService(ACTIVITY_SERVICE);
		activityMgr.killBackgroundProcesses(getPackageName());
	}
	
	/**
	 * 支付宝支付各种密钥,这些信息都是从后天获取
	 */
	public class AlipayKeys implements Serializable
	{
		private static final long serialVersionUID = 1L;

		//合作身份者id，以2088开头的16位纯数字
		public String DEFAULT_PARTNER;

		//收款支付宝账号
		public String DEFAULT_SELLER;

		//商户私钥，自助生成(然后上传到支付宝商家key管理页面)
		public String PRIVATE;

		//支付宝公钥(貌似全部都是一样的)
		public String PUBLIC;
	}
}
