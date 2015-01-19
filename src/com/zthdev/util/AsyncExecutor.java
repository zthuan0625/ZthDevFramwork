package com.zthdev.util;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * 类名称：AsyncExecutor <br>
 * 类描述：异步执行器(用于处理耗时操作等等,AsyncTask的简化版) <br>
 * 创建人：赵腾欢 创建时间：2014-7-13 下午1:07:15 <br>
 * 
 * @version V1.0
 */
public abstract class AsyncExecutor
{
	/**
	 * 标识后台任务执行成功
	 */
	public int OK = 202;
	
	/**
	 * 标识后台任务执行失败
	 */
	public int FAIL = 404;
	
	/**
	 * 前台任务调用者
	 */
	private Handler postHandler;

	public AsyncExecutor()
	{
		/**
		 * 初始化前台Handler
		 */
		postHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				// 调用前台方法
				doForegroundTask(msg);
			}
		};
	}

	/**
	 * 该方法在后台任务执行完毕后运行,用来处理如UI的数据绑定等操作
	 * 
	 * @param status
	 *            状态码,用户自定义(来实现后台执行情况的判断)
	 */
	public abstract void doForegroundTask(Message status);
	
	/**
	 * 在该方法中实现后台耗时操作
	 * 
	 * @return
	 */
	public abstract Message doBackgroundTask();

	/**
	 * 开始执行异步任务(相当于Thread的start方法)
	 */
	public void execute()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Message status = doBackgroundTask();
				postHandler.sendMessage(status);
			}
		}).start();
	}
}
