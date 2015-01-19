package com.zthdev.activity.imgchoice;

import java.util.ArrayList;
import java.util.List;
import com.zthdev.activity.base.ZDevActivity;
import com.zthdev.adapter.ImageGridAdapter;
import com.zthdev.adapter.ImageGridAdapter.ViewHolder;
import com.zthdev.bean.ImageFolder;
import com.zthdev.bean.ImageItem;
import com.zthdev.custom.view.NewDataToast;
import com.zthdev.framework.R;
import com.zthdev.util.ImgChoiceUtils;
import com.zthdev.util.ImgStoreUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 
 * 类名称：ImgGridChoiceActivity <br>
 * 类描述：图片选择网格视图 <br>
 * 创建人：赵腾欢 创建时间：2015-1-7 上午10:03:48 <br>
 * 
 * @version V1.0
 */
public class ImgGridChoiceActivity extends ZDevActivity
{
	public ImageFolder imgFolder;

	public GridView img_choise_grid;

	public Button choise_ok_btn;

	/**
	 * 返回按钮
	 */
	private Button btn_back;

	public ImageGridAdapter adapter;
	
	public ImgStoreUtils helper;

	/**
	 * 最多能选择多少张图片
	 */
	private int imgMaxSize;
	
	public List<String> selectList = new ArrayList<String>();
	public int selectTotal = 0;
	
	@Override
	public int initLayoutView()
	{
		return R.layout.activity_image_grid;
	}

	@Override
	public void initData()
	{
		imgMaxSize = getIntent().getExtras().getInt("imgMaxSize");
		
		choise_ok_btn = (Button) findViewById(R.id.choise_ok_btn);
		img_choise_grid = (GridView) findViewById(R.id.img_choise_grid);
		btn_back = (Button) findViewById(R.id.btn_back);
		
		helper = ImgStoreUtils.with(getApplicationContext());
		imgFolder = (ImageFolder) getIntent().getSerializableExtra("imglist");
		img_choise_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImgGridChoiceActivity.this, imgFolder.imageList);
		img_choise_grid.setAdapter(adapter);
	}

	@Override
	public void initViewListener()
	{
		img_choise_grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id)
			{
				final ImageItem item = imgFolder.imageList.get(position);
				String path = item.imagePath;
				item.isSelected = !item.isSelected;
				ViewHolder holder = (ViewHolder) view.getTag();
				if (item.isSelected)
				{
					if(imgMaxSize == selectTotal)
					{
						NewDataToast.makeErrorText(ImgGridChoiceActivity.this, "最多选择"+imgMaxSize+"张图片").show();
						item.isSelected = false;
						return;
					}
					++selectTotal;
					choise_ok_btn.setText("完成" + "(" + selectTotal + ")");
					selectList.add(path);
					holder.selected.setImageResource(R.drawable.icon_data_select);
					holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
				} else
				{
					--selectTotal;
					choise_ok_btn.setText("完成" + "(" + selectTotal + ")");
					selectList.remove(path);
					holder.selected.setImageResource(-1);
					holder.text.setBackgroundColor(0x00000000);
				}
			}
		});

		choise_ok_btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(selectTotal > 0)
				{
					if(ImgChoiceUtils.getInstance().mListener != null)
					{
						ImgChoiceUtils.getInstance().mListener.imgChoiced(selectList);
						ImgChoiceUtils.getInstance().close();
						setResult(202);
						finish();
					}
				}
				else
				{
					NewDataToast.makeErrorText(ImgGridChoiceActivity.this, "您没有实现图片选择接口").show();
				}
			}
		});

		btn_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(404);
				finish();
			}
		});
	}
}
