package com.satansin.android.compath;

import android.content.Context; 
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter; 
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

/**
 * 
 * @author ��ɽ����
 *  ͼƬ����������������ͼƬ
 */
public class ImageAdapter extends BaseAdapter {
//ͼƬ������
	// ����Context 
	private int ownposition;
	 
	private int count = 0;

	public int getOwnposition() {
		return ownposition;
	}

	public void setOwnposition(int ownposition) {
		this.ownposition = ownposition;
	}

	private Context mContext; 

	// ������������ ��ͼƬԴ

	// ���� ImageAdapter
	public ImageAdapter(Context c) {
		mContext = c;
	}

	public void setCount(int count){
		this.count = count;
	}
	// ��ȡͼƬ�ĸ���
	public int getCount() {
		return count;
	}

	// ��ȡͼƬ�ڿ��е�λ��
	public Object getItem(int position) { 
		ownposition=position;
		return position;
	}

	// ��ȡͼƬID
	public long getItemId(int position) {
		ownposition=position; 
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		 
		ownposition=position;
		ImageView imageview = new ImageView(mContext);
		imageview.setBackgroundColor(0xFF000000);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageview.setLayoutParams(new GalleryExt.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

//		imageview.setImageResource(ImageSource.mThumbIds[position]);
		//TODO
		imageview.setImageBitmap(null);
		
		// imageview.setAdjustViewBounds(true);
		// imageview.setLayoutParams(new GridView.LayoutParams(320, 480));
		// imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		return imageview;
	}
}
