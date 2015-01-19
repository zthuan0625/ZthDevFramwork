package com.zthdev.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 
 * 类名称：GlobalValueUtils <br>  
 * 类描述：全局变量存取类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-5 下午3:50:37 <br>  
 * @version V1.0
 */
public class GlobalValueUtils
{
	private HashMap<String, SoftReference<Object>> referenceMap;
	
	private GlobalValueUtils()
	{
		referenceMap = new HashMap<String, SoftReference<Object>>();
	}
	
	private static class Hold
	{
		private static final GlobalValueUtils global = new GlobalValueUtils();
	}
	
	/**
	 * 获取单例对象
	 * @param context
	 * @return
	 */
	public static GlobalValueUtils getInstance()
	{
		return Hold.global;
	}
	
	/**
	 * 存入变量
	 * @param key
	 * @param obj
	 */
	public void putRef(String key, Object obj)
	{
		referenceMap.put(key, new SoftReference<Object>(obj));
	}
	
	/**
	 * 返回变量
	 * @param key
	 * @return
	 */
	public Object getObj(String key)
	{
		if(!referenceMap.containsKey(key))
			return null;
		
		SoftReference<Object> ref = referenceMap.get(key);
		return ref.get();
	}
	
	/**
	 * 删除变量
	 * @param key
	 */
	public void removeRef(String key)
	{
		referenceMap.remove(key);
	}
}
