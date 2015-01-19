package com.zthdev.util;

import java.lang.reflect.Field;

import com.zthdev.annotation.BindID;

import android.view.View;

/**
 * 
 * 类名称：InjectUtil <br>  
 * 类描述：视图注入工具 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-7-13 下午12:35:08 <br>  
 * @version V1.0
 */
public class InjectUtils
{
   /**
	* 视图注入
	* @param targetObj
	* @param contentView
	*/
   public static void injectView(Object targetObj,View contentView)
   {
	    Field[] fields = targetObj.getClass().getDeclaredFields();
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
					field.set(targetObj, contentView.findViewById(resourid));
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
