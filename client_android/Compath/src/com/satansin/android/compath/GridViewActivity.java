package com.satansin.android.compath;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.satansin.android.compath.logic.GroupPicService;
import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.qiniu.ImageServiceQiniuImpl;
import com.satansin.android.compath.socket.GroupPicServiceSocketImpl;

public class GridViewActivity extends Activity {
	private DisplayMetrics dm;
	private GridImageAdapter ia;
	private GridView g;
	private int imageCol = 3;

	private int picCount = 0;
	private Bitmap[] photos = null;
	int groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		ia = new GridImageAdapter(this);
		setContentView(R.layout.mygridview);
		
		Intent intent = getIntent();
		groupId = intent.getIntExtra("groupId", 0);	
		photos = loadImages();
		g = (GridView) findViewById(R.id.myGrid);
		g.setAdapter(ia);
		g.setOnItemClickListener(new OnItemClick(this)); 
		//得到屏幕的大小
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm); 

	}
 

	/**
	 * 屏幕切换时进行处理
	 * 如果屏幕是竖屏，则显示3列，如果是横屏，则显示4列
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {

			super.onConfigurationChanged(newConfig);
			//如果屏幕是竖屏，则显示3列，如果是横屏，则显示4列
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				imageCol = 4;
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				imageCol = 3;
			}
			g.setNumColumns(imageCol);
			g.setAdapter(new ImageAdapter(this));
			// ia.notifyDataSetChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @author 空山不空
	 * 点击具体的小图片时，会链接到GridViewActivity页面，进行加载和展示 
	 */
	public class OnItemClick implements OnItemClickListener {
		public OnItemClick(Context c) {
			mContext = c;
		} 
		@Override
		public void onItemClick(AdapterView aview, View view, int position,
				long arg3) {
			Intent intent = new Intent();
			intent.setClass(GridViewActivity.this, GalleryActivity.class);
			intent.putExtra("position", position);
			intent.putExtra("picCount", picCount);
			GridViewActivity.this.startActivity(intent);
		} 
		private Context mContext;
	}

	/**
	 * 
	 * @author 空山不空
	 * 设置GridView的图片适配器
	 */
	public class GridImageAdapter extends BaseAdapter {

		Drawable btnDrawable;

		public GridImageAdapter(Context c) {
			mContext = c;
			Resources resources = c.getResources();
			btnDrawable = resources.getDrawable(R.drawable.bg);
		}

		public int getCount() {
			return picCount;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageViewExt imageView;

			if (convertView == null) {
				imageView = new ImageViewExt(mContext);
				//如果是横屏，GridView会展示4列图片，需要设置图片的大小 
				if (imageCol == 4) {
					imageView.setLayoutParams(new GridView.LayoutParams(
							dm.heightPixels / imageCol - 6, dm.heightPixels
									/ imageCol - 6));
				} else {//如果是竖屏，GridView会展示3列图片，需要设置图片的大小 
					imageView.setLayoutParams(new GridView.LayoutParams(
							dm.widthPixels / imageCol - 6, dm.widthPixels
									/ imageCol - 6));
				}
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

				// imageView.setPadding(3, 3, 3, 3);
			} else {
				imageView = (ImageViewExt) convertView;
			}
			// 动画效果
			// Animation an=
			// AnimationUtils.loadAnimation(mContext,R.anim.zoom_enter );
			// imageView.setAnimation(an);

			// Resources res = getResources();
			// //将Drawable转化为Bitmap
			// Bitmap bitmap
			// =ImageUtils.drawableToBitmap(res.getDrawable(mThumbIds[position]));
			// //缩放图片
			// Bitmap zoomBitmap = ImageUtils.zoomBitmap(bitmap, 100,100);
			// //获取圆角图片
			// Bitmap roundBitmap =
			// ImageUtils.getRoundedCornerBitmap(zoomBitmap, 10.0f);

			// imageView.setImageBitmap(roundBitmap);
//			imageView.setImageResource(ImageSource.mThumbIds[position]);
			
			//TODO
			imageView.setImageBitmap(null);

			return imageView;
		}

		private Context mContext;

			}
	
	//TODO
	public Bitmap[] loadImages(){
		GroupPicService service = new GroupPicServiceSocketImpl();
		ImageService imageService = new ImageServiceQiniuImpl(this);
		List<String> urlList = null;
		try {
			urlList = service.getGroupPics(groupId);
		} catch (NetworkTimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println("NetworkTimeoutException");
			e.printStackTrace();
		} catch (UnknownErrorException e) {
			// TODO Auto-generated catch block
			System.out.println("UnknownErrorException");
			e.printStackTrace();
		}
		if(urlList == null){
			System.out.println("urlList = null");
		}
		int size = urlList.size();
		Bitmap[] bitmaps = new Bitmap[size];
		for(int i = 0; i < size; i++){
			Bitmap bitmap = imageService.getBitmap(urlList.get(i), ImageService.ORIGIN);
			bitmaps[i] = bitmap;
		}
		picCount = bitmaps.length;
		return bitmaps;
	}
}
