package com.zthdev.custom.view;

import com.zthdev.framework.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * 类名称：ZthListView <br>
 * 类描述：实现了分批加载接口的ListView <br>
 * 创建人：赵腾欢 创建时间：2014-7-15 上午11:55:57 <br>
 * 
 * @version V1.0
 */
public class ZDevGridViewGroup extends ScrollView
{

	/**
	 * 分批加载接口
	 */
	private onLoadMoreListener mOnLoadMoreListener;

	/**
	 * 标识是否需要分批加载(所有数据加载完毕后就不需要加载)
	 */
	private boolean isFinishedLoad = false;

	/**
	 * 没有在加载
	 */
	public final int LOAD_STATE_IDLE = 0;

	/**
	 * 正在加载状态
	 */
	public final int LOAD_STATE_LOADING = 1;

	/**
	 * 标识加载状态
	 */
	public int LOAD_STATE = LOAD_STATE_IDLE;

	/**
	 * 标识分批加载的方式
	 */
	public enum BatchType
	{
		/**
		 * 滑动到最后一行自动加载
		 */
		AutoLoad,

		/**
		 * 点击页脚的加载按钮加载(手动加载)
		 */
		ManuallyLoad
	}

	/**
	 * 标识ListView的加载模式(默认为自动分批加载 滑动到最后一行自动加载更多)
	 */
	private BatchType batchType = BatchType.AutoLoad;

	/**
	 * 页脚视图
	 */
	public View footerView;

	/**
	 * 页脚加载更多
	 */
	private TextView footer_loadMoreButton;

	/**
	 * 页脚进度框
	 */
	private LinearLayout footer_progress;

	private ScrollGridView gridView;

	/**
	 * 初始化工作只需运行一次(onLayout方法可能会运行多次)
	 */
	private boolean loadOnce = false;

	public ZDevGridViewGroup(Context context)
	{
		super(context);
	}

	public ZDevGridViewGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ZDevGridViewGroup(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		if (changed && !loadOnce)
		{
			init(getContext());
			loadOnce = true;
		}
		super.onLayout(changed, l, t, r, b);
	}

	private void init(Context context)
	{
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		// 获取包含的GridView
		gridView = (ScrollGridView) getChildAt(0);
		this.removeAllViews();
		
		// 设置分批加载监听器
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.weight = 1;
		linearLayout.addView(gridView, params);
		// 设置ListView的页脚
		footerView = LayoutInflater.from(context).inflate(R.layout.listfooter,null);
		footer_loadMoreButton = (TextView) footerView.findViewById(R.id.footer_loadMoreButton);
		footer_progress = (LinearLayout) footerView.findViewById(R.id.footer_progress);
		android.widget.LinearLayout.LayoutParams lineaParams = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		linearLayout.addView(footerView, lineaParams);
		// footerView.setVisibility(View.GONE);
		this.addView(linearLayout);
		setBatchType(batchType);
	}

	/**
	 * 设置ListView的分批加载模式
	 * 
	 * @param type
	 */
	public void setBatchType(BatchType type)
	{
		this.batchType = type;
		
		if(footerView == null)
			return;

		// 判断是否为手动加载模式
		if (type == BatchType.ManuallyLoad)
		{
			footer_loadMoreButton.setText("点击加载更多.");
			footer_loadMoreButton.setVisibility(View.VISIBLE);
			footer_loadMoreButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mOnLoadMoreListener != null)
					{
						footer_loadMoreButton.setVisibility(View.GONE);
						footer_progress.setVisibility(View.VISIBLE);
						mOnLoadMoreListener
								.doLoadMoreData(ZDevGridViewGroup.this.gridView);
					}
				}
			});
			footer_progress.setVisibility(View.GONE);
		} else if (type == BatchType.AutoLoad)
		{
			footer_loadMoreButton.setVisibility(View.GONE);
			footer_progress.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置分批加载监听器
	 * 
	 * @param loadMoreListener
	 */
	public void setOnLoadMoreListener(onLoadMoreListener loadMoreListener)
	{
		this.mOnLoadMoreListener = loadMoreListener;
	}

	/**
	 * 继续监听分批加载刷新事件
	 * 
	 * @param state
	 */
	public void continueListener()
	{
		this.LOAD_STATE = LOAD_STATE_IDLE;
		// 如果当前为手动加载模式,则不需要去掉页脚.只需隐藏进度框然后显示文字提示
		if (batchType == BatchType.ManuallyLoad)
		{
			footer_loadMoreButton.setVisibility(View.VISIBLE);
			footer_progress.setVisibility(View.GONE);
		}
	}

	/**
	 * 关闭分批加载
	 */
	public void finishedLoad(String message)
	{
		this.isFinishedLoad = true;
		footer_loadMoreButton.setText(message);
		footer_loadMoreButton.setVisibility(View.VISIBLE);
		footer_loadMoreButton.setClickable(false);
		footer_progress.setVisibility(View.GONE);
	}

	/**
	 * 打开分批加载
	 */
	public void finishedOpen()
	{
		this.isFinishedLoad = false;
		footer_loadMoreButton.setVisibility(View.GONE);
		footer_loadMoreButton.setClickable(false);
		footer_progress.setVisibility(View.VISIBLE);
	}

	/**
	 * 分批加载结束
	 */
	public void loaded()
	{
		LOAD_STATE = LOAD_STATE_IDLE;
		if(batchType == BatchType.ManuallyLoad)
		{
			footer_loadMoreButton.setVisibility(View.VISIBLE);
			footer_progress.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,boolean clampedY)
	{
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		if (scrollY != 0 && null != mOnLoadMoreListener)
		{
			// 如果当前模式为手动加载则退出执行
			if (batchType == BatchType.ManuallyLoad)
			{
				return;
			}
			// 如果当前设置不需要加载则退出执行
			if (isFinishedLoad)
			{
				return;
			}
			// 如果当前正在加载则退出执行
			if (LOAD_STATE == LOAD_STATE_LOADING)
			{
				return;
			}
			LOAD_STATE = LOAD_STATE_LOADING;
			mOnLoadMoreListener.doLoadMoreData(ZDevGridViewGroup.this.gridView);
		}
	}

	/**
	 * 
	 * 类名称：onLoadMoreListener <br>
	 * 类描述：listView分批加载的监听器,如果要实现分批加载则实现该接口 <br>
	 * 创建人：赵腾欢 创建时间：2014-7-15 上午11:51:10 <br>
	 * 
	 * @version V1.0
	 */
	public interface onLoadMoreListener
	{
		public void doLoadMoreData(GridView gridView);
	}
}
