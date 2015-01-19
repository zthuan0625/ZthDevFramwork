package com.zthdev.version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import com.zthdev.bean.VersionInfo;
import com.zthdev.custom.view.DialogBuildUtils;
import com.zthdev.custom.view.NewDataToast;
import com.zthdev.exception.NetConnectErrorException;
import com.zthdev.framework.R;
import com.zthdev.util.AsyncExecutor;
import com.zthdev.util.DeviceInfoUtils;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.RemoteViews;

/**
 * 
 * 类名称：VersionManager <br>
 * 类描述：版本更新工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-11-8 下午1:43:14 <br>
 * 
 * @version V1.0
 */
public class VersionManager
{
	/**
	 * 上下文对象
	 */
	private Context mContext;

	/**
	 * 唯一实例
	 */
	private static VersionManager versionManager;

	/**
	 * 通知管理器
	 */
	private NotificationManager manager;

	/**
	 * 通知
	 */
	private Notification notif;

	/**
	 * 当前下载的进度
	 */
	private int length = 0;

	/**
	 * 当前的总下载量
	 */
	private int totalSize = 0;

	/**
	 * 当前的版本信息
	 */
	private VersionInfo tversionInfo = null;

	/**
	 * 文件保存的位置
	 */
	private String filePath;

	/**
	 * 定时器(定时刷新通知栏进度,这里不能刷新太频繁,否则系统UI线程会卡死)
	 */
	private Timer timer;

	/**
	 * 请求后台时的加载提示
	 */
	private Dialog dialog;

	// 定义html编码格式
	private final String mimeType = "text/html";
	private final String encoding = "utf-8";

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	private VersionManager(Context mContext)
	{
		this.mContext = mContext;
	}

	/**
	 * 返回唯一实例
	 * 
	 * @param mContext
	 * @return
	 */
	public static VersionManager with(Context mContext)
	{
		if (versionManager == null)
			versionManager = new VersionManager(mContext);
		versionManager.mContext = mContext;
		return versionManager;
	}

	/**
	 * 检查版本信息
	 * 
	 * @param path
	 *            版本信息地址
	 * @param isShowHint
	 *            是否显示提示信息
	 */
	public void checkVersion(final String path, final boolean isShowHint)
	{
		if (isShowHint)
		{
			dialog = DialogBuildUtils.with()
					                 .createProgressDialog(mContext)
					                 .setMessage("正在检查")
					                 .create();
			dialog.show();
		}
		// 版本更新
		new AsyncExecutor()
		{
			@Override
			public void doForegroundTask(Message status)
			{
				if (status.what == 500)
				{
					if (isShowHint)
					{
						dialog.dismiss();
						NewDataToast.makeText(mContext, "当前为最新版本,无需更新.").show();
					}
					tversionInfo = null;
				} else if (status.what == 200)
				{
					if (isShowHint)
						dialog.dismiss();
					// 如果SDCard不存在则提示无法更新
					if (!DeviceInfoUtils.ExistSDCard())
					{
						NewDataToast.makeText(mContext,
								"存在新版本,但检测到手机无SD卡,不能使用自动更新").show();
					} else
					{
						showDoUpdateDialog();
					}
				}
				else if(status.what == 300)
				{
					if (isShowHint)
					{
						dialog.dismiss();
						NewDataToast.makeText(mContext,"检查更新失败").show();
					}
				}
					
			}

			@Override
			public Message doBackgroundTask()
			{
				Message msg = new Message();
				try
				{
					VersionService service = new VersionService();
					PackageInfo info = DeviceInfoUtils.getPackageInfo(mContext);
					tversionInfo = service.getNewVersionInfo(path);
					if("0000".equals(tversionInfo.header.state))
					{
						if (info.versionName.equals(tversionInfo.data.version))
						{
							// 不需要更新
							msg.what = 500;
						}
						else
						{
							msg.what = 200;
						}
					}
					else
					{
						// 存在更新
						msg.what = 300;
					}
				} catch (NetConnectErrorException e)
				{
					e.showErrorToast();
					msg.what = 500;
				}
				return msg;
			}
		}.execute();
	}

