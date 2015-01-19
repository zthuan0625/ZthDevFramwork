package com.zthdev.net;

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
 * 类名称：ZHttpGetRequest <br>  
 * 类描述：代表了一次GET请求 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-12-24 上午10:13:01 <br>  
 * @version V1.0
 */
public class ZHttpGetRequest
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

	protected ZHttpGetRequest(Context context,String url)
	{
		this.context = context.getApplicationContext();
		parameterMap = new HashMap<String, String>();
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
	public ZHttpGetRequest setUrl(String url)
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
	public ZHttpGetRequest setValue(String key, String value)
	{
		this.parameterMap.put(key, value);
		return this;
	}
	
	/**
	 * 设置签名
	 * @param accessKey
	 * @return
	 */
	public ZHttpGetRequest setSign(String accessKey)
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
					String response = HttpUtils.doGet(url, parameterMap);
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
