package com.satansin.android.compath.logic;

import com.satansin.android.compath.qiniu.JSONObjectRet;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public interface ImageService {

	public static final int ALBUM = 1;
	public static final int ORIGIN = 2;
	public static final int THUMB_ICON_DISCUSS = 3;
	public static final int THUMB_ICON_PERSONAL_SETTINGS = 4;
	public static final int THUMB_GALLERY = 5;
	public static final int THUMB_PIC_DISCUSS = 6;

	public Bitmap getBitmap(String url, int type);
	
	public void uploadBitmap(Context context, String uptoken, Uri uri, JSONObjectRet ret) throws UnknownErrorException, NetworkTimeoutException;
	
}
