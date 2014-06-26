package com.satansin.android.compath.qiniu;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

public class ImageServiceQiniuImpl implements ImageService {
	
	private Context context;
	
	public ImageServiceQiniuImpl(Context context) {
		this.context = context;
	}
	
	private int getFileQualityCode(int type) {
		switch (type) {
		case ORIGIN:
			return MemoryService.IMG_ORIGIN;
		case THUMB_ICON:
			return MemoryService.IMG_THUMB_L;
		default:
			return 0;
		}
	}

	@Override
	public Bitmap getBitmap(String url, int type) throws UnknownErrorException {
		String[] urlSplit = url.split("/");
		if (urlSplit.length <= 0) {
			return null;
		}
		String fileName = urlSplit[urlSplit.length - 1];
		
		MemoryService memoryService = ServiceFactory.getMemoryService(context);
		Bitmap localBitmap = memoryService.getLocalImage(fileName, getFileQualityCode(type));
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
			memoryService.putLocalImage(remoteBitmap, fileName, getFileQualityCode(type));
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
