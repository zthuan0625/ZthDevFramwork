package com.zthdev.adapter;

import java.io.File;
import java.util.List;

import com.zthdev.bean.ImageFolder;
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
 * 类名称：ImageBucketAdapter <br>  
 * 类描述：图片选择文件夹适配器 <br>
 * 创建人：赵腾欢   
 * 创建时间：2015-1-7 下午4:51:42 <br>  
 * @version V1.0
 */
public class ImageBucketAdapter extends BaseAdapter
{
	/**
	 * 图片集列表
	 */
	List<ImageFolder> dataList;

	private Context context;

	public ImageBucketAdapter(Context context, List<ImageFolder> list)
	{
		this.context = context;
		dataList = list;
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

	class Holder
	{
		private ImageView iv;
		private ImageView selected;
		private TextView name;
		private TextView count;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		Holder holder;
		if (convertView == null)
		{
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_image_bucket, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else
		{
			holder = (Holder) convertView.getTag();
		}
		ImageFolder item = dataList.get(position);
		holder.count.setText("" + item.count);
		holder.name.setText(item.bucketName);
		holder.selected.setVisibility(View.GONE);
		if (item.imageList != null && item.imageList.size() > 0)
		{
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;

			if (!StringUtils.isEmpty(sourcePath))
			{
				ZImgLoaders.with(context).prepare().reSize(150, 150)
						.load(new File(sourcePath)).into(holder.iv).start();
			} else if (!StringUtils.isEmpty(thumbPath))
			{
				ZImgLoaders.with(context).prepare().load(new File(thumbPath))
						.into(holder.iv).start();
			}
		} else
		{
			holder.iv.setImageBitmap(null);
		}
		return convertView;
	}

}
