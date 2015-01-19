package com.zthdev.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
 * 类名称：ScrollListView <br>  
 * 类描述：可以嵌入ScrollView的ListView <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-9-26 下午2:53:36 <br>  
 * @version V1.0
 */
public class ScrollListView extends ListView
{
	public ScrollListView(Context context)
	{
		super(context);
	}

	public ScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScrollListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
