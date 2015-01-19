package com.zthdev.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 
 * 类名称：ScrollGridView <br>  
 * 类描述：可以嵌入ScrollView的网格布局 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-10-20 下午2:07:17 <br>  
 * @version V1.0
 */
public class ScrollGridView extends GridView
{
	public ScrollGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScrollGridView(Context context)
	{
		super(context);
	}

	public ScrollGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	// 该自定义控件只是重写了GridView的onMeasure方法，使其不会出现滚动条，ScrollView嵌套ListView也是同样的道理，不再赘述。
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
