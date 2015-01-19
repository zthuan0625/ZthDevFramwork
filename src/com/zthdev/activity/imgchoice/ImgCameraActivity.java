package com.zthdev.activity.imgchoice;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import com.zthdev.activity.base.ZDevActivity;
import com.zthdev.custom.view.CropImageView;
import com.zthdev.custom.view.DialogBuildUtils;
import com.zthdev.custom.view.NewDataToast;
import com.zthdev.framework.R;
import com.zthdev.img.ZImgLoaders;
import com.zthdev.util.AsyncExecutor;
import com.zthdev.util.BitmapUtils;
import com.zthdev.util.DeviceInfoUtils;
import com.zthdev.util.ZipUtils;

/**
 * 
 * 类名称：ImgCameraActivity <br>
 * 类描述：相机拍照 <br>
 * 创建人：赵腾欢 创建时间：2015-1-8 上午9:06:38 <br>
 * 
 * @version V1.0
 */
public class ImgCameraActivity extends ZDevActivity implements CordovaInterface
{

	private Bitmap imgBitmap;

	private String filePath;

	/**
	 * 照片
	 */
	private File imgfile = null;

	/**
	 * 图片裁切View
	 */
	private CropImageView cropImageView;

	/**
	 * 图片滤镜View
	 */
	private CordovaWebView filter_webview;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	/**
	 * 取消按钮
	 */
	private Button cancel_btn;

	/**
	 * 确定按钮
	 */
	private Button ok_btn;

	/**
	 * 图片美化按钮
	 */
	private RadioButton filter_btn;

	/**
	 * 图片裁切按钮
	 */
	private RadioButton crop_btn;

	/**
	 * 确定裁切按钮
	 */
	private Button crop_ok_btn;

	/**
	 * 图片美化面板
	 */
	private LinearLayout filter_panel;

	/**
	 * 调用相册相机
	 */
	protected static final int CAPTURE_CODE = 1;

//	// 定义html编码格式
//	private final String mimeType = "text/html";
//	private final String encoding = "utf-8";
	
	@Override
	public int initLayoutView()
	{
		return R.layout.activity_image_camera_crop;
	}

	@Override
	public void initData()
	{
		if (DeviceInfoUtils.ExistSDCard())
		{
			try
			{
				filePath = this.getExternalCacheDir().getAbsolutePath() + "temp";
			} catch (Exception e)
			{
				filePath = this.getCacheDir().getAbsolutePath() + "temp";
			}
		} else
		{
			filePath = this.getCacheDir().getAbsolutePath() + "temp";
		}
		
		cropImageView = (CropImageView) findViewById(R.id.cropimage);
		
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		ok_btn = (Button) findViewById(R.id.ok_btn);
		filter_btn = (RadioButton) findViewById(R.id.filter_btn);
		crop_btn = (RadioButton) findViewById(R.id.crop_btn);
		crop_ok_btn = (Button) findViewById(R.id.crop_ok_btn);
		filter_panel = (LinearLayout) findViewById(R.id.filter_panel);
		
		filter_webview = (CordovaWebView) findViewById(R.id.filter_webview);
		filter_webview.loadUrl("file:///android_asset/filter.html");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(filePath)));
		startActivityForResult(intent, CAPTURE_CODE);
	}

	@Override
	public void initViewListener()
	{
		cancel_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		ok_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

		crop_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				crop_ok_btn.setVisibility(View.VISIBLE);
				cropImageView.setDrawable(new BitmapDrawable(getResources(), imgBitmap), 
						                  imgBitmap.getWidth()/2, imgBitmap.getHeight()/2);
				cropImageView.setVisibility(View.VISIBLE);
				filter_panel.setVisibility(View.GONE);
				filter_webview.setVisibility(View.GONE);
			}
		});

		crop_ok_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				imgBitmap = cropImageView.getCropImage();
				cropImageView.setDrawable(new BitmapDrawable(getResources(), imgBitmap), 
		                                  imgBitmap.getWidth(), imgBitmap.getHeight());
			}
		});

		filter_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cropImageView.setVisibility(View.GONE);
				crop_ok_btn.setVisibility(View.GONE);
				filter_panel.setVisibility(View.VISIBLE);
				filter_webview.setVisibility(View.VISIBLE);
			}
		});
	}

	public void radiobottomClick(View v)
	{
		if (v.getId() == R.id.filter_btn)
		{

		} else if (v.getId() == R.id.crop_btn)
		{

		}
	}

	/**
	 * 相机拍照后返回
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != 0 && requestCode == CAPTURE_CODE)
		{
			
			final Dialog dialog = DialogBuildUtils.with()
					.createProgressDialog(this).setMessage("请稍后..").create();
			dialog.show();
			new AsyncExecutor()
			{
				@Override
				public void doForegroundTask(Message status)
				{
					dialog.dismiss();
					if (status.what == OK)
					{
						cropImageView.setDrawable(new BitmapDrawable(getResources(), imgBitmap), 
				                                  imgBitmap.getWidth()/2, imgBitmap.getHeight()/2);
						String base64Str = BitmapUtils.bitmapToBase64(imgBitmap);
						base64Str = ZipUtils.compress(base64Str);
						filter_webview.loadUrl("javascript:setImg('"+base64Str+"')");
					} else
					{
						NewDataToast.makeText(ImgCameraActivity.this,"图片读取失败,请重新选择...").show();
					}
				}

				@Override
				public Message doBackgroundTask()
				{
					Message msg = new Message();
					// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(new File(filePath).getAbsolutePath(), options);
					int scalefactor = 800 / options.outHeight;
					int width = scalefactor * options.outWidth;

					// 调用上面定义的方法计算inSampleSize值
					options.inSampleSize = calculateInSampleSize(options, width,800);

					// 使用获取到的inSampleSize值再次解析图片
					options.inJustDecodeBounds = false;
					imgBitmap = BitmapFactory.decodeFile(
							new File(filePath).getAbsolutePath(), options);
					int degress = BitmapUtils.readPictureDegree(filePath);
					if (degress != 0)
						imgBitmap = BitmapUtils.rotateBitmap(imgBitmap, degress);
					if (imgBitmap != null)
					{
						String imgName = System.currentTimeMillis() + ".jpg";
						ZImgLoaders
								.with(ImgCameraActivity.this)
								.writeImgToSDCard(imgName, imgBitmap, false, 30);
						imgfile = new File(ZImgLoaders.with(
								ImgCameraActivity.this).getImgCacheDir()
								+ imgName);
						msg.what = OK;
					} else
					{
						msg.what = FAIL;
					}
					return msg;
				}
			}.execute();
		}
	}

	/**
	 * 计算图片缩放比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight)
	{
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
		{
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	@Override
	public void startActivityForResult(CordovaPlugin command, Intent intent,
			int requestCode)
	{
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin plugin)
	{
	}

	@Override
	public Activity getActivity()
	{
		return this;
	}

	@Override
	public Object onMessage(String id, Object data)
	{
		return null;
	}

	@Override
	public ExecutorService getThreadPool()
	{
		return threadPool;
	}
}
