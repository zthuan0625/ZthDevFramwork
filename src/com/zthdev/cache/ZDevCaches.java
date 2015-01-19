package com.zthdev.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zthdev.util.Base64Utils;
import com.zthdev.util.DeviceInfoUtils;
import com.zthdev.util.FileUtils;
import com.zthdev.util.MD5Utils;
import android.content.Context;

/**
 * 
 * 类名称：ZDevCache <br>
 * 类描述：硬盘缓存工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-8-22 上午11:26:51 <br>
 * 
 * @version V1.0
 */
public class ZDevCaches
{
	/**
	 * 缓存实例当前所在的SDCard路径
	 */
	private File cacheDir;

	/**
	 * 缓存的最大容量(针对cacheDir文件夹,单位:M)
	 */
	private static final int MAX_SAVE_COUNT = 10;

	private static Map<Long, ZDevCaches> cachesMap = new HashMap<Long, ZDevCaches>();
	
	/**
	 * 初始化线程安全的map对象
	 */
	static
	{
		cachesMap = Collections.synchronizedMap(cachesMap);
	}

	
	private ZDevCaches()
	{
	}

	/**
	 * 缓存实例初始化
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param cacheName
	 *            缓存目录名称
	 */
	private void init(Context ctx, String cacheName)
	{
		if (DeviceInfoUtils.ExistSDCard())
		{
			this.cacheDir = new File(ctx.getExternalCacheDir(), cacheName);
		} else
		{
			this.cacheDir = new File(ctx.getCacheDir(), cacheName);
		}
		// 如果缓存目录不存在则创建
		if (!this.cacheDir.exists())
		{
			this.cacheDir.mkdirs();
		} else
		{
			double size = FileUtils.getDirSize(cacheDir.getAbsolutePath());
			// 如果缓存大小超出限制则清理缓存文件夹
			if (size > MAX_SAVE_COUNT)
			{
				FileUtils.delAllFile(cacheDir.getAbsolutePath());
			}
		}
	}
	
	/**
	 * 获取缓存实例
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param cacheName
	 *            缓存目录名称
	 * @return
	 */
	public static ZDevCaches get(Context ctx, String cacheName)
	{
		// 因为ZDevCaches是按文件夹来进行缓存的,不同的线程可能会设置不同的缓存目录,这里有必要针对线程来初始化ZDevCaches
		ZDevCaches cache = cachesMap.get(Thread.currentThread().getId());
		if (cache == null)
		{
			cache = new ZDevCaches();
			cachesMap.put(Thread.currentThread().getId(), cache);
		}
        cache.init(ctx, cacheName);
		return cache;
	}

	/**
	 * 关闭缓存
	 */
	public void close()
	{
		cachesMap.remove(Thread.currentThread().getId());
	}

