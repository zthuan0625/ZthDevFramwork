package com.zthdev.activity.base;

import java.lang.reflect.Field;
import com.zthdev.annotation.BindID;
import com.zthdev.app.ZDevAppContext;
import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * 类名称：ZthDevActivity <br>  
 * 类描述：视图自动注入Activity <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-7-14 上午10:12:42 <br>  
 * @version V1.0
 */
public abstract class ZDevActivity extends Activity
{
	/**
	 * 在该方法里面返回Activity的布局文件(如果不需要则返回-1)
	 */
	public abstract int initLayoutView();
	
	/**
	 * 初始化数据
	 */
	public abstract void initData();
	
	/**
	 * 在该方法里面定义需要自定义的监听事件
	 */
	public abstract void initViewListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		int resid = initLayoutView();
		if(resid > -1)
		{
			setContentView(initLayoutView());
		}
		ZDevAppContext.getInstance().pushActivity(this);
		injectView();
		initData();
		initViewListener();
	}
	
	/**
	 * 反射获得所有的控件并判断是否使用了bindID注解
	 * 如果使用则自动匹配相应ID
	 */
	protected void injectView()
	{
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field field : fields)
		{
			field.setAccessible(true);
			//判断该字段是否存在指定的注解
			if(field.isAnnotationPresent(BindID.class))
			{
				BindID inject = field.getAnnotation(BindID.class);
				int resourid = inject.id();
				try
				{
					field.set(this, findViewById(resourid));
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ZDevAppContext.getInstance().popActivity(this);
	}
}
