package com.zthdev.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 类名称：ImageFolder <br>  
 * 类描述：代表一个相册目录 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-7 上午9:35:20 <br>  
 * @version V1.0
 */
public class ImageFolder implements Serializable
{
	private static final long serialVersionUID = 1L;
	public int count = 0;
	public String bucketName;
	public ArrayList<ImageItem> imageList;

}
