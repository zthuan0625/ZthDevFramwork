package com.zthdev.version;

import com.zthdev.bean.VersionInfo;
import com.zthdev.exception.NetConnectErrorException;
import com.zthdev.net.HttpUtils;
import com.zthdev.util.BeanUtils;

/**
 * 
 * 项目名称：ChellonaCarV0620<br>
 * 类名称：VersionService <br>  
 * 类描述：版本更新工具类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-6-23 上午9:31:41 <br> 
 * @version V1.0
 */
public class VersionService
{
	/**
	 * 版本更新检查,返回版本信息
	 * @param path 检查更新路径
	 * @return
	 * @throws NetConnectErrorException
	 */
	public VersionInfo getNewVersionInfo(String path) throws NetConnectErrorException
	{
		VersionInfo versionInfo = null;
		String response = HttpUtils.doGet(path, null);
		if ("null".equals(response))
		{
			return null;
		}
		
		// 如果返回的数据以{或者[结尾那么就是一个合法的json数据,如果不是这两张形式开头，那么就代表没有该数据
		if (response.startsWith("{") || response.startsWith("["))
		{
			versionInfo = (VersionInfo) BeanUtils.json2Bean(response, VersionInfo.class);	
		}
		return versionInfo;
	}
}