	/**
	 * 询问是否更新对话框
	 */
	private void showDoUpdateDialog()
	{

		WebView webView = new WebView(mContext);
		webView.getSettings().setJavaScriptEnabled(true);
		// 设置webView自适应屏幕
		WebSettings settings = webView.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setSupportZoom(true);
		// // 设置出现缩放工具
		// settings.setBuiltInZoomControls(true);
		String html = "<!DOCTYPE html PUBLIC "
				+ "-//W3C//DTD XHTML 1.0 Transitional//EN"
				+ "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
				+ ">"
				+ "<html xmlns="
				+ "http://www.w3.org/1999/xhtml"
				+ ">"
				+ "<head>"
				+ "<meta http-equiv="
				+ "Content-Type"
				+ " content="
				+ "text/html; charset=utf-8"
				+ "/>"
				+ "<meta name='viewport' content='width=decice-width,uer-scalable=no'>"
				+ WEB_STYLE + "<body>" + tversionInfo.data.description + "<br/>"
				+ "文件大小:" + tversionInfo.data.filesize + "</body></html>";
		webView.loadDataWithBaseURL(null, html, mimeType, encoding, null);
		DialogBuildUtils.with()
		                .createHintDialog(mContext)
		                .setTitle("存在可用更新,是否立即更新?")
		                .setContentView(webView)
		                .setLeftButton("以后再说", null)
		                .setRigthButton("更新", new OnClickListener()
						 {
							 @Override
							 public void onClick(View v)
							 {
								 doUpdate();
							 }
						 })
					    .create()
						.show();
	}

	/**
	 * 更新版本信息
	 */
	public void doUpdate()
	{
		manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notif = new Notification();
		// 让通知常驻
		notif.flags |= Notification.FLAG_ONGOING_EVENT;
		notif.icon = R.drawable.ic_launcher;
		notif.tickerText = "正在下载新版本";
		// 通知栏显示所用到的布局文件
		notif.contentView = new RemoteViews(mContext.getPackageName(),R.layout.version_notif);
		manager.notify(10001, notif);
		this.filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zupdate/";
		File file = new File(filePath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		this.filePath += "version_" + tversionInfo.data.version + ".apk";
		System.out.println("the file:"+filePath);
		System.out.println("the url:"+tversionInfo.data.fileurl);
		DownloadThread downloadThread = new DownloadThread(tversionInfo.data.fileurl);
		downloadThread.start();
	}

	/**
	 * 进度提示
	 */
	private Handler progressHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// 如果为0则代表下载进度
			if (msg.what == 0)
			{
				float num = ((float) length / (float) totalSize);
				int currentSize = (int) (num * 100);
				notif.contentView.setTextViewText(R.id.content_view_text1,
						currentSize + "%");
				notif.contentView.setProgressBar(R.id.content_view_progress,
						totalSize, length, false);
				manager.notify(10001, notif);
			}
			// 如果为1则代表下载完成
			else if (msg.what == 1)
			{
				timer.cancel();
				manager.cancel(10001);
				// 询问是否按照该程序
				// 调用系统安装程序组件安装程序
				Intent intents = new Intent(Intent.ACTION_VIEW);
				intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// filePath为文件路径
				intents.setDataAndType(Uri.parse("file://" + filePath),
						"application/vnd.android.package-archive");
				mContext.startActivity(intents);
			}
			// 如果为2则代表下载中断
			else if (msg.what == 2)
			{
				NewDataToast.makeText(mContext, "下载新版本失败,请检查网络是否连接...").show();
				manager.cancel(10001);
			}
		}
	};

	/**
	 * 下载对象
	 */
	private final class DownloadThread extends Thread
	{
		private String url;

		public DownloadThread(String url)
		{
			this.url = url;
		}

		@Override
		public void run()
		{
			try
			{
				URL durl = new URL(url);
				HttpURLConnection urlConnection = (HttpURLConnection) durl.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.connect();
				File file = new File(filePath);
				file.createNewFile();
				FileOutputStream fileOutput = new FileOutputStream(file);
				InputStream inputStream = urlConnection.getInputStream();
				totalSize = urlConnection.getContentLength();
				byte[] buffer = new byte[32];
				int bufferLength = 0;
				timer = new Timer();
				timer.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						progressHandler.sendEmptyMessage(0);
					}
				}, 500, 500);

				while ((bufferLength = inputStream.read(buffer)) != -1)
				{
					fileOutput.write(buffer, 0, bufferLength);
					length += bufferLength;
				}

				fileOutput.flush();
				fileOutput.close();
				progressHandler.sendEmptyMessage(1);
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
				progressHandler.sendEmptyMessage(2);
			} catch (IOException e)
			{
				e.printStackTrace();
				progressHandler.sendEmptyMessage(3);
			}
		}
	}
}
