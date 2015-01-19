package com.zthdev.bean;

import java.io.Serializable;

/**
 * 
 * 项目名称：ChellonaCarV0620<br>
 * 类名称：VersionInfo <br>  
 * 类描述：程序版本信息实体类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-6-23 上午9:43:50 <br> 
 * @version V1.0
 */
public class VersionInfo implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	public Header header;
	public Data data;
	
	
	public class Header implements Serializable
	{
		
		private static final long serialVersionUID = 1L;
		public String state;
	}
	
	public class Data implements Serializable
	{
		
		private static final long serialVersionUID = 1L;
		public String name;
		public String fileurl;
		   public String version;
		   public String description;
		   public String filesize;
	}
   
}
