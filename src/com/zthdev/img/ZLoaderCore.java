package com.zthdev.img;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.zthdev.util.BitmapUtils;
import com.zthdev.util.MD5Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * 
 * 类名称：ImageLoaderUtils <br>
 * 类描述：图片加载及缓存工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-7-14 下午2:21:30 <br>
 * 
 * @version V1.0
 */
public class ZLoaderCore
{
	/**
	 * 内存卡保存路径
	 */
	public static final String SAVE_PATH = "/imgCache/img/";

	/**
	 * 缓存图片显示控件和一个唯一标识(这个用来防止ListView等列表显示图片发生的图片错位问题)
	 */
	public static Map<ImageView, String> validateMap;

	/**
	 * 图片缓存技术的核心类,用于缓存所有下载好的图片 在程序内存达到设定值时会将最少最近使用的图片移除掉
	 */
	private static LruCache<String, Bitmap> cache;

	/**
	 * 图片加载线程池
	 */
	private static ExecutorService loadThreadPool;

	/**
	 * 加载图片用的线程池大小
	 */
	public final static int threadSize = 3;

	/**
	 * 图片保存路径
	 */
	public String imgSavePath = null;

	/**
	 * 最多缓存20M的图片到内存卡,超出则自动清理
	 */
	public static final int MAX_SAVE_COUNT = 20;

	/**
	 * 是否已经初始化
	 */
	private boolean isInited = false;

	/**
	 * 线程安全的单例模式
	 */
	private static final class LoadHold
	{
		private final static ZLoaderCore core = new ZLoaderCore();
	}

