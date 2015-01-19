package com.zthdev.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 类名称：MD5 <br>
 * 类描述：生成唯一标识 <br>
 * 创建人：赵腾欢
 * 
 * @version V1.0
 */
public class MD5Utils
{

	/**
	 * 返回MD5码
	 * 
	 * @param content
	 *            需要转换的内容
	 * @return
	 */
	public static String getMD5(String content)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return getHashString(digest);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static String getHashString(MessageDigest digest)
	{
		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest())
		{
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString();
	}

	public static String getMd5(String accessKey, Map<String, String> params)
	{
		Set<String> set = params.keySet();
		String[] keys = new String[set.size()];
		set.toArray(keys);
		Arrays.sort(keys, new MyComparator());
		StringBuffer signVal = new StringBuffer();
		for (String key : keys)
		{
			String value = params.get(key);
			signVal.append(key).append(value);
		}
		String sign = getMD5Str(signVal + accessKey, "utf-8");
		return sign;
	}

	/**
	 * MD5 加密
	 */
	public static String getMD5Str(String str, String enc)
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes(enc));// "gb2312"
		} catch (NoSuchAlgorithmException e)
		{
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++)
		{
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

}
