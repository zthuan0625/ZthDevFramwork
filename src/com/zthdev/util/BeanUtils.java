package com.zthdev.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import com.google.gson.Gson;

/**
 * 
 * 类名称：BeanUtil <br>
 * 类描述：JavaBean工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-6-16 下午10:12:04 <br>
 * 
 * @version V1.0
 */
public class BeanUtils
{
	/**
	 * json转换为JavaBean
	 * 
	 * @param json
	 *            json数据
	 * @param clazz
	 *            JavaBean的字节码
	 * @return
	 */
	public static <T> T json2Bean(String json, Class<T> clazz)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}

	/**
	 * json转换为集合
	 * 
	 * @param json
	 *            json数据
	 * @param type
	 *            java.lang.reflect.Type
	 *            格式为new TypeToken<List<这里填入类型>>(){}.getType()
	 * @return
	 */
	public static <T> List<T> json2List(String json,Type type)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, type);
	}
	
	/**
	 * 对象转JSON
	 * @param t
	 *         待转换对象
	 * @return
	 */
	public static <T> String bean2Json(T t)
	{
		if(t == null)
		{
			return null;
		}
		
		Gson gson = new Gson();
		return gson.toJson(t);
	}
	
	/**
	 * 数据库ORM
	 * Note:需要注意的是实体类的成员名称必须与表字段的名称保持一致,
	 *      且该方法必须设置isNeedMoveCursor属性来告诉方法是否移动cursor
	 *      这样做的目的在于对于只有单个行的表,cursor只需移动一次,这里可以设置为true。
	 *      对于有多行的表,cursor需要移动N次,这里应该设置为false。但是这里不需要调用者
	 *      关心。对于有多行的表,应该调用cursor2BeanList方法获取一个集合
	 * @param cursor
	 *            游标对象
	 * @param clazz
	 *            对应JavaBean的字节码
	 * @param isNeedMoveCursor
	 *            是否需要移动游标         
	 * @param isCloseCursor
	 *            方法结束后是否关闭cursor(设置该参数完全是为了兼容cursor2BeanList方法)
	 *            对于调用者调用该方法,尽量设置为true                    
	 * @return
	 *         返回指定对象或者返回Null
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T cursor2Bean(Cursor cursor, Class<T> clazz,
			                        boolean isNeedMoveCursor,boolean isCloseCursor)
			                                    throws IllegalAccessException,InstantiationException
	{
		if(isNeedMoveCursor)
		{
			//如果移动光标失败则直接返回null
			if(!cursor.moveToFirst())
				return null;
		}
		
		//获取该类型的实例
		T targetObj = clazz.newInstance();
		
		//获得该类型的成员变量集合
		Field[] fields = clazz.getDeclaredFields();
		
		//遍历成员变量集合
		for (Field field : fields)
		{
			field.setAccessible(true);
			
			//获得该成员变量的类型
			Class<?> fieldType = field.getType();
			
			//获得该成员变量在数据表中对应的columnID
			int index = cursor.getColumnIndex(field.getName());
			
			//判断如果该属性在数据表中不存在则跳过给该字段赋值的操作,进入到下一个字段
			if( -1 == index)
			{
				continue;
			}
			
			// 判断该成员属性的类型,根据类型选择适当的赋值方法
			//判断属性是否为Integer类型
			if ((Integer.TYPE == fieldType) || (Integer.class == fieldType))
			{
				field.set(targetObj,cursor.getInt(index));
			} 
			//判断属性是否为String类型
			else if (String.class == fieldType)
			{
				field.set(targetObj, cursor.getString(index));
			} 
			else if ((Long.TYPE == fieldType) || (Long.class == fieldType))
			{
				field.set(targetObj, Long.valueOf(cursor.getLong(index)));
			} 
			else if ((Float.TYPE == fieldType) || (Float.class == fieldType))
			{
				field.set(targetObj, Float.valueOf(cursor.getFloat(index)));
			} 
			else if ((Short.TYPE == fieldType) || (Short.class == fieldType))
			{
				field.set(targetObj, Short.valueOf(cursor.getShort(index)));
			} 
			else if ((Double.TYPE == fieldType)
					|| (Double.class == fieldType))
			{
				field.set(targetObj, Double.valueOf(cursor.getDouble(index)));
			} 
			else if (Blob.class == fieldType)
			{
				field.set(targetObj,cursor.getBlob(index));
			} 
			else if (Date.class == fieldType)
			{// 处理java.util.Date类型,update 2012-06-10
				Date date = new Date();
				date.setTime(cursor.getLong(index));
				field.set(targetObj, date);
			} 
			else if (Character.TYPE == fieldType)
			{
				String fieldValue = cursor.getString(index);
				if ((fieldValue != null) && (fieldValue.length() > 0))
				{
					field.set(targetObj,Character.valueOf(fieldValue.charAt(0)));
				}
			}
		}
		//判断是否需要关闭游标
		if(isCloseCursor)
			cursor.close();
		return targetObj;
	}
	
	/**
	 * 数据库ORM(返回对象集合)
	 * 需要注意的是实体类的成员名称必须与表字段的名称保持一致
	 * @param cursor
	 *            游标对象
	 * @param clazz
	 *            对应JavaBean的字节码
	 * @return
	 *         返回指定对象集合或者返回一个空集合(获得集合之后应该调用size方法判断是否集合为空)
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> List<T> cursor2BeanList(Cursor cursor, Class<T> clazz) throws IllegalAccessException, 
	                                                                                InstantiationException
	{
		List<T> tList = new ArrayList<T>();
		while(cursor.moveToNext())
		{
			T t = cursor2Bean(cursor, clazz, false,false);
			if(t != null)
			{
				tList.add(t);
			}
		}
		return tList;
	}
	
	/**
	 * 将Bean中的数据封装到map集合中
	 * 
	 * @param bean 实体类
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> Map<String,Object> bean2Map(T bean) throws IllegalArgumentException, 
	                                                              IllegalAccessException
	{
		
		Map<String,Object> temp = new HashMap<String,Object>();
		
		Field[] fields = bean.getClass().getFields();
		
		for (Field field : fields)
		{
			temp.put(field.getName(), field.get(bean));
		}
		
		return temp;
	}
}
