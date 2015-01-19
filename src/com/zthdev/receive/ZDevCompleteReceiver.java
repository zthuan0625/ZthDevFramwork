package com.zthdev.receive;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * 接收系统下载组件下载完毕的广播(版本更新时使用)
 */
public class ZDevCompleteReceiver extends BroadcastReceiver
{
	private DownloadManager downloadManager;
	
	//下载任务唯一ID
	public long downloadID;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
		{
			// 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
			long id = intent.getLongExtra(
					  DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if(downloadID != id)
			{
				return;
			}
			Query query = new Query();
			query.setFilterById(id);
			downloadManager = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Cursor cursor = downloadManager.query(query);

			int columnCount = cursor.getColumnCount();
			
			// 这里把所有的列都打印一下，有什么需求，就怎么处理,文件的本地路径就是path
			String path = null; 
			
			while (cursor.moveToNext())
			{
				for (int j = 0; j < columnCount; j++)
				{
					String columnName = cursor.getColumnName(j);
					String string = cursor.getString(j);
					if (columnName.equals("local_uri"))
					{
						path = string;
						break;
					}
				}
			}
			cursor.close();
			// 如果sdcard不可用时下载下来的文件，那么这里将是一个内容提供者的路径，这里打印出来，有什么需求就怎么样处理
			if (path.startsWith("content:"))
			{
				cursor = context.getContentResolver().query(
						Uri.parse(path), null, null, null, null);
				columnCount = cursor.getColumnCount();
				while (cursor.moveToNext())
				{
					for (int j = 0; j < columnCount; j++)
					{
						String columnName = cursor.getColumnName(j);
						String string = cursor.getString(j);
						if (columnName.equals("_data"))
						{
							path = string;
							break;
						}
					}
				}
				cursor.close();
			}
			//调用系统安装程序组件安装程序
			Intent intents= new Intent(Intent.ACTION_VIEW);  
			intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			//filePath为文件路径  
			intents.setDataAndType(Uri.parse("file:///"+path), "application/vnd.android.package-archive");  
			context.startActivity(intents); 
		}
	}
}
