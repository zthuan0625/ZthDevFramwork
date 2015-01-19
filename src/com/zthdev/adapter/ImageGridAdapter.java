package com.zthdev.adapter;

import java.io.File;
import java.util.List;
import com.zthdev.bean.ImageItem;
import com.zthdev.framework.R;
import com.zthdev.img.ZImgLoaders;
import com.zthdev.util.StringUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 类名称：ImageGridAdapter <br>  
 * 类描述：图片选择网格视图适配器 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-7 下午5:04:35 <br>  
 * @version V1.0
 */
public class ImageGridAdapter extends BaseAdapter
{

	public List<ImageItem> dataList;
	private Context context;
	
	public ImageGridAdapter(Context context, List<ImageItem> list)
	{
		this.context = context;
		this.dataList = list;
	}

	@Override
	public int getCount()
	{
		int count = 0;
		if (dataList != null)
		{
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
					
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		if(!StringUtils.isEmpty(item.thumbnailPath))
		{
			ZImgLoaders.with(context)
                       .prepare()
                       .load(new File(item.thumbnailPath))
                       .into(holder.iv)
                       .start();
		}
		else if(!StringUtils.isEmpty(item.imagePath))
		{
			ZImgLoaders.with(context)
	                   .prepare()
	                   .reSize(150, 150)
	                   .load(new File(item.imagePath))
	                   .into(holder.iv)
	                   .start();
		}
		
		if (item.isSelected)
		{
			holder.selected.setImageResource(R.drawable.icon_data_select);
			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else
		{
			holder.selected.setImageResource(-1);
			holder.text.setBackgroundColor(0x00000000);
		}

		return convertView;
	}
	
	public class ViewHolder
	{
		public ImageView iv;
		public ImageView selected;
		public TextView text;
	}
}
