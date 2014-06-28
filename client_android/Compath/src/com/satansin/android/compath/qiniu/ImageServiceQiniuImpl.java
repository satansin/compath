package com.satansin.android.compath.qiniu;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	private String uploadedUrl = "";
	private Exception uploadException;

	/**
	 * @return 图片上传到服务器上的完整路径
	 * @throws Exception 
	 */
	@Override
	public String uploadBitmap(Context context, String uptoken, Uri uri) throws UnknownErrorException, NetworkTimeoutException {
		IO.putFile(context, uptoken, IO.UNDEFINED_KEY, uri, new PutExtra(), new JSONObjectRet() {
			@Override
			public void onFailure(Exception ex) {
				uploadException = new NetworkTimeoutException();
			}
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					uploadedUrl = Conf.SERVER_DOMAIN + obj.getString("hash");
				} catch (JSONException e) {
					uploadException = new UnknownErrorException();
				}
			}
		});
		if (uploadException != null) {
			if (uploadException instanceof NetworkTimeoutException) {
				throw (NetworkTimeoutException) uploadException;
			}
			if (uploadException instanceof UnknownErrorException) {
				throw (UnknownErrorException) uploadException;
			}
		}
		return uploadedUrl;
	}

}
