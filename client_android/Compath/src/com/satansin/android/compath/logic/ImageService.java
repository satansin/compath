package com.satansin.android.compath.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public interface ImageService {

	public static final int ALBUM = 0;
	public static final int ORIGIN = 1;
	public static final int THUMB_ICON = 2;

	public Bitmap getBitmap(String url, int type) throws UnknownErrorException;
	
	public String uploadBitmap(Context context, String uptoken, Uri uri) throws UnknownErrorException, NetworkTimeoutException;
	
}
