package com.zthdev.custom.view;

import java.util.List;
import java.util.Map;
import com.zthdev.framework.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 
 * 类名称：TabPageIndicator <br>  
 * 类描述：导航条控件
 *        (在使用这个控件的activity清单文件声明中必须使用
 *        android:theme="@style/Theme.PageIndicatorDefaults"这个主题) <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-11-11 上午10:28:56 <br>  
 * @version V1.0
 */
public class TabPageIndicator extends HorizontalScrollView
{
	/**
	 * 当设置tab的时候,如果没有显示文字,则默认为空
	 */
	private static final CharSequence EMPTY_TITLE = "";

	/**
	 * tab的选中事件接口
	 */
	public interface OnTabSelectedListener
	{
		void onTabSelected(int position,TabView tabView);
	}

	/**
	 * tab左右滑动动画线程
	 */
	private Runnable mTabSelector;

	/**
	 * tab点击事件
	 */
	private final OnClickListener mTabClickListener = new OnClickListener()
	{
		public void onClick(View view)
		{
			TabView tabView = (TabView) view;
			final int newSelected = tabView.getIndex();
			setCurrentItem(newSelected);
			if (mTabSelectedListener != null)
			{
				mTabSelectedListener.onTabSelected(newSelected,tabView);
			}
		}
	};
	/**
	 * 存放tab的容器
	 */
	private final IcsLinearLayout mTabLayout;

	/**
	 * tab组的最大宽度
	 */
	private int mMaxTabWidth;
	
	/**
	 * 当前选择的tab索引
	 */
	private int mSelectedTabIndex;

	private OnTabSelectedListener mTabSelectedListener;

	public TabPageIndicator(Context context)
	{
		this(context, null);
	}

