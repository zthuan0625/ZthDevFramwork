package com.zthdev.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 类名称：BindID <br>  
 * 类描述：视图绑定注解(持有视图ID) <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-7-29 上午10:35:31 <br>  
 * @version V1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindID
{
    int id();
}
