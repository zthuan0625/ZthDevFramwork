package com.zthdev.img;

import java.io.File;
import java.util.UUID;
import com.zthdev.util.MD5Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 
 * 类名称：RequestCreator <br>
 * 类描述：代表了一次图片加载请求 <br>
 * 创建人：赵腾欢 创建时间：2014-11-6 上午9:37:44 <br>
 * 
 * @version V1.0
 */
public class RequestCreator
{

	/**
	 * 标识唯一
	 */
	private String uuid;
	
	/**
	 * 应用程序上下文
	 */
	private Context context;

	/**
	 * 文件后缀(PNG,或者是JPEG)
	 */
	public String suffix;
	
	/**
	 * 需要的宽度(没有设置则按图片原生宽度显示)
	 */
	private int imgWidth = -1;

	/**
	 * 需要的高度(没有设置则按图片原生高度显示)
	 */
	private int imgHeight = -1;

	/**
	 * 是否添加到控件的背景层(setBackground)
	 */
	private boolean isAttachBg = false;
	
	/**
	 * 图片加载网络地址
	 */
	private String imgURL;

	/**
	 * 为图片保存名称后面添加一个tag
	 */
	private String tag;
	
	/**
	 * 图片保存的md5key
	 */
	private String md5key;
	
	/**
	 * 图片加载本地文件
	 */
	private File imgFile;

	/**
	 * 图片加载中显示的图片
	 */
	private Drawable placeHoldDrawable;

	/**
	 * 图片加载失败显示的图片
	 */
	private Drawable errorLoadDrawable;

	/**
	 * 当前需要显示图片的ImageView
	 */
	private ImageView targetImgView;
	
	public int getImgWidth()
	{
		return imgWidth;
	}

	public int getImgHeight()
	{
		return imgHeight;
	}

	public String getImgURL()
	{
		return imgURL;
	}

	public File getImgFile()
	{
		return imgFile;
	}

	
	public ImageView getTargetImgView()
	{
		return targetImgView;
	}

	public String getUuid()
	{
		return uuid;
	}

	/**
	 * 设置是否将图片添加到背景层
	 * @param isAttachBg
	 */
	public RequestCreator attachBg(boolean isAttachBg)
	{
		this.isAttachBg = isAttachBg;
		return this;
	}

	/**
	 * 返回图片保存名称后面依附的tag
	 * @return
	 */
	public String getTag()
	{
		return tag;
	}

	/**
	 * 为图片保存的名称后面添加一个tag
	 * 有时加载图片需要裁切出缩略图但是又需要用到大图
	 * 这里在保存到sdcard时为防止因保存名称相同而覆盖大图或小图,添加一个tag来区分
	 * @param tag
	 */
	public RequestCreator setTag(String tag)
	{
		this.tag = tag;
		if(imgURL != null)
			md5key = MD5Utils.getMD5(imgURL + tag);
		else if(imgFile != null)
			md5key = MD5Utils.getMD5(imgFile.getAbsolutePath() + tag);
		return this;
	}

	/**
	 * 返回图片保存的md5key
	 * @return
	 */
	public String getMd5key()
	{
		return md5key;
	}

