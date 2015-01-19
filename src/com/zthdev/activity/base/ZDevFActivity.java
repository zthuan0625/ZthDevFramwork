package com.zthdev.activity.base;

import java.lang.reflect.Field;
import com.zthdev.annotation.BindID;
import com.zthdev.app.ZDevAppContext;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * 类名称：ZthDevFActivity <br>  
 * 类描述：支持Fragment的视图自动注入Activity <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-7-14 上午10:11:38 <br>  
 * @version V1.0
 */
public abstract class ZDevFActivity extends FragmentActivity 
{
	/**
	 * 在该方法里面设置Activity的布局文件
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
		setContentView(initLayoutView());
		ZDevAppContext.getInstance().pushActivity(this);
		injectView();
		initData();
		initViewListener();
	}
	
	/**
	 * 反射获得所有的控件并判断是否使用了bindID注解
	 * 如果使用则自动匹配相应ID
	 */
	private void injectView()
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
