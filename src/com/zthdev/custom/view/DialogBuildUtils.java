package com.zthdev.custom.view;

import com.zthdev.framework.R;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * 类名称：DialogBuildUtils <br>
 * 类描述：自定义对话框构建类 <br>
 * 创建人：赵腾欢 创建时间：2014-11-8 下午2:33:37 <br>
 * 
 * @version V1.0
 */
public class DialogBuildUtils
{

	private static DialogBuildUtils utils;
	
	private DialogBuildUtils()
	{
	}
	
	public static DialogBuildUtils with()
	{
		if(utils == null)
		{
			utils = new DialogBuildUtils();
		}
		
		return utils;
	}
	
	/**
	 * 返回进度框构造器
	 * @param context
	 * @return
	 */
	public ProgressDialogBuilder createProgressDialog(Context context)
	{
		return new ProgressDialogBuilder(context);
	}

	/**
	 * 返回提示框构造器
	 * @param context
	 * @return
	 */
	public HintDialogBuilder createHintDialog(Context context)
	{
		return new HintDialogBuilder(context);
	}
	
	/**
	 * 进度框构造器
	 */
	public class ProgressDialogBuilder
	{
		private Dialog mTargetDialog;

		// 对话框提示信息
		private TextView dialog_message;

		public ProgressDialogBuilder(Context context)
		{
			this.mTargetDialog = new Dialog(context, R.style.dialog);
			this.mTargetDialog.setCanceledOnTouchOutside(false);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
			dialog_message = (TextView) view.findViewById(R.id.dialog_message);
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			// 宽度为屏幕的1/3
			int size = (int) (dm.widthPixels / 2.5);
			LayoutParams params = new LayoutParams(size, size);
			this.mTargetDialog.setContentView(view, params);
			// 设置背景不暗淡
			WindowManager.LayoutParams lp = this.mTargetDialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f;
		}

		/**
		 * 设置对话框内容
		 * 
		 * @param message
		 */
		public ProgressDialogBuilder setMessage(String message)
		{
			dialog_message.setText(message);
			return this;
		}

		/**
		 * 返回对话框
		 * 
		 * @return
		 */
		public Dialog create()
		{
			return mTargetDialog;
		}
	}

	/**
	 * 提示框构造器
	 */
	public class HintDialogBuilder
	{
		private Dialog mTargetDialog;

		// 对话框标题
		private TextView dialog_title;
		// 对话框提示信息
		private TextView dialog_message;

		// 左边按钮
		private Button dialog_yes_btn;
		// 中间按钮
		private Button dialog_center_btn;
		// 右边按钮
		private Button dialog_no_btn;
		// 内容容器
		private LinearLayout dialog_contain;

		public HintDialogBuilder(Context context)
		{
			this.mTargetDialog = new Dialog(context, R.style.dialog);
			this.mTargetDialog.setCanceledOnTouchOutside(false);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
			dialog_title = (TextView) view.findViewById(R.id.dialog_title);
			dialog_message = (TextView) view.findViewById(R.id.dialog_message);
			dialog_yes_btn = (Button) view.findViewById(R.id.dialog_yes_btn);
			dialog_center_btn = (Button) view
					.findViewById(R.id.dialog_center_btn);
			dialog_no_btn = (Button) view.findViewById(R.id.dialog_no_btn);
			dialog_contain = (LinearLayout) view
					.findViewById(R.id.dialog_contain);
			this.mTargetDialog.setContentView(view);
			// 设置背景不暗淡
			WindowManager.LayoutParams lp = this.mTargetDialog.getWindow()
					.getAttributes();
			lp.dimAmount = 0.7f;
		}

		/**
		 * 设置对话框标题
		 */
		public HintDialogBuilder setTitle(String title)
		{
			dialog_title.setText(title);
			return this;
		}

		/**
		 * 设置对话框内容
		 * 
		 * @param message
		 */
		public HintDialogBuilder setMessage(String message)
		{
			dialog_message.setText(message);
			return this;
		}

		/**
		 * 设置对话框显示内容
		 * 
		 * @param view
		 */
		public HintDialogBuilder setContentView(View view)
		{
			if (view == null)
				return this;
			dialog_contain.removeAllViews();
			dialog_contain.addView(view);
			return this;
		}

		/**
		 * 设置左边按钮
		 * 
		 * @param title
		 * @param clickListener
		 */
		public HintDialogBuilder setLeftButton(String title,
				final OnClickListener clickListener)
		{
			dialog_yes_btn.setText(title);
			dialog_yes_btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (clickListener != null)
					{
						clickListener.onClick(v);
					}
					mTargetDialog.dismiss();
				}
			});
			dialog_yes_btn.setVisibility(View.VISIBLE);
			return this;
		}

		/**
		 * 设置中间按钮
		 * 
		 * @param title
		 * @param clickListener
		 */
		public HintDialogBuilder setCenterButton(String title,
				final OnClickListener clickListener)
		{
			dialog_center_btn.setText(title);
			dialog_center_btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (clickListener != null)
					{
						clickListener.onClick(v);
					}
					mTargetDialog.dismiss();
				}
			});
			dialog_center_btn.setVisibility(View.VISIBLE);
			return this;
		}

		/**
		 * 设置右边按钮
		 * 
		 * @param title
		 * @param clickListener
		 */
		public HintDialogBuilder setRigthButton(String title,
				final OnClickListener clickListener)
		{
			dialog_no_btn.setText(title);
			dialog_no_btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (clickListener != null)
					{
						clickListener.onClick(v);
					}
					mTargetDialog.dismiss();
				}
			});
			dialog_no_btn.setVisibility(View.VISIBLE);
			return this;
		}

		/**
		 * 返回对话框
		 * 
		 * @return
		 */
		public Dialog create()
		{
			return mTargetDialog;
		}
	}
}
