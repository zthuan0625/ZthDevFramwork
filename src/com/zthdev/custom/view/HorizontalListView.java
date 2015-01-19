package com.zthdev.custom.view;

import java.util.ArrayList;
import java.util.LinkedList;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * 
 * 类名称：HorizontalListView <br>
 * 类描述：水平ListView控件 <br>
 * 创建人：赵腾欢 创建时间：2014-6-25 上午9:08:40 <br>
 * 修改人： 修改时间： <br>
 * 修改备注：
 * 
 * @version V1.0
 */
public class HorizontalListView extends AdapterView<ListAdapter>
{
	/**
	 * 列表适配器
	 */
	private ListAdapter mAdapter;

	/**
	 * 列表视图集合
	 */
	private LinkedList<View> mContentViews;

	/**
	 * 当前选中的View
	 */
	private int mCurrentShowViewIndex = -1;

	/**
	 * 列表中View的数量
	 */
	private int mMaxViewsSize = -1;

	/**
	 * 滑动速度捕获
	 */
	private VelocityTracker mVelocityTracker;

	/**
	 * 判断手势为滑动的最小距离
	 */
	private int mTouchSlop;

	/**
	 * 判断View随惯性滑动的最大速度
	 */
	private int mMaximumVelocity;

	/**
	 * 负责滑动View
	 */
	private Scroller mScroller;

	public HorizontalListView(Context context)
	{
		super(context);
		mContentViews = new LinkedList<View>();
		mCurrentShowViewIndex = 0;
		init();
	}

