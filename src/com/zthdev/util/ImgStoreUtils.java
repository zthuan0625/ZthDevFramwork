package com.zthdev.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zthdev.bean.ImageFolder;
import com.zthdev.bean.ImageItem;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

/**
 * 
 * 类名称：AlbumHelper <br>  
 * 类描述：图库工具类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-7 上午9:50:29 <br>  
 * @version V1.0
 */
public class ImgStoreUtils
{
	
    /**
     * 内容提供者,用来查询系统图库
     */
	private ContentResolver cr;

	/**
	 * 缩略图列表
	 */
	private HashMap<String, String> thumbnailList = new HashMap<String, String>();
	
	/**
	 * 存在图片的文件夹集合
	 */
	private HashMap<String, ImageFolder> bucketList = new HashMap<String, ImageFolder>();

	private static final class Hold
	{
		private static ImgStoreUtils instance = new ImgStoreUtils();
	}
	
	private ImgStoreUtils()
	{
	}

	private boolean isInit;
	
	/**
	 * 获取实例
	 * @param context
	 * @return
	 */
	public static ImgStoreUtils with(Context context)
	{
		if (!Hold.instance.isInit)
		{
			Hold.instance.cr = context.getContentResolver();
		}
		return Hold.instance;
	}

	/**
	 * 得到缩略图
	 */
	private void getThumbnail()
	{
		String[] projection =
		{ Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection,null, null, null);
		getThumbnailColumnData(cursor);
	}

	/**
	 * 从数据库中得到缩略图
	 * 
	 * @param cur
	 */
	private void getThumbnailColumnData(Cursor cur)
	{
		if (cur.moveToFirst())
		{
			int image_id;
			String image_path;
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do
			{
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);
				thumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}

	/**
	 * 是否创建了图片集
	 */
	private boolean hasBuildImagesBucketList = false;

	/**
	 * 得到图片集
	 */
	void buildImagesBucketList()
	{
		// 构造缩略图索引
		getThumbnail();

		// 构造相册索引
		String columns[] = new String[]
		{ Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA,
				Media.DISPLAY_NAME, Media.TITLE, Media.SIZE,
				Media.BUCKET_DISPLAY_NAME };
		// 得到一个游标
		Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null,null);
		if (cur.moveToFirst())
		{
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			do
			{
				String _id = cur.getString(photoIDIndex);
				String path = cur.getString(photoPathIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);

				ImageFolder bucket = bucketList.get(bucketId);
				if (bucket == null)
				{
					bucket = new ImageFolder();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
					bucket.bucketName = bucketName;
				}
				bucket.count++;
				ImageItem imageItem = new ImageItem();
				imageItem.imagePath = path;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				bucket.imageList.add(imageItem);
			} while (cur.moveToNext());
		}

		hasBuildImagesBucketList = true;
	}

	/**
	 * 得到图片集
	 * 
	 * @param refresh
	 *            是否需要重新创建图片集
	 * @return
	 */
	public List<ImageFolder> getImagesBucketList(boolean refresh)
	{
		if (refresh || (!refresh && !hasBuildImagesBucketList))
		{
			buildImagesBucketList();
		}
		List<ImageFolder> tmpList = new ArrayList<ImageFolder>();
		Iterator<Entry<String, ImageFolder>> itr = bucketList.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry<String, ImageFolder> entry = (Map.Entry<String, ImageFolder>) itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}
}
