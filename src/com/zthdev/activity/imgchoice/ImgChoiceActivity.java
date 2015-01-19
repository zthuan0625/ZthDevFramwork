package com.zthdev.activity.imgchoice;

import java.util.List;
import com.zthdev.activity.base.ZDevActivity;
import com.zthdev.adapter.ImageBucketAdapter;
import com.zthdev.bean.ImageFolder;
import com.zthdev.framework.R;
import com.zthdev.util.ImgStoreUtils;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

/**
 * 图片选择activity
 */
public class ImgChoiceActivity extends ZDevActivity
{
	/**
	 * 返回按钮
	 */
	private Button btn_back;

	private GridView img_choise_grid;

	private ImageBucketAdapter adapter;// 自定义的适配器

	/**
	 * 系统图库工具类
	 */
	private ImgStoreUtils helper;

	private List<ImageFolder> dataList;

	/**
	 * 最多能选择多少张图片
	 */
	private int imgMaxSize;

	@Override
	public int initLayoutView()
	{
		return R.layout.activity_image_bucket;
	}

	@Override
	public void initData()
	{
		//选择图片的最大数量,默认为9张
		imgMaxSize = getIntent().getExtras().getInt("imgMaxSize");
		img_choise_grid = (GridView) findViewById(R.id.img_choise_grid);
		btn_back = (Button) findViewById(R.id.btn_back);
		helper = ImgStoreUtils.with(getApplicationContext());
		dataList = helper.getImagesBucketList(false);
		adapter = new ImageBucketAdapter(ImgChoiceActivity.this, dataList);
		img_choise_grid.setAdapter(adapter);
	}

	@Override
	public void initViewListener()
	{
		img_choise_grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent intent = new Intent(ImgChoiceActivity.this,ImgGridChoiceActivity.class);
				intent.putExtra("imglist",dataList.get(position));
				intent.putExtra("imgMaxSize", imgMaxSize);
				startActivityForResult(intent, 1);
			}
		});

		btn_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//判断是否已经选择图片，如果是则关闭页面
		if(resultCode == 202)
		{
			finish();
		}
	}
}