	/**
	 * 图片加载工具初始化
	 * 
	 * @param context
	 */
	public ZLoaderCore()
	{
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context)
	{
		this.isInited = true;
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();

		int cacheSize = maxMemory / 8;

		// 设置图片缓存大小为程序最大可用内存的1/8
		cache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap bitmap)
			{
				return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
			}
		};

		validateMap = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
		this.imgSavePath = context.getCacheDir().getAbsolutePath() + SAVE_PATH;
		// 创建一个固定线程数的线程池,池大小为threadSize个线程
		loadThreadPool = Executors.newFixedThreadPool(threadSize);
	}

	/**
	 * 返回图片加载的唯一实例
	 * 
	 * @param context
	 * @return
	 */
	public static ZLoaderCore get(Context context)
	{
		if (!LoadHold.core.isInited)
			LoadHold.core.init(context);
		return LoadHold.core;
	}

	/**
	 * 加载图片并绑定到ImageView
	 * 
	 * @param imgAddress
	 *            图片地址
	 * @param targetView
	 *            显示图片的ImageView
	 */
	public void load(RequestCreator creator)
	{
		// 判断路径是否为空
		if (creator == null)
		{
			throw new IllegalArgumentException("image path must not be empty");
		}

		validateMap.put(creator.getTargetImgView(), creator.getUuid());
		if (creator.getImgURL() != null)
		{
			downloadBitmap(creator);
		} else if (creator.getImgFile() != null)
		{
			loadLocalBitmap(creator);
		} else
		{
			throw new IllegalArgumentException("image path must not be empty");
		}

	}

	/**
	 * 获取网络图片
	 * 
	 * @param creator
	 */
	private void downloadBitmap(final RequestCreator creator)
	{
		// 从缓存中读取bitmap
		Bitmap bitmap = getBitmapFromCache(creator.getMd5key());

		// 如果缓存中存在bitmap则绑定到ImageView上面,
		if (bitmap != null)
		{
			String uuid = validateMap.get(creator.getTargetImgView());
			if (uuid != null && uuid.equals(creator.getUuid()))
			{
				validateMap.remove(creator.getTargetImgView());
				creator.end(bitmap);
			}
			return;
		} else
		{
			// 线程池加载图片
			loadThreadPool.execute(new Runnable()
			{
				@Override
				public void run()
				{
					// 如果不存在则从SDCard中读取
					Bitmap bitmap = loadImgFromSDCard(imgSavePath + creator.getMd5key(),
							                          creator.getImgWidth(), 
							                          creator.getImgHeight());
					// 如果已经找到图片则显示
					if (bitmap != null)
					{
						// 判断此控件是否还需要显示该图片(ListView滑动)
						String uuid = validateMap.get(creator
								.getTargetImgView());
						if (uuid != null && uuid.equals(creator.getUuid()))
						{
							validateMap.remove(creator.getTargetImgView());
							creator.end(bitmap);
							// 添加到内存缓存
							addBitmapToCache(creator.getMd5key(), bitmap);
						}
					}
					// 如果SDCard上面也不存在该图片,那么就从网络上面读取
					else
					{
						loadImgFromNet(creator);
					}
				}
			});
		}
	}

	/**
	 * 加载内存卡图片
	 * 
	 * @return
	 */
	private void loadLocalBitmap(final RequestCreator creator)
	{
		// 线程池加载图片
		loadThreadPool.execute(new Runnable()
		{
			@Override
			public void run()
			{
				String imgPath = null;
				if (creator.getImgURL() != null)
				{
					imgPath = imgSavePath + creator.getImgURL();
				} else if (creator.getImgFile() != null)
				{
					imgPath = creator.getImgFile().getAbsolutePath();
				}

				Bitmap bitmap = getBitmapFromCache(creator.getMd5key());
				// 如果不在缓存中则读取内存卡
				if (bitmap == null)
				{
					bitmap = loadImgFromSDCard(imgPath, creator.getImgWidth(),
							creator.getImgHeight());
				}
				if (bitmap == null)
					creator.end(null);
				else
				{
					String uuid = validateMap.get(creator.getTargetImgView());
					if (uuid != null && uuid.equals(creator.getUuid()))
					{
						validateMap.remove(creator.getTargetImgView());
						creator.end(bitmap);
						// 添加到内存缓存
						addBitmapToCache(creator.getMd5key(), bitmap);
					}
				}
			}
		});
	}

	/**
	 * 将图片添加到缓存里面
	 * 
	 * @param bitmap
	 *            图片bitmap
	 * @param key
	 *            对应的key
	 */
	private void addBitmapToCache(String key, Bitmap bitmap)
	{
		if (key == null || bitmap == null)
			return;
		if (cache.get(key) == null)
		{
			cache.put(key, bitmap);
		}
	}

	/**
	 * 从缓存中读取Bitmap,如果不存在则返回null
	 * 
	 * @param key
	 */
	private Bitmap getBitmapFromCache(String key)
	{
		return cache.get(key);
	}

	/**
	 * 从内存卡中加载图片
	 * 
	 * @param imgFile
	 *            图片的本地地址
	 * @return
	 */
	private Bitmap loadImgFromSDCard(String imgPath, int imgWidth, int imgHeight)
	{
		Bitmap bitmap = null;
		File imgFile = new File(imgPath);
		if (imgFile.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(imgFile);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				byte[] b = new byte[1000];
				int length;
				while ((length = fis.read(b)) != -1)
				{
					bos.write(b, 0, length);
				}
				fis.close();
				bos.close();
				byte[] imgBytes = bos.toByteArray();
				bitmap = BitmapUtils.compressBitmap(imgBytes, imgWidth,imgHeight);
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 加载网络图片
	 * 
	 * @param imgHref
	 *            图片路径
	 * @param targetView
	 *            显示图片的ImageView
	 */
	private void loadImgFromNet(final RequestCreator creator)
	{
		Bitmap bitmap = loadImageFromUrl(creator.getImgURL(),creator.getImgWidth(), creator.getImgHeight());
		if (bitmap == null)
			creator.end(null);
		else
		{
			String uuid = validateMap.get(creator.getTargetImgView());
			if (uuid != null && uuid.equals(creator.getUuid()))
			{
				validateMap.remove(creator.getTargetImgView());
				creator.end(bitmap);
				// 添加到内存缓存
				addBitmapToCache(creator.getMd5key(), bitmap);
				writeToSDCard(creator.getMd5key(), bitmap, false, 30,creator.suffix);
			}
		}
	}

	/**
	 * 从网络下载图片
	 * 
	 * @param url
	 *            图片url
	 * @param imgWidth
	 *            图片的宽度(如果传入值小于等于0则不会压缩)
	 * @param imgHeight
	 *            图片的高度(如果传入值小于等于0则不会压缩)
	 * @return
	 */
	public Bitmap loadImageFromUrl(String url, int imgWidth, int imgHeight)
	{
		URL m;
		byte[] data = null;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		byte isBuffer[] = new byte[1024];
		if (url == null)
			return null;
		try
		{
			m = new URL(url);
			i = (InputStream) m.getContent();

			bis = new BufferedInputStream(i, 1024 * 4);
			out = new ByteArrayOutputStream();
			int len = 0;
			while ((len = bis.read(isBuffer)) != -1)
			{
				out.write(isBuffer, 0, len);
			}
			out.flush();
			data = out.toByteArray();
			bis.close();
			out.close();
		} catch (MalformedURLException e1)
		{
			e1.printStackTrace();
			return null;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if (data == null)
			return null;
		return BitmapUtils.compressBitmap(data, imgWidth, imgHeight);
	}

	/**
	 * 
	 * 将图片写入SD卡中进行缓存
	 * 
	 * @param imgName
	 *            图片的URL地址(用来生成文件名)
	 * @param bitmap
	 *            需要保存的图片
	 * @param isMD5
	 *            是否将图片名称进行MD5转码
	 * @param quality
	 *            图片压缩的质量
	 * @param suffix
	 *            图片的后缀(PNG,JPG,这里会根据后缀不同，选用不同的压缩算法)
	 */
	public void writeToSDCard(final String imgName, final Bitmap bitmap,
			final boolean isMD5, final int quality, final String suffix)
	{
		String filename = null;
		if (isMD5)
		{
			// 获取URL的MD5值
			filename = MD5Utils.getMD5(imgName);
		} else
		{
			filename = imgName;
		}

		// 获取图片保存的路径
		File fileDir = null;

		fileDir = new File(imgSavePath);

		// 如果路径不存在则创建目录
		if (!fileDir.exists())
		{
			fileDir.mkdirs();
		}
		try
		{
			File imgFile = new File(fileDir, filename);
			imgFile.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(imgFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			/**
			 * 选择合适的算法
			 */
			CompressFormat format = null;
			if (suffix.contains("png") || suffix.contains("PNG"))
			{
				format = Bitmap.CompressFormat.PNG;
			} else
			{
				format = Bitmap.CompressFormat.JPEG;
			}

			bitmap.compress(format, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

			// 图片质量(0-100之间)
			int options = 80;
			// 循环判断如果压缩后图片是否大于50kb,大于且图片质量大于0则继续压缩
			while (baos.toByteArray().length / 1024 > quality && options > 0)
			{
				baos.reset();// 重置baos即清空baos
				bitmap.compress(format, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			fOut.write(baos.toByteArray());
			fOut.flush();
			fOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 将图片写入SD卡中进行缓存
	 * 
	 * @param imgName
	 *            图片的URL地址(用来生成文件名)
	 * @param bitmap
	 *            需要保存的图片
	 * @param isMD5
	 *            是否将图片名称进行MD5转码
	 * @param quality
	 *            图片压缩的质量
	 */
	public void writeToSDCard(final String imgName, final Bitmap bitmap,
			final boolean isMD5, final int quality)
	{
		String filename = null;
		if (isMD5)
		{
			// 获取URL的MD5值
			filename = MD5Utils.getMD5(imgName);
		} else
		{
			filename = imgName;
		}

		// 获取图片保存的路径
		File fileDir = null;

		fileDir = new File(imgSavePath);

		// 如果路径不存在则创建目录
		if (!fileDir.exists())
		{
			fileDir.mkdirs();
		}
		try
		{
			File imgFile = new File(fileDir, filename);
			imgFile.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(imgFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			// 图片质量(0-100之间)
			int options = 80;
			// 循环判断如果压缩后图片是否大于50kb,大于且图片质量大于0则继续压缩
			while (baos.toByteArray().length / 1024 > quality && options > 0)
			{
				baos.reset();// 重置baos即清空baos
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			fOut.write(baos.toByteArray());
			fOut.flush();
			fOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