	public HorizontalListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContentViews = new LinkedList<View>();
		mCurrentShowViewIndex = 0;
		init();
	}

	private void init()
	{
		ViewConfiguration cof = ViewConfiguration.get(getContext());
		mTouchSlop = cof.getScaledTouchSlop();
		mMaximumVelocity = cof.getScaledMaximumFlingVelocity();
		mScroller = new Scroller(getContext());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		/**
		 * 该控件必须有明确的大小指定
		 */
		if (widthMode != MeasureSpec.EXACTLY && !isInEditMode())
		{
			throw new IllegalStateException(
					"HorizontalListView can only be used in EXACTLY mode.");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY && !isInEditMode())
		{
			throw new IllegalStateException(
					"HorizontalListView can only be used in EXACTLY mode.");
		}

		/**
		 * 确定子控件的大小
		 */
		int childCount = getChildCount();
		for (int index = 0; index < childCount; ++index)
		{
			getChildAt(index).measure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);

		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE)
			{
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	private float lastX;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		// 如果没有子控件那么就不处理该次触摸事件
		if (getChildCount() == 0)
		{
			return false;
		}

		if (mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(ev);

		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			lastX = ev.getX();
			// 如果此前有滑动事件未完成则终止滑动事件
			if (!mScroller.isFinished())
			{
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int distanceX = (int) Math.abs(ev.getX() - lastX);
			// 判断手指滑动的距离是否达到了判定为滑动的距离
			if (distanceX > mTouchSlop)
			{
				if (ev.getX() - lastX > 0)
				{
					scrollBy((int) (ev.getX() - lastX), 0);
				} else
				{
					scrollBy((int) (ev.getX() - lastX), 0);
				}

				lastX = ev.getX();
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// 如果没有子控件那么就不处理该次触摸事件
		if (getChildCount() == 0)
		{
			return false;
		}

		if (mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(event);
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			lastX = event.getRawX();
			// 如果此前有滑动事件未完成则终止滑动事件
			if (!mScroller.isFinished())
			{
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int distanceX = (int) Math.abs(event.getRawX() - lastX);
			// 判断手指滑动的距离是否达到了判定为滑动的距离
			if (distanceX > mTouchSlop)
			{
				if (event.getX() - lastX > 0)
				{
					scrollBy(-(int) (event.getRawX() - lastX), 0);
				} else
				{
					scrollBy(-(int) (event.getRawX() - lastX), 0);
				}

				lastX = event.getRawX();
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:

			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int xVelocity = (int) mVelocityTracker.getXVelocity();
			if (xVelocity > 1000 && mCurrentShowViewIndex > 0)
			{
				--mCurrentShowViewIndex;
				final int newX = mCurrentShowViewIndex * getWidth();
				final int delta = newX - getScrollX();
				mScroller.startScroll(getScrollX(), 0, delta, 0,
						Math.abs(delta) * 2);
				invalidate();
				if (mVelocityTracker != null)
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				System.out.println("tttt889");
				return true;
			}
			if (xVelocity < -1000 && mCurrentShowViewIndex < mMaxViewsSize - 1)
			{
				++mCurrentShowViewIndex;
				final int newX = mCurrentShowViewIndex * getWidth();
				final int delta = newX - getScrollX();
				mScroller.startScroll(getScrollX(), 0, delta, 0,Math.abs(delta) * 2);
				invalidate();
				if (mVelocityTracker != null)
				{
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				return true;
			}

			// 获取当前View的宽度
			final int screenWidth = getWidth();

			/**
			 * 判断当前应该显示的item的方法为:当前View的X坐标加上屏幕宽度的一半再除以屏幕宽度
			 * 一个屏幕宽度就是一个item。这样就可以算出当前应该显示的item的索引
			 */
			final int whichScreen = (getScrollX() + (screenWidth / 2))
					/ screenWidth;

			/**
			 * 因为该布局是按从X=0坐标开始,按水平向右延伸的方式扩展, 那么每个Item所在位置的X坐标就可以确认为
			 * Item索引乘以Item的宽度
			 * 如果需要滑动到I位置的item。那么就是该位置item的X坐标减去当前View的左上角X坐标,即为滑动距离
			 */
			final int newX = whichScreen * getWidth();
			final int delta = newX - getScrollX();

			/**
			 * 如果当前是向左滑动,那么这时如果取得item索引大于当前item的集合大小,那么这时候就应该回滚
			 */
			if (whichScreen >= mMaxViewsSize)
			{
				int backX = (mMaxViewsSize - 1) * getWidth();
				int backDelta = backX - getScrollX();
				mScroller.startScroll(getScrollX(), 0, backDelta, 0,
						Math.abs(delta) * 2);
				invalidate();
				return true;
			}

			/**
			 * 根据判断出来的位置滑动到指定位置
			 */
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public void computeScroll()
	{
		if (mScroller.computeScrollOffset())
		{
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public ListAdapter getAdapter()
	{
		return mAdapter;
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		this.mAdapter = adapter;
		this.mMaxViewsSize = adapter.getCount();
		this.mCurrentShowViewIndex = 0;
		setSelection(mCurrentShowViewIndex);
	}

	@Override
	public View getSelectedView()
	{
		if (this.mContentViews != null && this.mContentViews.size() > 0)
		{
			if (this.mCurrentShowViewIndex != -1)
			{
				return this.mContentViews.get(mCurrentShowViewIndex);
			}
		}
		return null;
	}

	@Override
	public void setSelection(int position)
	{
		ArrayList<View> recycleViews = new ArrayList<View>();
		View recycleView;
		while (!mContentViews.isEmpty())
		{
			recycleViews.add(recycleView = mContentViews.remove());
			detachViewFromParent(recycleView);
		}

		for (int i = 0; i < mAdapter.getCount(); i++)
		{
			View v = makeAndAddView(i, true, (recycleViews.isEmpty() ? null
					: recycleViews.remove(i)));
			mContentViews.addLast(v);
		}

		for (View view : recycleViews)
		{
			removeDetachedView(view, false);
		}

		requestLayout();
	}

	private View makeAndAddView(int position, boolean addToEnd, View convertView)
	{
		View view = mAdapter.getView(position, convertView, this);
		return setupChild(view, addToEnd, convertView != null);
	}

	/**
	 * 设置子View的布局参数
	 * 
	 * @param child
	 * @param addToEnd
	 * @param recycle
	 * @return
	 */
	private View setupChild(View child, boolean addToEnd, boolean recycle)
	{
		ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) child
				.getLayoutParams();

		if (p == null)
		{
			// 因为该自定义view的特性,这里子元素的宽度必须充满整个父元素
			p = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		}
		// 判断当前子元素是否只是detach剥离了父元素的树结构,如果是则只需重新依附上去
		if (recycle)
			attachViewToParent(child, (addToEnd ? -1 : 0), p);
		else
			addViewInLayout(child, (addToEnd ? -1 : 0), p, true);
		return child;
	}
}
