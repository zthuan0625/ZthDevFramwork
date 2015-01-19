package com.zthdev.bean;

/**
 * 
 * 类名称：NameValuePair <br>  
 * 类描述：键值对 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-15 下午2:26:41 <br>  
 * @version V1.0
 */
public class NameValuePair
{
	public Object key;
	public Object value;
	
	public NameValuePair(){}
	
	public NameValuePair(Object key,Object value)
	{
		this.key = key;
		this.value = value;
	}
	
	public boolean isContainKey(Object key)
	{
		if(key instanceof String)
		{
			if(this.key.equals(key))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(this.key == key)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public Object getValue(String key)
	{
		if(this.key.equals(key))
		{
			return this.value;
		}
		else
		{
			return null;
		}
	}
}
