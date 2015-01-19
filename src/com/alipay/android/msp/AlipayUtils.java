package com.alipay.android.msp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.alipay.android.app.sdk.AliPay;
import com.zthdev.app.ZDevAppContext;
import com.zthdev.bean.AlipayBean;

/**
 * 
 * 类名称：AlipayUtils <br>
 * 类描述：支付宝支付工具类 <br>
 * 创建人：赵腾欢 创建时间：2014-8-18 下午5:05:15 <br>
 * 
 * @version V1.0
 */
public class AlipayUtils
{
	/**
	 * log日志标签
	 */
	public static final String TAG = "alipay-sdk";

	/**
	 * 标识返回数据为支付类型
	 */
	private static final int RQF_PAY = 1;

	/**
	 * 订单支付
	 * 
	 * @param alipayBean
	 *            订单实体类
	 * @param context
	 *            调用者Activity
	 * @param mHandler
	 *            接收信息回调(该文件最后有格式模板)
	 */
	public static void doPay(AlipayBean alipayBean, final Activity context,
			final Handler mHandler) throws Exception
	{
		ZDevAppContext app = ZDevAppContext.getInstance();

		// 获取拼装好的订单信息
		String info = getNewOrderInfo(alipayBean);

		// 对数据进行私钥签名
		String sign = Rsa.sign(info, app.alipayKeys.PRIVATE);

		// 对数据进行URL转码
		sign = URLEncoder.encode(sign, "UTF-8");

		// 设置签名
		info += "&sign=\"" + sign + "\"&" + getSignType();

		Log.i("ExternalPartner", "start pay");
		// start the pay.
		Log.i(TAG, "info = " + info);

		final String orderInfo = info;
		new Thread()
		{
			public void run()
			{
				AliPay alipay = new AliPay(context, mHandler);

				// 设置为沙箱模式，不设置默认为线上环境
				// alipay.setSandBox(true);

				String result = alipay.pay(orderInfo);

				System.out.println("pay:" + result);
				Message msg = new Message();
				msg.what = RQF_PAY;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 订单支付
	 * 
	 * @param alipayBean
	 *            订单实体类
	 * @param context
	 *            调用者Activity
	 * @param mHandler
	 *            接收信息回调(该文件最后有格式模板)
	 */
	public static void doPay(final String orderInfo, final Activity context,final Handler mHandler) throws Exception
	{
		new Thread()
		{
			public void run()
			{
				AliPay alipay = new AliPay(context, mHandler);

				// 设置为沙箱模式，不设置默认为线上环境
				// alipay.setSandBox(true);

				String result = alipay.pay(orderInfo);

				System.out.println("pay:" + result);
				Message msg = new Message();
				msg.what = RQF_PAY;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 把订单实体类的所有属性封装成请求参数
	 * 
	 * @param alipayBean
	 * @return
	 */
	private static String getNewOrderInfo(AlipayBean alipayBean)
	{
		ZDevAppContext app = ZDevAppContext.getInstance();

		StringBuilder sb = new StringBuilder();
		// 支付宝合作ID
		sb.append("partner=\"");
		sb.append(app.alipayKeys.DEFAULT_PARTNER);

		// 商户网站唯一订单 号
		sb.append("\"&out_trade_no=\"");
		// sb.append(getOutTradeNo());
		sb.append(alipayBean.out_trade_no);

		// 商品名称
		sb.append("\"&subject=\"");
		sb.append(alipayBean.subject);

		// 商品详情(String1000)
		sb.append("\"&body=\"");
		sb.append(alipayBean.body);

		// 付款总金额
		sb.append("\"&total_fee=\"");
		sb.append(alipayBean.total_fee);

		// 接口名称。固定值。这里是手机安全支付
		sb.append("\"&service=\"mobile.securitypay.pay");

		// 参数编码字符集
		sb.append("\"&_input_charset=\"UTF-8");
				
		try
		{
			// 服务器异步通知页面路径
			sb.append("\"&notify_url=\"");
		    if (alipayBean.notify_url != null)
		    {
			     // 网址需要做URL编码
			     sb.append(URLEncoder.encode(alipayBean.notify_url,"UTF-8"));
		    } else
		    {
			     // 网址需要做URL编码
			     sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp","UTF-8"));
		     }
		    // 如果配置了该属性那么支付完毕后会跳转到该页面
			sb.append("\"&return_url=\"");
			if (alipayBean.return_url != null)
			{
				// 网址需要做URL编码
				sb.append(URLEncoder.encode(alipayBean.return_url,"UTF-8"));
			} else
			{
				// 网址需要做URL编码
				sb.append(URLEncoder.encode("http://m.alipay.com","UTF-8"));
			}
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// 支付类型,默认值为:1(商品购买)。
		sb.append("\"&payment_type=\"1");

		// 商家支付宝账号
		sb.append("\"&seller_id=\"");
		sb.append(app.alipayKeys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");

		// 未付款交易的超时时间
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	// /**
	// * 生成唯一订单号
	// * @return
	// */
	// private static String getOutTradeNo()
	// {
	// SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
	// Date date = new Date();
	// String key = format.format(date);
	//
	// java.util.Random r = new java.util.Random();
	// key += r.nextInt();
	// key = key.substring(0, 15);
	// Log.d(TAG, "outTradeNo: " + key);
	// return key;
	// }

	/**
	 * 返回请求数据的加密方式(默认RSA)
	 * 
	 * @return
	 */
	private static String getSignType()
	{
		return "sign_type=\"RSA\"";
	}

	// Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// Result result = new Result((String) msg.obj);
	//
	// switch (msg.what) {
	// case RQF_PAY:
	// Toast.makeText(ExternalPartner.this, result.getResult(),
	// Toast.LENGTH_SHORT).show();
	// break;
	// }
	// };
	// };

}