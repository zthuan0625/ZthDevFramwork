package com.zthdev.fragment;

import java.lang.reflect.Field;

import com.zthdev.annotation.BindID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * 类名称：ZthDevFragment <br>  
 * 类描述：视图自动注入Fragment <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-7-14 上午10:13:42 <br>  
 * @version V1.0
 */
public abstract class ZDevFragment extends Fragment
{
	/**
	 * 在该方法里面返回Fragment的布局文件
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
	    View contentView = inflater.inflate(initLayoutView(), null);
	    injectView(contentView);
		initViewListener();
		initData();
		return contentView;
	}
	
	/**
	 * 反射获得所有的控件并判断是否使用了bindID注解
	 * 如果使用则自动匹配相应ID
	 */
	private void injectView(View contentView)
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
					field.set(this, contentView.findViewById(resourid));
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
}
