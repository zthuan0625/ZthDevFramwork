package com.zthdev.net;

import android.content.Context;

/**
 * 
 * 类名称：ZRequestCreator <br>  
 * 类描述：请求构造器 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-12-24 上午10:55:04 <br>  
 * @version V1.0
 */
public class ZRequestCreator
{

	/**
	 * 构造GET请求实例
	 * @return
	 */
	public static ZHttpGetRequest creatorGet(Context context,String url)
	{
		return new ZHttpGetRequest(context,url);
	}
	
	/**
	 * 构造POST请求实例
	 * @return
	 */
	public static ZHttpPostRequest creatorPost(Context context,String url)
	{
		return new ZHttpPostRequest(context,url);
	}
}
