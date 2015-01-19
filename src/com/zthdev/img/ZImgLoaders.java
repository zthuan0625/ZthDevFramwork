package com.zthdev.img;

import com.zthdev.util.FileUtils;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * 
 * 类名称：ZImgLoaders <br>  
 * 类描述：图片加载工具类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-11-6 上午9:16:45 <br>  
 * @version V1.0
 */
public class ZImgLoaders
{
	
	private Context context;
	
	/**
	 * 是否已经初始化
	 */
	private boolean isInited = false;
	
	/**
	 * 私有构造方法(单例)
	 */
	private ZImgLoaders(){}
	
	/**
	 * 内部类实现的线程安全的单例模式
	 */
	private static class ImgHold
	{
		private static final ZImgLoaders imgLoaders = new ZImgLoaders();
	}
	
	/**
	 * 获取单例对象
	 * @param context
	 * @return
	 */
	public static ZImgLoaders with(Context context)
	{
		if(!ImgHold.imgLoaders.isInited)
		{
			ImgHold.imgLoaders.isInited = true;
			ImgHold.imgLoaders.context = context.getApplicationContext();
			ZLoaderCore.get(context);
		}
		return ImgHold.imgLoaders;
	}
	
	/**
	 * 准备获取图片(返回请求实例)
	 * @return
	 */
	public RequestCreator prepare()
	{
		return new RequestCreator(context);
	}
	
	/**
	 * 获取图片在手机内存中的缓存路径
	 * @return
	 */
	public String getImgCacheDir()
	{
		return ZLoaderCore.get(context).imgSavePath;
	}
	
	/**
	 * 将指定图片写入SDCard中
	 * @param imgName
	 *             图片名称
	 * @param bitmap
	 *             图片Bitmap对象
	 * @param isMD5
	 *             是否将图片进行MD5转码
	 * @param quality
	 *             图片压缩的质量(100为不压缩)
	 */
	public void writeImgToSDCard(final String imgName, final Bitmap bitmap,
			                     final boolean isMD5, final int quality)
	{
		ZLoaderCore.get(context).writeToSDCard(imgName, bitmap, isMD5, quality);
	}
	
	/**
	 * 在程序退出时进行的一些清理
	 */
	public void destory()
	{
		// 如果图片缓存已经超出峰值,那么清理图片
		if (FileUtils.getDirSize(ZLoaderCore.get(context).imgSavePath) > ZLoaderCore.MAX_SAVE_COUNT)
		{
			FileUtils.delAllFile(ZLoaderCore.get(context).imgSavePath);
		}
	}

	/**
	 * 清理所有缓存图片
	 */
	public void destoryAll()
	{
		FileUtils.delAllFile(ZLoaderCore.get(context).imgSavePath);
	}
}
