package com.zthdev.bean;

import java.io.Serializable;

/**
 * 
 * 类名称：ImageItem <br>  
 * 类描述：图片信息 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-7 上午9:35:45 <br>  
 * @version V1.0
 */
public class ImageItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
