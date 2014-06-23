package com.satansin.android.compath.qiniu;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.satansin.android.compath.file.FileHelper;
import com.satansin.android.compath.logic.ImageService;

public class ImageServiceQiniuImpl implements ImageService {
	
	private Context context;
	
	public ImageServiceQiniuImpl(Context context) {
		this.context = context;
	}

	@Override
	public Bitmap getBitmap(String url, int quality) {
		FileHelper helper = new FileHelper(context);
		Bitmap localBitmap = helper.getLocalImage(url, quality);
		if (localBitmap != null) {
			return localBitmap;
		}
		
		try {
			// TODO 加入根据quality的缩放
			URL httpUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
			connection.setConnectTimeout(0);
			connection.setDoInput(true);
			connection.connect();
			
			InputStream stream = connection.getInputStream();
			Bitmap remoteBitmap = BitmapFactory.decodeStream(stream);
			stream.close();
			
			if (remoteBitmap == null) {
				return null;
			}
			helper.putLocalImage(url, remoteBitmap, quality);
			return remoteBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void uploadBitmap(Context context, String uptoken, String key,
			Uri uri, PutExtra extra, JSONObjectRet ret) {
		// TODO Auto-generated method stub
		IO.putFile(context, uptoken, key, uri, extra, ret);
	}

}
