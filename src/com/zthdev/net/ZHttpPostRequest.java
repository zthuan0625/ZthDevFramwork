package com.zthdev.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Message;

import com.zthdev.exception.NetConnectErrorException;
import com.zthdev.util.AsyncExecutor;
import com.zthdev.util.DeviceInfoUtils;
import com.zthdev.util.MD5Utils;

/**
 * 
 * 类名称：ZHttpPostRequest <br>  
 * 类描述：代表了一次POST请求 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-12-24 上午10:46:37 <br>  
 * @version V1.0
 */
public class ZHttpPostRequest
{
	private Context context;
	
	/**
	 * 请求地址
	 */
	private String url;
	
	/**
	 * 请求参数
	 */
	private Map<String, String> parameterMap;
	
	/**
	 * 文件参数
	 */
	private Map<String, File> fileParameterMap;

	protected ZHttpPostRequest(Context context,String url)
	{
		this.context = context.getApplicationContext();
		parameterMap = new HashMap<String, String>();
		fileParameterMap = new HashMap<String, File>();
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}

	/**
	 * 设置请求地址
	 * @param url
	 * @return
	 */
	public ZHttpPostRequest setUrl(String url)
	{
		this.url = url;
		return this;
	}
	
	/**
	 * 设置请求参数
	 * @param key
	 * @param value
	 * @return
	 */
	public ZHttpPostRequest setPostValue(String key, String value)
	{
		this.parameterMap.put(key, value);
		return this;
	}
	
	/**
	 * 设置文件参数
	 * @param key
	 * @param file
	 * @return
	 */
	public ZHttpPostRequest setPostValue(String key, File file)
	{
		this.fileParameterMap.put(key, file);
		return this;
	}
	
	/**
	 * 设置签名
	 * @param accessKey
	 * @return
	 */
	public ZHttpPostRequest setSign(String accessKey)
	{
		String str = MD5Utils.getMd5(accessKey, parameterMap);
		parameterMap.put("sign", str);
		return this;
	}
	
	/**
	 * 开始请求
	 */
	public void doRequest(final ResponseListener listener)
	{
		/**
		 * 如果没有网络连接则直接退出执行
		 */
		if(!DeviceInfoUtils.isNetworkConnected(context))
		{
//			listener.onFinally();
			listener.onFailure();
			return;
		}
		
		//开始异步请求
		new AsyncExecutor()
		{
			@Override
			public void doForegroundTask(Message msg)
			{
//				listener.onFinally();
				if(msg.what == OK)
				{
					listener.onSuccess((String) msg.obj);
				}
				else
				{
					listener.onFailure();
				}
			}
			
			@Override
			public Message doBackgroundTask()
			{
				Message msgMessage = new Message();
				msgMessage.what = FAIL;
				try
				{
					String response = HttpUtils.doPost(url, parameterMap, fileParameterMap);
					if(response != null && response.length() > 0)
					{
						msgMessage.what = OK;
						msgMessage.obj = response;
					}
				}
				catch(NetConnectErrorException e)
				{
				}
				return msgMessage;
			}
		}.execute();
	}
}
