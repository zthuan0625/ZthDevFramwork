package com.zthdev.custom.view;

import com.zthdev.framework.R;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新数据Toast提示控件
 */
public class NewDataToast extends Toast
{

	public NewDataToast(Context context)
	{
		super(context);
	}

	@Override
	public void show()
	{
		super.show();
	}

	/**
	 * 通知
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @return
	 */
	public static NewDataToast makeText(Context context, CharSequence text)
	{
		NewDataToast result = new NewDataToast(context);
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		View v = inflate.inflate(R.layout.toast_message, null);
		v.setMinimumWidth(dm.widthPixels);// 设置控件最小宽度为手机屏幕宽度
		TextView tv = (TextView) v.findViewById(R.id.new_data_toast_message);
		tv.setText(text);
		result.setView(v);
		result.setDuration(LENGTH_SHORT);
		result.setGravity(Gravity.TOP, 0, 250);
		return result;
	}

	/**
	 * 异常通知
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @return
	 */
	public static NewDataToast makeErrorText(Context context, CharSequence text)
	{
		NewDataToast result = new NewDataToast(context);
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		View v = inflate.inflate(R.layout.toast_img_message, null);
		int size = (int) (dm.widthPixels / 2.5);
		v.setMinimumWidth(size);// 设置控件最小宽度为手机屏幕宽度
		v.setMinimumHeight(size);
		TextView tv = (TextView) v.findViewById(R.id.toast_message);
		tv.setText(text);
		ImageView iv = (ImageView) v.findViewById(R.id.toast_img);
		iv.setImageResource(R.drawable.toast_error);
		result.setView(v);
		result.setDuration(LENGTH_SHORT);
		result.setGravity(Gravity.CENTER, 0, 0);
		return result;
	}
	
	/**
	 * 成功通知
	 * 
	 * @param context
	 * @param text
	 *            提示消息
	 * @return
	 */
	public static NewDataToast makeSuccessText(Context context, CharSequence text)
	{
		NewDataToast result = new NewDataToast(context);
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		View v = inflate.inflate(R.layout.toast_img_message, null);
		// 宽度为屏幕的1/3
		int size = (int) (dm.widthPixels / 2.5);
		v.setMinimumWidth(size);// 设置控件最小宽度为手机屏幕宽度
		v.setMinimumHeight(size);
		ImageView iv = (ImageView) v.findViewById(R.id.toast_img);
		iv.setImageResource(R.drawable.toast_success);
		TextView tv = (TextView) v.findViewById(R.id.toast_message);
		tv.setText(text);
		result.setView(v);
		result.setDuration(LENGTH_SHORT);
		result.setGravity(Gravity.CENTER, 0, 0);
		return result;
	}
}
