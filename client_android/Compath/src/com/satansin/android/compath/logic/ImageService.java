package com.satansin.android.compath.logic;

import com.satansin.android.compath.qiniu.JSONObjectRet;
import com.satansin.android.compath.qiniu.PutExtra;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public interface ImageService {
	
	public static final int ORIGIN = 1;
	public static final int THUMB_ICON = 2;

	public Bitmap getBitmap(String url, int type) throws UnknownErrorException;
	
	// TODO
	public void uploadBitmap(Context context, String uptoken, String key, Uri uri, PutExtra extra, JSONObjectRet ret);
	
}
