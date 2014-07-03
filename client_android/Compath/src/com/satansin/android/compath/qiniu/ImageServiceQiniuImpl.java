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
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

public class ImageServiceQiniuImpl implements ImageService {
	
	private Context context;
	
	public ImageServiceQiniuImpl(Context context) {
		this.context = context;
	}
	
	private int getFileQualityCode(int type) {
		switch (type) {
		case ALBUM:
			return MemoryService.IMG_ALBUM;
		case ORIGIN:
			return MemoryService.IMG_ORIGIN;
		case THUMB_ICON_DISCUSS:
			return MemoryService.IMG_THUMB_L;
		case THUMB_ICON_PERSONAL_SETTINGS:
			return MemoryService.IMG_THUMB_H;
		case THUMB_GALLERY:
			return MemoryService.IMG_THUMB_H;
		default:
			return 0;
		}
	}
	
	private String getTargetImgSizeParams(int type) {
		switch (type) {
		case ALBUM:
			return "";
		case ORIGIN:
			return "";
		case THUMB_ICON_DISCUSS:
			return "?imageView2/1/w/60/h/60";
		case THUMB_ICON_PERSONAL_SETTINGS:
			return "?imageView2/1/w/96/h/96";
		case THUMB_GALLERY:
			// TODO
		default:
			return "";
		}
	}

	@Override
	public Bitmap getBitmap(String url, int type) {
		String[] urlSplit = url.split("/");
		if (urlSplit.length <= 0) {
			return null;
		}
		String fileName = urlSplit[urlSplit.length - 1];
		
		MemoryService memoryService = ServiceFactory.getMemoryService(context);
		Bitmap localBitmap = null;
		try {
			localBitmap = memoryService.getLocalImage(fileName, getFileQualityCode(type));
		} catch (UnknownErrorException e) {
		}
		if (localBitmap != null) {
			return localBitmap;
		}
		
		try {
			URL httpUrl = new URL(url + getTargetImgSizeParams(type));
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
	
	/**
	 * @return 图片上传到服务器上的完整路径
	 * @throws Exception 
	 */
	@Override
	public void uploadBitmap(Context context, String uptoken, Uri uri, JSONObjectRet ret) throws UnknownErrorException, NetworkTimeoutException {
		IO.putFile(context, uptoken, IO.UNDEFINED_KEY, uri, new PutExtra(), ret);
	}

}
