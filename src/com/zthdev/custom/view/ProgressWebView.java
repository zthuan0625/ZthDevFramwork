package com.zthdev.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * 
 * 类名称：ProgressWebView <br>  
 * 类描述：带进度条的WebView <br>
 * 创建人：赵腾欢   
 * 创建时间：2014-8-21 上午9:19:15 <br>  
 * @version V1.0
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView
{

	private ProgressBar progressbar;

	public ProgressWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		progressbar = new ProgressBar(context, null,android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,5, 0, 0));
		addView(progressbar);
		setWebChromeClient(new WebChromeClient());
	}

	public class WebChromeClient extends android.webkit.WebChromeClient
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			if (newProgress == 100)
			{
				progressbar.setVisibility(GONE);
			} else
			{
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}
}
