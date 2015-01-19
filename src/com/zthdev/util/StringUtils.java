package com.zthdev.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * 
 * 类名称：StringUtils <br>
 * 类描述：字符串工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-6-27 上午10:55:44 <br>
 * 
 * @version V1.0
 */
public class StringUtils
{

	// 定义html编码格式
	public final static String mimeType = "text/html";
	public final static String encoding = "utf-8";

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} "
			+ "a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;"
			+ "border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;"
			+ "border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px "
			+ "solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;"
			+ "padding:2px 4px;white-space:nowrap;}</style>";

	public static String getHtml(String content)
	{
		String html = "<!DOCTYPE html PUBLIC "
				+ "-//W3C//DTD XHTML 1.0 Transitional//EN"
				+ "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
				+ ">"
				+ "<html xmlns="
				+ "http://www.w3.org/1999/xhtml"
				+ ">"
				+ "<head>"
				+ "<meta http-equiv="
				+ "Content-Type"
				+ " content="
				+ "text/html; charset=utf-8"
				+ "/>"
				+ "<meta name='viewport' content='width=decice-width,uer-scalable=no'>"
				+ WEB_STYLE + "<body>" + content + "</body></html>";
		return html;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input)
	{
		if (input == null || "".equals(input) || "null".equalsIgnoreCase(input))
			return true;

		for (int i = 0; i < input.length(); i++)
		{
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否为一个合法的Json文件(不为json的依据就是null或者不以'{'和'['开头)
	 * 
	 * @param response
	 *            json字符串
	 * @return
	 */
	public static boolean isNoEmptyAndIsJson(String response)
	{
		if (isEmpty(response))
		{
			return false;
		}

		if ("null".equals(response))
		{
			return false;
		}

		// 如果返回的数据以{或者[结尾那么就是一个合法的json数据,如果不是这两张形式开头，那么就代表没有该数据
		if (response.startsWith("{") || response.startsWith("["))
		{
			return true;
		}

		return false;
	}

	/**
	 * 验证手机号码格式是否合法
	 */
	public static boolean validateMobileNO(String mobiles)
	{
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	// 判断email格式是否正确
	public static boolean validateEmail(String email)
	{
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 身份证
	 * 
	 * @param id
	 * @return
	 */
	public static boolean validateCardID(String id)
	{
		int[] ary =
		{ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] ch =
		{ '1', '2', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int sum = 0;
		char data;
		switch (id.length())
		{
		case 17:
			char[] ary1 = id.toCharArray();
			for (int i = 0; i < ary1.length; i++)
			{
				sum += (ary1[i] - '0') * ary[i];
			}
			data = ch[sum % 11];
			return false;

		case 18:
			char[] ary2 = id.toCharArray();
			for (int i = 0; i < ary2.length - 1; i++)
			{
				sum += (ary2[i] - '0') * ary[i];
			}
			data = ch[sum % 11];
			char lastNum = id.charAt(17);
			lastNum = lastNum == 'x' ? 'X' : lastNum;
			if (data == lastNum)
			{
				return true;
			}
			char[] ary3 = new char[17];
			for (int i = 0; i < id.length() - 1; i++)
			{
				ary3[i] = ary2[i];
			}
			return false;

		default:
			return false;
		}
	}

	/**
	 * 计算两个时间段的月份差(时间格式例子:2014-09)
	 * 
	 * @param currentDate
	 *            当前时间
	 * @param oldDate
	 *            以前时间
	 * @return
	 * @throws ParseException
	 */
	public static int calculateMonthIn(String currentDate, String oldDate)
	{
		try
		{
			int result = 0;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();

			c1.setTime(sdf.parse(currentDate));
			c2.setTime(sdf.parse(oldDate));

			result = (c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) * 12
					+ c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);

			return result;
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 时间戳转时间
	 * 
	 * @param time
	 * 
	 * @return
	 */
	public static String getStrTime(String time)
	{
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals(""))
		{
			return "";
		}
		sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}

	/**
	 * 时间戳转时间
	 * @param time
	 *           时间戳
	 * @param pattern
	 *           时间格式
	 * @return
	 */
	public static String getStrTime(String time,String pattern)
	{
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals(""))
		{
			return "";
		}
		sdf = new SimpleDateFormat(pattern);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time));
		return re_StrTime;
	}
	
	/**
	 * 将时间戳转为代表"距现在多久之前"的字符串
	 * 
	 * @param timeStr
	 *            时间戳
	 * @return
	 */
	public static String getStandardDate(String timeStr)
	{

		StringBuffer sb = new StringBuffer();

		long t = Long.parseLong(timeStr);
		long time = System.currentTimeMillis() - (t * 1000);
		long mill = (long) Math.ceil(time / 1000);// 秒前

		long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

		long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

		long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

		if (day - 1 > 0)
		{
			sb.append(day + "天");
		} else if (hour - 1 > 0)
		{
			if (hour >= 24)
			{
				sb.append("1天");
			} else
			{
				sb.append(hour + "小时");
			}
		} else if (minute - 1 > 0)
		{
			if (minute == 60)
			{
				sb.append("1小时");
			} else
			{
				sb.append(minute + "分钟");
			}
		} else if (mill - 1 > 0)
		{
			if (mill == 60)
			{
				sb.append("1分钟");
			} else
			{
				sb.append(mill + "秒");
			}
		} else
		{
			sb.append("刚刚");
		}
		if (!sb.toString().equals("刚刚"))
		{
			sb.append("前");
		}
		return sb.toString();
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @param oldTime
	 *            以前时间
	 * @param currentTime
	 *            当前时间
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(Date oldTime, Date currentTime)
	{
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long time1 = oldTime.getTime();
		long time2 = currentTime.getTime();
		long diff;
		if (time1 < time2)
		{
			diff = time2 - time1;
		} else
		{
			long[] times =
			{ 0, 0, 0, 0 };
			return times;
		}
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long[] times =
		{ day, hour, min, sec };
		return times;
	}

	/**
	 * 过滤html代码
	 * 
	 * @param input
	 * @return
	 */
	public static String Html2Text(String input, int length)
	{
		if (input == null || input.trim().equals(""))
		{
			return "";
		}
		// 去掉所有html元素,
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		return str;
	}

	/**
	 * 改变TextView字体颜色(可局部改变一段文字颜色)
	 * 
	 * @param str
	 *            需要改变的文字
	 * @param color
	 *            需要改变的颜色
	 * @param textSize
	 *            字体大小
	 * @return
	 */
	public static SpannableStringBuilder changeColor(String str, int color,
			int textSize)
	{
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(color), 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new AbsoluteSizeSpan(textSize), 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return style;
	}

	/**
	 * 生成6位随机数
	 */
	public static String getRandomCode()
	{
		Random rd = new Random();
		String n = "";
		int getNum;
		do
		{
			getNum = Math.abs(rd.nextInt()) % 10 + 48;// 产生数字0-9的随机数
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n += dn;
		} while (n.length() < 6);
		return n;
	}
	
	/**
	 * 4舍5入
	 * @param number
	 *             待处理的数
	 * @param decimalDigits
	 *             保留的小数点后位数
	 * @return
	 */
	public static double getDecimal(double number,int decimalDigits)
	{
	    int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.   
	    BigDecimal bd = new BigDecimal(number);   
	    bd = bd.setScale(decimalDigits,roundingMode); 
		return bd.doubleValue();
	}
}
