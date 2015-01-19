package com.zthdev.bean;

/**
 * 
 * 类名称：AlipayBean <br>  
 * 类描述：支付宝订单实体类 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-8-18 下午4:58:02 <br>  
 * @version V1.0
 */
public class AlipayBean
{
	/**
	 * 唯一订单号(有后台生成然后获取)
	 */
	public String out_trade_no;
	
	/**
	 * 商品名称
	 */
    public String subject;
    
    /**
     * 商品详情(1000字以内)
     */
    public String body;
    
    /**
     * 付款总金额
     */
    public String total_fee;
    
    /**
     * 服务器异步通知页面路径
     */
    public String notify_url;
    
    /**
     * 如果配置了该属性那么支付完毕后会跳转到该页面
     */
    public String return_url;
}
