package com.zthdev.util;

import com.zthdev.framework.R;

import android.content.Context;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 
 * 类名称：SharedUtil <br>  
 * 类描述：分享工具类<br>
 * 创建人  赵腾欢 <br>
 * 创建时间：2014-7-9 下午2:36:53 <br> 
 * @version V1.0
 *
 */
public class SharedUtils
{

	// 使用快捷分享完成分享（请务必仔细阅读位于SDK解压目录下Docs文件夹中OnekeyShare类的JavaDoc）
	/**
	 * ShareSDK集成方法有两种</br>
	 * 1、第一种是引用方式，例如引用onekeyshare项目，onekeyshare项目再引用mainlibs库</br>
	 * 2、第二种是把onekeyshare和mainlibs集成到项目中，本例子就是用第二种方式</br> 请看“ShareSDK
	 * 使用说明文档”，SDK下载目录中 </br> 或者看网络集成文档
	 * http://wiki.sharesdk.cn/Android_%E5%BF%AB
	 * %E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
	 * 3、混淆时，把sample或者本例子的混淆代码copy过去，在proguard-project.txt文件中
	 * 
	 * 
	 * 平台配置信息有三种方式： 1、在我们后台配置各个微博平台的key
	 * 2、在代码中配置各个微博平台的key，http://sharesdk.cn/androidDoc
	 * /cnsdk/framework/ShareSDK.html
	 * 3、在配置文件中配置，本例子里面的assets/ShareSDK.conf,
	 */
	public static void doShare(Context context,String details,String imgURL,boolean silent, String platform)
	{
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ic_launcher, "app_name");
		
		oks.setUrl("http://www.668app.cn");
		
		oks.setComment(context.getString(R.string.share));
		oks.setSite("app_name");
		oks.setTitleUrl("http://www.668app.cn");
		oks.setSiteUrl("http://www.668app.cn");
		oks.setText(details);
		oks.setSilent(silent);
		if(imgURL != null)
		{
			oks.setImageUrl(imgURL);
		}
		
		if (platform != null) 
		{
			oks.setPlatform(platform);
		}

		oks.setDialogMode();

		oks.disableSSOWhenAuthorize();

		oks.show(context);
	}
}
