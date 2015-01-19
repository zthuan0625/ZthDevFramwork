package com.zthdev.exception;

import com.zthdev.app.ZDevAppContext;

/**
 * 
 * 类名称：NetConnectErrorException <br>  
 * 类描述：全局异常处理 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-8-15 下午5:58:55 <br>  
 * @version V1.0
 */
public class NetConnectErrorException extends RuntimeException
{

	/**
	 * 网络连接失败
	 */
	public static final int EEROR_CONN = -10000;
	private static final long serialVersionUID = 1L;

	public NetConnectErrorException()
	{
		super();
	}

	public NetConnectErrorException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	public NetConnectErrorException(String detailMessage)
	{
		super(detailMessage);
	}

	public NetConnectErrorException(Throwable throwable)
	{
		super(throwable);
	}
	
	/**
	 * 显示连接失败的提示框
	 */
	public void showErrorToast()
	{
		ZDevAppContext.getInstance().netErrorHandler.sendEmptyMessage(EEROR_CONN);
	}
}
