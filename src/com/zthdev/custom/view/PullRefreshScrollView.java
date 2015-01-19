package com.zthdev.custom.view;

import com.zthdev.framework.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * 项目名称：TestPullRefresh<br>
 * 类名称：PullRefreshView <br>
 * 类描述：自定义下拉刷新 <br>
 * 创建人：赵腾欢 创建时间：2014-6-1 下午10:02:10 <br>
 * 
 * @version V1.0
 */
public class PullRefreshScrollView extends ScrollView
{

	/**
	 * 下拉头往上回滚的速度
	 */
	private static final int ROLLBACK_SPEED = -20;

	/**
	 * 标识当前正在下拉状态
	 */
	private static final int STATUS_PULL_TO_REFRESH = 0;

	/**
	 * 标识当前已经达到可以刷新的高度,但是手指还未抬起
	 */
	private static final int STATUS_RELEASE_TO_REFRESH = 1;

	/**
	 * 标识当前正在刷新
	 */
	private static final int STATUS_REFRESHING = 2;

	/**
	 * 标识当前已经刷新完毕或者手指未有动作(默认状态)
	 */
	private static final int STATUS_REFRESH_FINISHED = 3;

	/**
	 * 标识当前的状态
	 */
	private int currentStatus = STATUS_REFRESH_FINISHED;

	/**
	 * 标识最后一次的状态
	 */
	private int lastStatus = currentStatus;

	/**
	 * 下拉刷新里面的内容布局
	 */
	private LinearLayout contentLayout;

	/**
	 * 下拉刷新里面下拉才会出现的页眉视图
	 */
	private View headView;

	/**
	 * 页眉的布局
	 */
	private MarginLayoutParams headMarginLayoutParams;

	/**
	 * 刷新时显示的进度条
	 */
	private ProgressBar progressBar;

	/**
	 * 指示下拉和释放的箭头
	 */
	private ImageView arrow;

	/**
	 * 指示下拉和释放的文字描述
	 */
	private TextView description;

	/**
	 * 初始化工作只需运行一次(onLayout方法可能会运行多次)
	 */
	private boolean loadOnce = false;

	/**
	 * 手指触摸屏幕时的Y坐标点
	 */
	private float yDownPosition = -11000000;

	/**
	 * 下拉头偏移出屏幕的高度
	 */
	private int hideHeaderHeight;

	/**
	 * 下拉事件需要手指最少滑动的距离
	 */
	private int pullRefreshNeddHeight;

	/**
	 * 下拉刷新事件监听器
	 */
	private OnPullRefreshListener onPullRefreshListener;

	public PullRefreshScrollView(Context context)
	{
		super(context);
	}

