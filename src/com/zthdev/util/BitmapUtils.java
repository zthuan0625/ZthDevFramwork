package com.zthdev.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.widget.ImageView;

/**
 * 
 * 类名称：BitmapUtils <br>
 * 类描述：Bitmap工具 <br>
 * 创建人：赵腾欢 创建时间：2014-11-10 下午4:12:17 <br>
 * 
 * @version V1.0
 */
public class BitmapUtils
{

	/**
	 * 判断图片是否需要旋转
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path)
	{
		int degress = 0;
		try
		{
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
			case ExifInterface.ORIENTATION_ROTATE_90:
				degress = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degress = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degress = 270;
				break;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return degress;
	}

	/**
	 * 旋转图片
	 * 
	 * @param bitmap
	 *            待旋转的图片
	 * @param degress
	 *            旋转角度
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress)
	{
		if (bitmap != null)
		{
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * 图片压缩
	 * 
	 * @param image
	 *            需要压缩的原始图片
	 * @param maxSize
	 *            允许的图片最大 大小
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image, int maxSize)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		// 图片质量(0-100之间)
		int options = 80;
		// 循环判断如果压缩后图片是否大于maxSizekb,大于且图片质量大于0则继续压缩
		while (baos.toByteArray().length / 1024 > maxSize && options > 0)
		{
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		image.recycle();
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 将Bitmap按照指定的大小压缩
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap compressBitmap(byte[] imgBytes, int imgWidth,
			int imgHeight)
	{
		Bitmap bitmap = null;
		if (imgWidth <= 0 && imgHeight <= 0)
		{
			bitmap = BitmapFactory
					.decodeByteArray(imgBytes, 0, imgBytes.length);
		} else
		{
			// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory
					.decodeByteArray(imgBytes, 0, imgBytes.length, options);

			// 如果需要缩放的大小跟原大小一致则直接返回
			if (imgWidth == options.outWidth && imgHeight == options.outHeight)
			{
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeByteArray(imgBytes, 0,
						imgBytes.length, options);
				return bitmap;
			}

			// 判断是否根据高度或者宽度缩放
			if (imgWidth > 0 && imgHeight <= 0)
			{
				int scalefactor = imgWidth / options.outWidth;
				imgHeight = scalefactor * options.outHeight;
			} else if (imgWidth <= 0 && imgHeight > 0)
			{
				int scalefactor = imgHeight / options.outHeight;
				imgWidth = scalefactor * options.outWidth;
			}

			// 调用上面定义的方法计算inSampleSize值
			options.inSampleSize = calculateInSampleSize(options, imgWidth,
					imgHeight);

			if (options.inSampleSize <= 0)
				options.inSampleSize = 1;
			// 使用获取到的inSampleSize值再次解析图片
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(imgBytes, 0,
					imgBytes.length, options);
		}
		return bitmap;
	}

	/**
	 * 将byte数组转换成Bitmap
	 * 
	 * @param imgBytes
	 * @return
	 */
	public static Bitmap bytes2Bitmap(byte[] imgBytes)
	{
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
		return bitmap;
	}

	/**
	 * Bitmap转byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 根据指定的宽高获取压缩比例
	 * 
	 * @param options
	 * @param reqWidth
	 *            宽度
	 * @param reqHeight高度
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight)
	{
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		// 计算出实际宽高和目标宽高的比率
		final int heightRatio = Math.round((float) height / (float) reqHeight);
		final int widthRatio = Math.round((float) width / (float) reqWidth);
		// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
		// 一定都会大于等于目标的宽和高。
		inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		return inSampleSize;
	}

	/**
	 * 使头像变灰
	 * 
	 * @param drawable
	 */
	public static void porBecomeGrey(ImageView imageView, Drawable drawable)
	{
		drawable.mutate();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		drawable.setColorFilter(cf);
		imageView.setImageDrawable(drawable);
	}

	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap)
	{

		// 将Bitmap转换成Base64字符串
		StringBuffer string = new StringBuffer();
		string.append("data:image/jpeg;base64,");
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		try
		{
			bitmap.compress(CompressFormat.JPEG, 80, bStream);
			bStream.flush();
			bStream.close();
			byte[] bytes = bStream.toByteArray();
			string.append(Base64.encodeToString(bytes, Base64.NO_WRAP));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("string.." + string.length());
		return string.toString();
	}

	/**
	 * base64转为bitmap
	 * 
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data)
	{
		String base64Str = base64Data.substring(base64Data.indexOf(",") + 1,base64Data.length());
		System.out.println("the base64 decode:" + base64Str);
		byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