	public TabPageIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		mTabLayout = new IcsLinearLayout(context,R.attr.vpiTabPageIndicatorStyle);
		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,MATCH_PARENT));
	}

	/**
	 * 设置tab点击事件处理
	 * @param listener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener listener)
	{
		mTabSelectedListener = listener;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);

		final int childCount = mTabLayout.getChildCount();
		if (childCount > 1
				&& (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST))
		{
			if (childCount > 2)
			{
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
			} else
			{
				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
			}
		} else
		{
			mMaxTabWidth = -1;
		}

		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();

		if (lockedExpanded && oldWidth != newWidth)
		{
			setCurrentItem(mSelectedTabIndex);
		}
	}

	/**
	 * 自动移动到指定的Tab(让不在屏幕显示的tab显示)
	 * 
	 * @param position
	 */
	private void animateToTab(final int position)
	{
		final View tabView = mTabLayout.getChildAt(position);
		if (mTabSelector != null)
		{
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable()
		{
			public void run()
			{
				final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		if (mTabSelector != null)
		{
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		if (mTabSelector != null)
		{
			removeCallbacks(mTabSelector);
		}
	}

	/**
	 * 添加一个tab
	 * @param index
	 *            放置的索引位置
	 * @param text
	 *            tab文字
	 * @param iconResId
	 *            tab图标(如果存在)
	 */
	private void addTab(int index, CharSequence text, int iconResId)
	{
		final TabView tabView = new TabView(getContext());
		tabView.mIndex = index;
		tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
		tabView.setText(text);
		tabView.setTextColor(getResources().getColor(android.R.color.black));
		
		if (iconResId != 0)
		{
			tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
		}

		mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0,MATCH_PARENT, 1));
	}

	/**
	 /**
	 * 添加一个tab
	 * @param index
	 *            放置的索引位置
	 * @param text
	 *            tab文字
	 * @param iconBitmap
	 *            tab图标(如果存在)
	 * @param orentation
	 *            图标方向(1:左,2:上,3:右,4:下)
	 */
	private void addTab(int index, CharSequence text, Bitmap iconBitmap,int orentation)
	{
		final TabView tabView = new TabView(getContext());
		tabView.mIndex = index;
		tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
		tabView.setText(text);
		tabView.setTextColor(getResources().getColor(android.R.color.black));
		
		if (iconBitmap != null)
		{
			Drawable draw = new BitmapDrawable(getResources(), iconBitmap);
			switch(orentation)
			{
			case 0:
			case 1:
				tabView.setCompoundDrawablesWithIntrinsicBounds(draw,null	,null,null);
				break;
			case 2:
				tabView.setCompoundDrawablesWithIntrinsicBounds(null,draw	,null,null);
				break;
			case 3:
				tabView.setCompoundDrawablesWithIntrinsicBounds(null,null	,draw,null);
				break;
			case 4:
				tabView.setCompoundDrawablesWithIntrinsicBounds(null,null	,null,draw);
				break;
			}
		}

		mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0,MATCH_PARENT, 1));
	}
	
    /**
     * 设置tab组
     * @param tabs
     */
	public void setTabs(List<String> tabs)
	{
		mTabLayout.removeAllViews();
		final int count = tabs.size();
		for (int i = 0; i < count; i++)
		{
			CharSequence title = tabs.get(i);
			if (title == null)
			{
				title = EMPTY_TITLE;
			}
			int iconResId = 0;
			addTab(i, title, iconResId);
		}
		if (mSelectedTabIndex > count)
		{
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}

	 /**
     * 设置tab组
     * @param tabs
     */
	public void setTabs(String[] tabs)
	{
		mTabLayout.removeAllViews();
		final int count = tabs.length;
		for (int i = 0; i < count; i++)
		{
			CharSequence title = tabs[i];
			if (title == null)
			{
				title = EMPTY_TITLE;
			}
			int iconResId = 0;
			addTab(i, title, iconResId);
		}
		if (mSelectedTabIndex > count)
		{
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}
	
	/**
     * 设置带图标的tab组(key:tab标题,Integer:资源ID)
     * @param tabs
     */
	public void setTabs(Map<String,Integer> tabs)
	{
		if(tabs != null && tabs.size() > 0)
		{
			mTabLayout.removeAllViews();
			
			int i = 0;
			for (Map.Entry<String, Integer> entry : tabs.entrySet())
			{
				CharSequence title = entry.getKey();
				if (title == null)
				{
					title = EMPTY_TITLE;
				}
				addTab(i, title, entry.getValue());
				++i;
			}
			mSelectedTabIndex = i;
			setCurrentItem(mSelectedTabIndex);
			requestLayout();
		}
	}
	
	/**
     * 设置带图标的tab组(key:tab标题,Integer:资源ID)
     * @param tabs
     * @param orentation
	 *            图标方向(1:左,2:上,3:右,4:下)
     */
	public void setTabs(Map<String,Bitmap> tabs,int orentation)
	{
		if(tabs != null && tabs.size() > 0)
		{
			mTabLayout.removeAllViews();
			
			int i = 0;
			for (Map.Entry<String, Bitmap> entry : tabs.entrySet())
			{
				CharSequence title = entry.getKey();
				if (title == null)
				{
					title = EMPTY_TITLE;
				}
				addTab(i, title, entry.getValue(),orentation);
				++i;
			}
			mSelectedTabIndex = i;
			setCurrentItem(mSelectedTabIndex);
			requestLayout();
		}
	}
	
	/**
	 * 设置当前显示的Tab
	 * @param item
	 */
	public void setCurrentItem(int item)
	{
		mSelectedTabIndex = item;

		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++)
		{
			final View child = mTabLayout.getChildAt(i);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected)
			{
				animateToTab(item);
			}
		}
	}

	/**
	 * 对应导航条上面的tab
	 */
	public class TabView extends TextView
	{
		public int mIndex;

		public TabView(Context context)
		{
			super(context, null, R.attr.vpiTabPageIndicatorStyle);
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// Re-measure if we went beyond our maximum size.
			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth)
			{
				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth,
						MeasureSpec.EXACTLY), heightMeasureSpec);
			}
		}

		public int getIndex()
		{
			return mIndex;
		}
	}
}