	public PullRefreshScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// 响应滑动事件的最小距离
		pullRefreshNeddHeight = ViewConfiguration.get(context)
				.getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce)
		{
			// 获取包含的ListView
			contentLayout = (LinearLayout) getChildAt(0);
			headView = contentLayout.getChildAt(0);
			// 页眉中的进度框
			progressBar = (ProgressBar) headView
					.findViewById(R.id.progress_bar);
			// 页眉中的箭头
			arrow = (ImageView) headView.findViewById(R.id.arrow);
			// 页眉中的提示文字
			description = (TextView) headView.findViewById(R.id.description);

			// 获得下拉头的高度(取负值,之后偏移出屏幕需要用到)
			hideHeaderHeight = -headView.getHeight();
			System.out.println("the headerHeight:" + hideHeaderHeight);

			// 获取下拉头的布局参数并转换成margin布局参数
			headMarginLayoutParams = (MarginLayoutParams) headView
					.getLayoutParams();
			// 设置上边的偏移值为下拉头的高度的负数,这样就可以偏移出屏幕
			headMarginLayoutParams.topMargin = hideHeaderHeight;
			headView.setLayoutParams(headMarginLayoutParams);
			// 标识当前已经初始化完毕,下次重绘时不需要重新初始化
			loadOnce = true;
			headView.setVisibility(View.GONE);
		}
	}

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance)
			{
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		if (currentStatus == STATUS_REFRESHING)
		{
			return false;
		}
		switch (event.getAction())
		{
		// 手指刚按下记录按下点的位置
		case MotionEvent.ACTION_DOWN:
			yDownPosition = event.getRawY();
			super.onTouchEvent(event);
			break;

		// 手指滑动时
		case MotionEvent.ACTION_MOVE:

			if (yDownPosition == -11000000)
			{
				yDownPosition = event.getRawY();
				return super.onTouchEvent(event);
			}
			int distance = (int) (event.getRawY() - yDownPosition);

			if (distance < 0)
			{
				return super.onTouchEvent(event);
			}
			if (Math.abs(distance) < pullRefreshNeddHeight)
			{
				return super.onTouchEvent(event);
			}
			// 当滚动到最上或者最下时就不会再滚动，这时移动布局
			if (isNeedMove() && currentStatus != STATUS_REFRESHING)
			{
				if (!headView.isShown())
				{
					headView.setVisibility(View.VISIBLE);
				}
				if (headMarginLayoutParams.topMargin > 0)
				{
					currentStatus = STATUS_RELEASE_TO_REFRESH;
				} else
				{
					currentStatus = STATUS_PULL_TO_REFRESH;
				}
				// 通过偏移下拉头的topMargin值，来实现下拉效果
				headMarginLayoutParams.topMargin = (distance / 2)
						+ hideHeaderHeight;
				headView.setLayoutParams(headMarginLayoutParams);
			} else
			{
				super.onTouchEvent(event);
			}
			break;

		case MotionEvent.ACTION_UP:
			yDownPosition = -11000000;
			if (currentStatus == STATUS_PULL_TO_REFRESH)
			{
				contentLayout.setEnabled(false);
				new HideHeaderTask().execute();
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH)
			{
				contentLayout.setEnabled(false);
				new RefreshingTask().execute();
			}
			super.onTouchEvent(event);
			break;
		}

		// 时刻记得更新下拉头中的信息
		if (currentStatus == STATUS_PULL_TO_REFRESH
				|| currentStatus == STATUS_RELEASE_TO_REFRESH)
		{
			updateHeaderView();
			lastStatus = currentStatus;
		}
		return super.onTouchEvent(event);
	}

	// 是否需要移动布局
	public boolean isNeedMove()
	{
		int scrollY = getScrollY();
		if (scrollY == 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * 根据不同状态刷新下拉头里面的信息
	 */
	private void updateHeaderView()
	{
		if (lastStatus != currentStatus)
		{
			if (currentStatus == STATUS_PULL_TO_REFRESH)
			{
				description.setText(getResources().getString(
						R.string.pull_to_refresh_pull_label));
				arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH)
			{
				description.setText(getResources().getString(
						R.string.pull_to_refresh_release_label));
				arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_REFRESHING)
			{
				description.setText(getResources().getString(
						R.string.pull_to_refresh_refreshing_label));
				progressBar.setVisibility(View.VISIBLE);
				arrow.clearAnimation();
				arrow.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 根据当前的状态来旋转箭头。
	 */
	private void rotateArrow()
	{
		float pivotX = arrow.getWidth() / 2f;
		float pivotY = arrow.getHeight() / 2f;
		float fromDegrees = 0f;
		float toDegrees = 0f;
		if (currentStatus == STATUS_PULL_TO_REFRESH)
		{
			fromDegrees = 180f;
			toDegrees = 360f;
		} else if (currentStatus == STATUS_RELEASE_TO_REFRESH)
		{
			fromDegrees = 0f;
			toDegrees = 180f;
		}
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
				pivotX, pivotY);
		animation.setDuration(100);
		animation.setFillAfter(true);
		arrow.startAnimation(animation);
	}

	/**
	 * 指定当前线程休眠time毫秒
	 * 
	 * @param time
	 */
	private void sleep(int time)
	{
		try
		{
			Thread.sleep(time);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 设置下拉刷新事件监听器
	 * 
	 * @param onPullRefreshListener
	 */
	public void setOnPullRefreshListener(
			OnPullRefreshListener onPullRefreshListener)
	{
		this.onPullRefreshListener = onPullRefreshListener;
	}

	/**
	 * 结束下拉刷新(下拉头偏移屏幕之外隐藏) 这个方法给外部调用(多线程网络通信时需要)
	 */
	public void finishRefresh()
	{
		currentStatus = STATUS_REFRESH_FINISHED;
		new HideHeaderTask().execute();
	}

	/**
	 * 刷新的线程(刷新的过程是先把超过下拉头显示区域的部分回滚,然后刷新,然后再隐藏下拉头)
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			int topMargin = headMarginLayoutParams.topMargin;
			while (true)
			{
				topMargin = topMargin + ROLLBACK_SPEED;
				if (topMargin <= 0)
				{
					topMargin = 0;
					break;
				}
				publishProgress(topMargin);
				sleep(10);
			}
			currentStatus = STATUS_REFRESHING;
			publishProgress(0);
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			updateHeaderView();
			headMarginLayoutParams.topMargin = values[0];
			headView.setLayoutParams(headMarginLayoutParams);
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (onPullRefreshListener != null)
			{
				onPullRefreshListener.onRefresh();
			}
		}
	}

	/**
	 * 隐藏下拉头的线程类
	 */
	class HideHeaderTask extends AsyncTask<Void, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(Void... params)
		{
			int topMargin = headMarginLayoutParams.topMargin;

			// 死循环回滚下拉头,线程休眠10毫秒
			while (true)
			{
				topMargin = topMargin + ROLLBACK_SPEED;

				if (topMargin <= hideHeaderHeight)
				{
					topMargin = hideHeaderHeight;
					break;
				}

				publishProgress(topMargin);
				sleep(10);
			}

			return topMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			// 每次减少一些偏移就在UI线程中更新
			headMarginLayoutParams.topMargin = values[0];
			headView.setLayoutParams(headMarginLayoutParams);
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			headMarginLayoutParams.topMargin = result;
			headView.setLayoutParams(headMarginLayoutParams);
			currentStatus = STATUS_REFRESH_FINISHED;
			contentLayout.setEnabled(true);
		}
	}

	/**
	 * 下拉刷新的事件监听
	 */
	public interface OnPullRefreshListener
	{
		public void onRefresh();
	}
}
