package com.zthdev.activity.base;
import java.lang.reflect.Field;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.zthdev.annotation.BindID;
import com.zthdev.app.ZDevAppContext;
import com.zthdev.custom.view.ScrollTabHost;

/**
 * 
 * 类名称：ZDevTabActivity <br>  
 * 类描述：带TabHost的Activity,支持滑动切换 <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-10-20 下午3:00:17 <br>  
 * @version V1.0
 */
public abstract class ZDevTabActivity extends Activity implements
		OnGestureListener
{

	/**
	 * TabHost
	 */
	private ScrollTabHost mTabHost;

	/**
	 * 手势解析
	 */
	private GestureDetector mGestureDetector;
	
	/**
	 * 在该方法里面返回Activity的布局文件
	 */
	public abstract int initLayoutView();
	
	/**
	 * 初始化TabHost的数据
	 * @param mTabHost
	 */
	public abstract void intTabHostData(ScrollTabHost mTabHost);
	
	/**
	 * 初始化数据
	 */
	public abstract void initData();

	/**
	 * 在该方法里面定义需要自定义的监听事件
	 */
	public abstract void initViewListener();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(initLayoutView());
		ZDevAppContext.getInstance().pushActivity(this);
		initTab();
		intTabHostData(mTabHost);
		initTabTouch();
		injectAllView();
		initData();
		initViewListener();
	}

	/**
	 * 初始化TabHost
	 */
	private void initTab()
	{
		mTabHost = (ScrollTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	
	/**
	 * 初始化TabHost滑动事件
	 */
	private void initTabTouch()
	{
		DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mTabHost.initSwitchAnimation(metrics.widthPixels);
		mGestureDetector = new GestureDetector(this,this);
		mTabHost.setOnTouchListener(mOnTouchListener);
	}
	
	/**
	 * 反射获得所有的控件并判断是否使用了bindID注解 如果使用则自动匹配相应ID
	 */
	protected void injectAllView()
	{
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			field.setAccessible(true);
			// 判断该字段是否存在指定的注解
			if (field.isAnnotationPresent(BindID.class))
			{
				BindID inject = field.getAnnotation(BindID.class);
				int resourid = inject.id();
				try
				{
					field.set(this, findViewById(resourid));
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			return mGestureDetector.onTouchEvent(event);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)
	{
		//判断X方向的滑动速度
		int vel = velocityX > 10 ? -1 : 1;
		mTabHost.setCurrentTab(mTabHost.getCurrentTab() + vel);

		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ZDevAppContext.getInstance().popActivity(this);
	}
}
