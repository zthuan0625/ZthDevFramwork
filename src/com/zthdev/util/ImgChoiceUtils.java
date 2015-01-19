package com.zthdev.util;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import com.zthdev.activity.imgchoice.ImgCameraActivity;
import com.zthdev.activity.imgchoice.ImgChoiceActivity;
import com.zthdev.framework.R;

/**
 * 
 * 类名称：ImgChoiceUtils <br>
 * 类描述：选择照片工具类 <br>
 * 创建人：赵腾欢 创建时间：2015-1-7 下午2:26:39 <br>
 * 
 * @version V1.0
 */
public class ImgChoiceUtils
{
	private int imgMaxSize = 9;

	/**
	 * 图片选中后的事件调用
	 */
	public ImgChoiceListener mListener;

	private ImgChoiceUtils()
	{
	}

	private static ImgChoiceUtils instance = null;

	public static ImgChoiceUtils getInstance()
	{
		if (instance == null)
		{
			instance = new ImgChoiceUtils();
		}
		return instance;
	}

	/**
	 * 设置允许选择的图片数量
	 * 
	 * @param count
	 * @return
	 */
	public ImgChoiceUtils setImgChoiceCount(int count)
	{
		this.imgMaxSize = count;
		return this;
	}

	/**
	 * 设置图片选择监听器
	 * 
	 * @param listener
	 * @return
	 */
	public ImgChoiceUtils setImgChoiceListener(ImgChoiceListener listener)
	{
		this.mListener = listener;
		return this;
	}

	/**
	 * 关闭
	 */
	public void close()
	{
		instance = null;
	}
	
	/**
	 * 图片选择
	 * 
	 * @param mContext
	 * @param listener
	 */
	public void doChoiceImg(Context mContext,View parent)
	{
		PopupWindows popWindows = new PopupWindows(mContext);
		popWindows.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
	}

	public interface ImgChoiceListener
	{
		public void imgChoiced(List<String> imgs);
		public void cancel();
	}

	public class PopupWindows extends PopupWindow
	{

		private Context mContext;
		
		public PopupWindows(Context mContext)
		{
			this.mContext = mContext;
			View view = View.inflate(mContext, R.layout.img_choice_type_pop, null);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			setAnimationStyle(R.style.PopupAnimation);
			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Intent intent = new Intent(PopupWindows.this.mContext, ImgCameraActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PopupWindows.this.mContext.startActivity(intent);
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Intent intent = new Intent(PopupWindows.this.mContext, ImgChoiceActivity.class);
					intent.putExtra("imgMaxSize", imgMaxSize);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PopupWindows.this.mContext.startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					dismiss();
					mListener.cancel();
				}
			});

		}
	}
}
