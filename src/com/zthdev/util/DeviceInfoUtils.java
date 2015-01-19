package com.zthdev.util;

import java.io.File;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * 类名称：DeviceInfo <br>
 * 类描述：获取与手机设备相关的一些信息 <br>
 * 创建人：赵腾欢 创建时间：2014-7-15 上午9:46:02 <br>
 * 
 * @version V1.0
 */
public class DeviceInfoUtils
{
	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public static boolean isAppSound(Context context)
	{
		return isAudioNormal(context);
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public static boolean isAudioNormal(Context context)
	{
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context)
	{
		PackageInfo info = null;
		try
		{

			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context)
	{
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null)
		{
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE)
		{
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo))
			{
				if (extraInfo.toLowerCase().equals("cmnet"))
				{
					netType = 3;
				} else
				{
					netType = 2;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI)
		{
			netType = 1;
		}
		return netType;
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean ExistSDCard()
	{
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 获取SD卡剩余容量
	 * 
	 * @return
	 */
	public static long getSDFreeSize()
	{
		if(ExistSDCard())
		{
			// 取得SD卡文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = sf.getAvailableBlocks();
			// 返回SD卡空闲大小
			// return freeBlocks * blockSize; //单位Byte
			// return (freeBlocks * blockSize)/1024; //单位KB
			return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
		}
		else
		{
			return 0;
		}
	}

	/**
	 * SD卡总容量
	 * 
	 * @return
	 */
	public static long getSDAllSize()
	{
		if(ExistSDCard())
		{
			// 取得SD卡文件路径
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 获取所有数据块数
			long allBlocks = sf.getBlockCount();
			// 返回SD卡大小
			// return allBlocks * blockSize; //单位Byte
			// return (allBlocks * blockSize)/1024; //单位KB
			return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
		}
		else
		{
			return 0;
		}
	}
	
	
	/**
	 * 关闭软键盘
	 * @param view
	 *            获得焦点的view对象
	 * @param context
	 *             上下文对象
	 */
	public static void closeSoftInputMethod(View view,Context context)
	{
		if (view != null) 
		{
		    InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
