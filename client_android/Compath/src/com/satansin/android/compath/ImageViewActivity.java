package com.satansin.android.compath;

import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.ServiceFactory;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

public class ImageViewActivity extends ActionBarActivity {

	public static final String EXTRA_THUMBNAIL = "com.satansin.android.compath.EXTRA_THUMBNAIL";
	public static final String EXTRA_URL = "com.satansin.android.compath.EXTRA_URL";
	
	private String url;
	
	private ImageView imageContainer;
	
	private GetOriginImageTask getOriginImageTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		
		imageContainer = (ImageView) findViewById(R.id.image_container);
		imageContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageViewActivity.this.finish();
			}
		});

		Bundle bundle = getIntent().getExtras();
		Object bitmapObject = bundle.get(EXTRA_THUMBNAIL);
		if (bitmapObject != null) {
			Bitmap thumbnail = (Bitmap) bitmapObject;
			imageContainer.setImageBitmap(thumbnail);
		}
		
		url = bundle.getString(EXTRA_URL);
		if (url == null || url.length() == 0) {
			return;
		}
		
		getOriginImageTask = new GetOriginImageTask();
		getOriginImageTask.execute();
	}
	
	private class GetOriginImageTask extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... params) {
			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
			return imageService.getBitmap(url, ImageService.ORIGIN);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			getOriginImageTask = null;
			if (result != null) {
				imageContainer.setImageBitmap(result);
			}
		}
	}

}