	/**
	 * main线程Handler
	 */
	private Handler mainHandler  = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == 0)
			{
				if (errorLoadDrawable != null)
					targetImgView.setImageDrawable(errorLoadDrawable);
			}
			else if(msg.what == 1)
			{
				Bitmap bitmap = (Bitmap) msg.obj;
				//判断是否添加到背景层
				if(isAttachBg)
					targetImgView.setBackgroundDrawable(new BitmapDrawable(context.getResources(), bitmap));
				else
					targetImgView.setImageBitmap(bitmap);
			}
		}
	};
	
	RequestCreator(Context context)
	{
		this.context = context.getApplicationContext();
		this.uuid = UUID.randomUUID().toString();
	}

	/**
	 * 重新调整图片大小
	 * 
	 * @param imgWidth
	 *            需要的宽度
	 * @param imgHeignt
	 *            需要的高度
	 */
	public RequestCreator reSize(int imgWidth, int imgHeight)
	{
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		return this;
	}
	
	/**
	 * 根据宽度进行缩放
	 * @param imgWidth
	 *              目标宽度
	 * @return
	 */
	public RequestCreator reSizeWidth(int imgWidth)
	{
		this.imgWidth = imgWidth;
		return this;
	}
	
	/**
	 * 根据高度进行缩放
	 * @param imgHeight
	 *              目标高度
	 * @return
	 */
	public RequestCreator reSizeHeight(int imgHeight)
	{
		this.imgHeight = imgHeight;
		return this;
	}
	
	/**
	 * 设置图片默认显示的图片
	 * 
	 * @param resId
	 *            资源ID
	 * @return
	 */
	public RequestCreator placeHoldImg(int resId)
	{
		if (resId > 0)
		{
			placeHoldDrawable = context.getResources().getDrawable(resId);
		}
		return this;
	}

	/**
	 * 设置图片默认显示的图片
	 * 
	 * @param bitmap
	 *            图片的Bitmap对象
	 * @return
	 */
	public RequestCreator placeHoldImg(Bitmap bitmap)
	{
		if (bitmap != null)
		{
			placeHoldDrawable = new BitmapDrawable(context.getResources(),bitmap);
		}
		return this;
	}

	/**
	 * 设置图片默认显示的图片
	 * 
	 * @param placeDrawable
	 *            图片的Drawable对象
	 * @return
	 */
	public RequestCreator placeHoldImg(Drawable placeDrawable)
	{
		if (placeDrawable != null)
		{
			placeHoldDrawable = placeDrawable;
		}
		return this;
	}

	/**
	 * 设置图片加载失败显示的图片
	 * 
	 * @param resId
	 *            资源ID
	 * @return
	 */
	public RequestCreator errorLoadImg(int resId)
	{
		if (resId > 0)
		{
			errorLoadDrawable = context.getResources().getDrawable(resId);
		}
		return this;
	}

	/**
	 * 设置图片加载失败显示的图片
	 * 
	 * @param bitmap
	 *            图片的Bitmap对象
	 * @return
	 */
	public RequestCreator errorLoadImg(Bitmap bitmap)
	{
		if (bitmap != null)
		{
			errorLoadDrawable = new BitmapDrawable(context.getResources(),bitmap);
		}
		return this;
	}

	/**
	 * 设置图片加载失败显示的图片
	 * 
	 * @param placeDrawable
	 *            图片的Drawable对象
	 * @return
	 */
	public RequestCreator errorLoadImg(Drawable errorDrawable)
	{
		if (errorDrawable != null)
		{
			errorLoadDrawable = errorDrawable;
		}
		return this;
	}

	/**
	 * 设置需要显示图片的ImageView
	 * 
	 * @param imageView
	 * @return
	 */
	public RequestCreator into(ImageView imageView)
	{
		this.targetImgView = imageView;
		return this;
	}

	/**
	 * 设置网络图片的地址(加载网络图片时使用)
	 * 
	 * @param url
	 *            图片的URL地址
	 * @return
	 */
	public RequestCreator load(String URL)
	{
		this.imgURL = URL;
		if(tag != null)
			md5key = MD5Utils.getMD5(imgURL + tag);
		else
			md5key = MD5Utils.getMD5(imgURL);
		
		this.suffix = URL.substring(URL.lastIndexOf(".") + 1,URL.length());
		return this;
	}

	/**
	 * 设置图片的本地文件(加载本地图片时使用)
	 * 
	 * @param file
	 *            图片文件
	 * @return
	 */
	public RequestCreator load(File file)
	{
		this.imgFile = file;
		if(tag != null)
			md5key = MD5Utils.getMD5(imgFile.getAbsolutePath() + tag);
		else
			md5key = MD5Utils.getMD5(imgFile.getAbsolutePath());
		String path = file.getAbsolutePath();
		this.suffix = path.substring(path.lastIndexOf(".") + 1,path.length());
		return this;
	}

	/**
	 * 开始加载图片
	 */
	public void start()
	{
		if(targetImgView == null)
			return;
		if(placeHoldDrawable != null)
			targetImgView.setImageDrawable(placeHoldDrawable);
		ZLoaderCore.get(context).load(this);
	}

	/**
	 * 加载图片结束
	 * 
	 * @param bitmap
	 */
	public void end(Bitmap bitmap)
	{
		// 如果没有显示图片的控件则退出
		if (targetImgView == null)
			return;

		if (bitmap == null)
		{
			//0,代表没有找到图片
			mainHandler.sendMessage(mainHandler.obtainMessage(0, bitmap));
		} else
		{
			//1,代表找到图片
			mainHandler.sendMessage(mainHandler.obtainMessage(1, bitmap));
		}
	}
}
