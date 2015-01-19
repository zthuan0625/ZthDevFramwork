package com.zthdev.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import com.zthdev.exception.NetConnectErrorException;

/**
 * 
 * 类名称：HttpRequestUtil <br>
 * 类描述：HTTP请求的包装工具类 <br>
 * 创建人：赵腾欢 <br>
 * 创建时间：2014-6-13 上午11:51:35 <br>
 * 
 * @version V1.0
 */

public class HttpUtils
{
	/**
	 * 当网络连接失败时,默认只重新连接2次
	 */
	public static final int TIMECOUNT = 2;

	/**
	 * 重新请求时间间隔(毫秒)
	 */
	public static final int SLEEPTIME = 200;

	/**
	 * 编码方式
	 */
	public static final String Charset = "UTF-8";

	/**
	 * 多少秒超时
	 */
	private final static int TIMEOUT_CONNECTION = 10 * 1000;
	private final static int TIMEOUT_SOCKET = 10 * 1000;

	private HttpUtils()
	{
	}

	/**
	 * 获取HttpClient
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient()
	{
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(Charset);
		return httpClient;
	}

	/**
	 * 返回Get请求实体
	 * 
	 * @param url
	 * @return
	 */
	private static GetMethod getHttpGet(String url)
	{
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		return httpGet;
	}

	/**
	 * 返回Post请求实体
	 * 
	 * @param url
	 * @return
	 */
	private static PostMethod getHttpPost(String url)
	{
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		return httpPost;
	}

	/**
	 * 
	 * 基于HttpClient的GET请求
	 * 
	 * @param path
	 *            请求路径
	 * @param parameterMap
	 *            请求参数(如果没有则传null)
	 * @return 返回类型为字符串
	 */
	public static String doGet(String path, Map<String, String> parameterMap)
	{
		// 如果请求路径为空则退出执行
		if (path == null)
		{
			return null;
		}

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		int requestTimeCount = 0;// 当前请求的次数
		String responseBody = "";
		do
		{
			try
			{
				if (parameterMap != null && !parameterMap.isEmpty())
				{
					path = path + "?" + jointParameter(parameterMap);
				}
				httpClient = getHttpClient();
				httpGet = getHttpGet(path);
				int statusCode = httpClient.executeMethod(httpGet);
				// 判断是否请求成功
				if (statusCode == HttpStatus.SC_OK)
				{
					responseBody = httpGet.getResponseBodyAsString();
					return responseBody;
				} else
				{
					throw new NetConnectErrorException();
				}
			} catch (Exception e)
			{
				++requestTimeCount;// 如果连接失败则连接次数加1
				if (requestTimeCount <= TIMECOUNT)
				{
					try
					{
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e1)
					{
					}
					continue;
				}
				throw new NetConnectErrorException();
			}finally
			{
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (requestTimeCount <= TIMECOUNT);
		return responseBody;
	}

	/**
	 * 上传文件(可上传文字参数和文件)
	 * 
	 * @param path
	 *            请求地址
	 * @param parameterMap
	 *            请求文字参数
	 * @param fileParameterMap
	 *            上传文件参数
	 * @return
	 */
	public static String doPost(String path, Map<String, String> parameters,Map<String, File> files)
	{
		// 如果请求路径或者请求参数为空则退出执行
		if (path == null || (parameters == null && files == null))
		{
			return null;
		}

		// post表单参数处理
		int length = (parameters == null ? 0 : parameters.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (parameters != null)
			for (String name : parameters.keySet())
			{
				parts[i++] = new StringPart(name, parameters.get(name), Charset);
			}
		if (files != null)
			for (String file : files.keySet())
			{
				try
				{
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}

		HttpClient httpClient = null;
		PostMethod postMethod = null;
		String responseBody = "";
		int requestTimeCount = 0;// 当前请求的次数
		do
		{
			try
			{
				// 使用HttpPost对象设置发送的URL路径
				postMethod = getHttpPost(path);
				httpClient = getHttpClient();
				// 发送请求体
				postMethod.setRequestEntity(new MultipartRequestEntity(parts,postMethod.getParams()));
				int statusCode = httpClient.executeMethod(postMethod);
				// 判断是否请求成功
				if (statusCode == HttpStatus.SC_OK)
				{
					responseBody = postMethod.getResponseBodyAsString();
					return responseBody;
				} else
				{
					throw new NetConnectErrorException();
				}
			} catch (Exception e)
			{
				++requestTimeCount;// 如果连接失败则连接次数加1
				if (requestTimeCount <= TIMECOUNT)
				{
					try
					{
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e1)
					{
					}
					continue;
				}
				throw new NetConnectErrorException();
			} finally
			{
				// 释放连接
				postMethod.releaseConnection();
				httpClient = null;
			}
		} while (requestTimeCount <= TIMECOUNT);
		return responseBody;
	}

	/**
	 * 请求服务端,直接返回输入流(适合读取图片和文件)
	 * 
	 * @param path
	 *            请求路径
	 * @param parameterMap
	 *            请求参数集合
	 * @return
	 */
	public static InputStream doGetReturnStream(String path,Map<String, String> parameterMap)
	{
		// 如果请求路径为空则退出执行
		if (path == null)
		{
			return null;
		}

		int requestTimeCount = 0;// 当前请求的次数
		do
		{
			try
			{
				URL url = null;
				if (parameterMap != null)
				{
					url = new URL(path + "?" + jointParameter(parameterMap));
				} else
				{
					url = new URL(path);
				}

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// 设置5秒超时
				conn.setConnectTimeout(TIMEOUT_CONNECTION);
				// 连接
				conn.connect();

				// 判断是否请求成功(状态码200表示成功)
				if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK)
				{
					// 判断是否需要直接返回输入流
					return conn.getInputStream();
				}

			} catch (MalformedURLException e)
			{
				// 如果连接失败则连接次数加1
				++requestTimeCount;

				// 如果连接次数没有超过最大限制则重新连接
				if (requestTimeCount <= TIMECOUNT)
				{
					try
					{
						// 线程休眠1秒钟
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e1)
					{
					}
					continue;
				}
				throw new NetConnectErrorException();
			} catch (IOException e)
			{
				++requestTimeCount;// 如果连接失败则连接次数加1
				if (requestTimeCount <= TIMECOUNT)
				{
					try
					{
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e1)
					{
					}
					continue;
				}
				throw new NetConnectErrorException();
			}
		} while (requestTimeCount <= TIMECOUNT);
		return null;
	}

	/**
	 * 拼接参数
	 * 
	 * @param parameterMap
	 *            参数集合
	 * @return
	 */
	private static String jointParameter(Map<String, String> parameterMap)
	{
		StringBuilder builder = new StringBuilder();
		if (parameterMap != null)
		{
			for (Map.Entry<String, String> entry : parameterMap.entrySet())
			{
				try
				{
					builder.append(entry.getKey());
					builder.append("=");
					builder.append(URLEncoder.encode(entry.getValue(), Charset));
					builder.append("&");
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		String parame = builder.toString();
		return parame;
	}
}
