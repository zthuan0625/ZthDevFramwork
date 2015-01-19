package com.zthdev.net;

/**
 * 
 * 类名称：ResponseListener <br>
 * 类描述：请求回调 <br>
 * 创建人：赵腾欢 创建时间：2014-12-24 上午10:31:47 <br>
 * 
 * @version V1.0
 */
public interface ResponseListener
{
	/**
	 * 请求成功 
	 * @param response
	 */
	public void onSuccess(String response);

	/**
	 * 无论成功失败都要调用的方法
	 */
	// public void onFinally();
	
	/**
	 * 请求失败(一般是请求超时)
	 */
	public void onFailure();
}