	// =======================================
	// ============ String数据 读写 ===========
	// =======================================
	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 */
	public void put(String key, String value)
	{
		// 先对字符串进行Base64转码(最基本的安全处理)
		byte[] bytes = Base64Utils.encode(value.getBytes()).getBytes();

		File file = newFile(key);
		BufferedOutputStream out = null;
		try
		{
			out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(bytes);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (out != null)
			{
				try
				{
					out.flush();
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, String value, int saveTime)
	{
		put(key, DateUtils.newStringWithDateInfo(saveTime, value));
	}

	/**
	 * 读取 String数据
	 * 
	 * @param key
	 * @return String 数据
	 */
	public String getString(String key)
	{
		File file = getFile(key);
		if (!file.exists())
			return null;
		boolean removeFile = false;
		BufferedInputStream in = null;
		try
		{
			in = new BufferedInputStream(new FileInputStream(file));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			String readString = null;
			byte[] buffer = new byte[1024];
			int len = 0;
			// 从文件中按字节读取内容，到文件尾部时read方法将返回-1
			while ((len = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, len);
			}
			// 因为保存内容做了Base64转码,所以这里需要先解码
			readString = new String(Base64Utils.decode(new String(out
					.toByteArray())));

			// 判断保存时间,是否已经到期,如果到期则删除并返回null
			if (!DateUtils.isDue(readString))
			{
				return DateUtils.clearDateInfo(readString);
			} else
			{
				removeFile = true;
				return null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			// 如果已经到期则删除文件
			if (removeFile)
				remove(key);
		}
	}

	// =======================================
	// ============= 序列化 数据 读写 ===============
	// =======================================
	/**
	 * 保存序列化对象到缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的value
	 */
	public void put(String key, Serializable value)
	{
		put(key, value, -1);
	}

	/**
	 * 保存序列化对象到缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的value
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, Serializable value, int saveTime)
	{
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
			byte[] data = baos.toByteArray();

			if (saveTime != -1)
			{
				put(key, data, saveTime);
			}
			// 如果时间为-1,那么就代表永久保存
			else
			{
				put(key, data);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				oos.close();
			} catch (IOException e)
			{
			}
		}
	}

	/**
	 * 返回序列化对象
	 * 
	 * @param key
	 *            文件名
	 * @return 没有则返回null
	 */
	public Object getObject(String key)
	{
		byte[] data = getBinary(key);
		if (data != null)
		{
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try
			{
				bais = new ByteArrayInputStream(data);
				ois = new ObjectInputStream(bais);
				Object reObject = ois.readObject();
				return reObject;
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			} finally
			{
				try
				{
					if (bais != null)
						bais.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				try
				{
					if (ois != null)
						ois.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 返回该路径下面的所有对象集合
	 * @return
	 */
	public List<Object> getObjects()
	{
		List<Object> lists = new ArrayList<Object>();
		File[] files = cacheDir.listFiles();
		for(File file:files)
		{
			lists.add(getObj(file.getAbsolutePath()));
		}
		return lists;
	}
	private Object getObj(String path)
	{
		byte[] data = getBin(path);
		if (data != null)
		{
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try
			{
				bais = new ByteArrayInputStream(data);
				ois = new ObjectInputStream(bais);
				Object reObject = ois.readObject();
				return reObject;
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			} finally
			{
				try
				{
					if (bais != null)
						bais.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				try
				{
					if (ois != null)
						ois.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	private byte[] getBin(String path)
	{
		RandomAccessFile RAFile = null;
		boolean removeFile = false;
		try
		{
			File file = new File(path);
			if (!file.exists())
				return null;
			RAFile = new RandomAccessFile(file, "r");
			byte[] byteArray = new byte[(int) RAFile.length()];
			RAFile.read(byteArray);
			if (!DateUtils.isDue(byteArray))
			{
				return DateUtils.clearDateInfo(byteArray);
			} else
			{
				removeFile = true;
				return null;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (RAFile != null)
			{
				try
				{
					RAFile.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (removeFile)
				FileUtils.delFile(path);
		}
	}

	// =======================================
	// ============== byte 数据 读写 =============
	// =======================================
	/**
	 * 保存 byte数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的数据
	 */
	public void put(String key, byte[] value)
	{
		File file = newFile(key);
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			out.write(value);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (out != null)
			{
				try
				{
					out.flush();
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存 byte数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, byte[] value, int saveTime)
	{
		put(key, DateUtils.newByteArrayWithDateInfo(saveTime, value));
	}

	/**
	 * 获取 byte 数据
	 * 
	 * @param key
	 * @return byte 数据
	 */
	public byte[] getBinary(String key)
	{
		RandomAccessFile RAFile = null;
		boolean removeFile = false;
		try
		{
			File file = getFile(key);
			if (!file.exists())
				return null;
			RAFile = new RandomAccessFile(file, "r");
			byte[] byteArray = new byte[(int) RAFile.length()];
			RAFile.read(byteArray);
			if (!DateUtils.isDue(byteArray))
			{
				return DateUtils.clearDateInfo(byteArray);
			} else
			{
				removeFile = true;
				return null;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (RAFile != null)
			{
				try
				{
					RAFile.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (removeFile)
				remove(key);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key)
	{
		File image = getFile(key);
		return image.delete();
	}

	/**
	 * 根据指定的文件名返回文件
	 * 
	 * @param key
	 * @return
	 */
	private File getFile(String key)
	{
		File file = newFile(key);
		Long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);

		return file;
	}

	/**
	 * 创建新文件
	 * 
	 * @param key
	 * @return
	 */
	private File newFile(String key)
	{
		return new File(cacheDir, MD5Utils.getMD5(key));
	}

	/**
	 * 时间计算工具类
	 */
	private static class DateUtils
	{
		/**
		 * 判断缓存的String数据是否到期
		 * 
		 * @param str
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(String str)
		{
			return isDue(str.getBytes());
		}

		/**
		 * 判断缓存的byte数据是否到期
		 * 
		 * @param data
		 * @return true：到期了 false：还没有到期
		 */
		private static boolean isDue(byte[] data)
		{
			String[] strs = getDateInfoFromDate(data);
			if (strs != null && strs.length == 2)
			{
				String saveTimeStr = strs[0];
				while (saveTimeStr.startsWith("0"))
				{
					saveTimeStr = saveTimeStr
							.substring(1, saveTimeStr.length());
				}
				long saveTime = Long.valueOf(saveTimeStr);
				long deleteAfter = Long.valueOf(strs[1]);
				// 如果当前时间大于(文件的保存时间+文件保存的持续时间),那么文件就已经过期
				if (System.currentTimeMillis() > saveTime + deleteAfter * 1000)
				{
					return true;
				}
			}
			return false;
		}

		/**
		 * 将内容拼接上一个时间戳
		 * 
		 * @param second
		 *            持续时间(单位:秒)
		 * @param strInfo
		 *            内容
		 * @return
		 */
		private static String newStringWithDateInfo(int second, String strInfo)
		{
			return createDateInfo(second) + strInfo;
		}

		/**
		 * 将内容拼接上一个时间戳
		 * 
		 * @param second
		 * @param data2
		 * @return 返回一个byte数组
		 */
		private static byte[] newByteArrayWithDateInfo(int second, byte[] data2)
		{
			byte[] data1 = createDateInfo(second).getBytes();
			byte[] retdata = new byte[data1.length + data2.length];
			System.arraycopy(data1, 0, retdata, 0, data1.length);
			System.arraycopy(data2, 0, retdata, data1.length, data2.length);
			return retdata;
		}

		/**
		 * 当读取文件之后,这里必须要删除文件前面的时间戳去掉还原数据
		 * 
		 * @param strInfo
		 * @return
		 */
		private static String clearDateInfo(String strInfo)
		{
			// 判断不为空且是否存在时间戳,如果存在时间错则删除时间戳
			if (strInfo != null && hasDateInfo(strInfo.getBytes()))
			{
				strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
						strInfo.length());
			}
			return strInfo;
		}

		/**
		 * 当读取文件之后,这里必须要删除文件前面的时间戳去掉还原数据
		 * 
		 * @param data
		 * @return
		 */
		private static byte[] clearDateInfo(byte[] data)
		{
			if (hasDateInfo(data))
			{
				return copyOfRange(data, indexOf(data, mSeparator) + 1,
						data.length);
			}
			return data;
		}

		/**
		 * 判断是否包含时间戳信息
		 * 
		 * @param data
		 * @return
		 */
		private static boolean hasDateInfo(byte[] data)
		{
			return data != null && data.length > 15 && data[13] == '-'
					&& indexOf(data, mSeparator) > 14;
		}

		/**
		 * 获取保存的时间以及保存持续时间
		 * 
		 * @param data
		 * @return
		 */
		private static String[] getDateInfoFromDate(byte[] data)
		{
			// 判断是否存在时间戳
			if (hasDateInfo(data))
			{
				// 文件保存的时间
				String saveDate = new String(copyOfRange(data, 0, 13));

				// 文件保存持续时间
				String deleteAfter = new String(copyOfRange(data, 14,
						indexOf(data, mSeparator)));
				return new String[]
				{ saveDate, deleteAfter };
			}
			return null;
		}

		/**
		 * 获取指定字符在字节数组中的位置
		 * 
		 * @param data
		 * @param c
		 * @return
		 */
		private static int indexOf(byte[] data, char c)
		{
			for (int i = 0; i < data.length; i++)
			{
				if (data[i] == c)
				{
					return i;
				}
			}
			return -1;
		}

		/**
		 * 获取指定范围的byte数组
		 * 
		 * @param original
		 *            原byte数组
		 * @param from
		 *            起始范围
		 * @param to
		 *            终止范围
		 * @return
		 */
		private static byte[] copyOfRange(byte[] original, int from, int to)
		{
			int newLength = to - from;
			if (newLength < 0)
				throw new IllegalArgumentException(from + " > " + to);
			byte[] copy = new byte[newLength];
			System.arraycopy(original, from, copy, 0,
					Math.min(original.length - from, newLength));
			return copy;
		}

		/**
		 * 分隔符(插入时间戳时与原内容分割)
		 */
		private static final char mSeparator = ' ';

		/**
		 * 创建时间戳
		 * 
		 * @param second
		 *            指定的时间(单位:秒)
		 * @return
		 */
		private static String createDateInfo(int second)
		{
			String currentTime = System.currentTimeMillis() + "";
			while (currentTime.length() < 13)
			{
				currentTime = "0" + currentTime;
			}
			return currentTime + "-" + second + mSeparator;
		}
	}

}
