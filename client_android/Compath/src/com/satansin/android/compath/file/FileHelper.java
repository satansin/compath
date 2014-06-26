package com.satansin.android.compath.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.MemoryService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileHelper {
	
	private Context context;

    private File sdRootDir;
    private File imgRootDir;
    private File albumImgDir;
    private File originImgDir;
    private File thumbHImgDir;
    private File thumbLImgDir;
    private File usrRootDir;

    private static final String DIRNAME_ROOT = "LocChat";
    private static final String FILENAME_SESSION = "ssn";
    private static final String FILENAME_CITIES = "cty";
    private static final String DIRNAME_IMAGE = "img";
	private static final String DIRNAME_IMAGE_ALBUM = "album";
    private static final String DIRNAME_IMAGE_ORIGIN = "ori";
    private static final String DIRNAME_IMAGE_THUMB_H = "tbh";
    private static final String DIRNAME_IMAGE_THUMB_L = "tbl";
    private static final String DIRNAME_USR = "usr";
	private static final String FILENAME_HISTORY_MESSAGES = "hms";
    
    public static final int OBJECT_SESSION = 1;
	public static final int OBJECT_CITY_LIST = 2;
	public static final int OBJECT_HISTORY_MESSAGES = 3;

    public FileHelper(Context context) throws StorageNotFoundException {
        this.context = context;
        boolean hasSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!hasSD) {
			throw new StorageNotFoundException();
		}

//      FILESPATH = this.context.getFilesDir().getPath();
        
        sdRootDir = new File(Environment.getExternalStorageDirectory(), DIRNAME_ROOT);
        if (!sdRootDir.exists()) {
        	sdRootDir.mkdir();
		}
        
        imgRootDir = new File(sdRootDir, DIRNAME_IMAGE);
        if (!imgRootDir.exists()) {
        	imgRootDir.mkdir();
		}
        
        albumImgDir = new File(imgRootDir, DIRNAME_IMAGE_ALBUM);
        if (!albumImgDir.exists()) {
        	albumImgDir.mkdir();
		}
        
        originImgDir = new File(imgRootDir, DIRNAME_IMAGE_ORIGIN);
        if (!originImgDir.exists()) {
        	originImgDir.mkdir();
		}
        
        thumbHImgDir = new File(imgRootDir, DIRNAME_IMAGE_THUMB_H);
        if (!thumbHImgDir.exists()) {
        	thumbHImgDir.mkdir();
		}
        
        thumbLImgDir = new File(imgRootDir, DIRNAME_IMAGE_THUMB_L);
        if (!thumbLImgDir.exists()) {
        	thumbLImgDir.mkdir();
		}
        
        usrRootDir = new File(sdRootDir, DIRNAME_USR);
        if (!usrRootDir.exists()) {
        	usrRootDir.mkdir();
		}
        
    }
    
    private File getFile(int objectType) {
    	switch (objectType) {
		case OBJECT_SESSION:
			return new File(sdRootDir, FILENAME_SESSION);

		default:
			break;
		}
    	return null;
    }
    
    private File getUsrDir(String usrname) throws IOException {
		File usrDir = new File(usrRootDir, usrname);
		if (!usrDir.exists()) {
        	usrDir.mkdir();
		}
		File historyFile = new File(usrDir, FILENAME_HISTORY_MESSAGES);
		if (!historyFile.exists()) {
			historyFile.createNewFile();
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(historyFile));
			stream.writeObject(0);
			stream.close();
		}
		return usrDir;
    }
    
    private InputStream getUsrFileInputStream(int objectType, String usrname) throws IOException {
    	switch (objectType) {
		case OBJECT_HISTORY_MESSAGES:
			return getSDFileInputStream(getUsrDir(usrname), FILENAME_HISTORY_MESSAGES);

		default:
			return null;
		}
    }
    
    private InputStream getFileInputStream(int objectType) throws IOException {
    	switch (objectType) {
		case OBJECT_SESSION:
			return getSDFileInputStream(sdRootDir, FILENAME_SESSION);
		case OBJECT_CITY_LIST:
			return getAssetsFileInputStream(FILENAME_CITIES);
		default:
			break;
		}
    	return null;
    }
    
    private OutputStream getUsrFileOutputStream(int objectType, String usrname) throws IOException {
    	switch (objectType) {
		case OBJECT_HISTORY_MESSAGES:
			return getSDFileOutputStream(getUsrDir(usrname), FILENAME_HISTORY_MESSAGES);

		default:
			return null;
		}
    }
    
    private OutputStream getFileOutputStream(int objectType) throws IOException {
    	switch (objectType) {
		case OBJECT_SESSION:
			return getSDFileOutputStream(sdRootDir, FILENAME_SESSION);
		case OBJECT_CITY_LIST:
			return null;
		default:
			break;
		}
    	return null;
    }
	
	private OutputStream getSDFileOutputStream(File rootDir, String fileName) throws IOException {
		File file = new File(rootDir, fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}

	private InputStream getSDFileInputStream(File rootDir, String fileName) throws IOException {
		File file = new File(rootDir, fileName);
		if (!file.exists()) {
			return null;
		}
		return new FileInputStream(file);
	}
	
	private InputStream getAssetsFileInputStream(String fileName) throws IOException {
		return context.getAssets().open(fileName);
	}

//	public Object readObjectFromFile(int objectType) {
//		try {
//			InputStream stream = getFileInputStream(objectType);
//			if (stream == null) {
//				return null;
//			}
//
//			ObjectInputStream objectInputStream = new ObjectInputStream(stream);
//			Object object = objectInputStream.readObject();
//
//			switch (objectType) {
//			case OBJECT_SESSION:
//				object = (Session) object;
//				break;
//			default:
//				break;
//			}
//			objectInputStream.close();
//			return object;
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	public boolean writeObjectToFile(Object object, int objectType) {
//		try {
//			OutputStream stream = getFileOutputStream(objectType);
//			if (stream == null) {
//				return false;
//			}
//			ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
//			objectOutputStream.writeObject(object);
//			objectOutputStream.close();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}

	public void deleteObject(int objectType) {
		try {
			File objectFile = getFile(objectType);
			if (objectFile.exists()) {
				objectFile.delete();
			}
		} catch (Exception e) {
			return;
		}
	}

	public List<City> readListFromFile(int objectType) {
		try {
			InputStream stream = getFileInputStream(objectType);
			if (stream == null) {
				return null;
			}

			switch (objectType) {
			case OBJECT_CITY_LIST:
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "GBK"));
				String line;
				ArrayList<City> list = new ArrayList<City>();
				while ((line = bufferedReader.readLine()) != null) {
					String[] split = line.split("\\|");
					list.add(new City(
							Integer.parseInt(split[0]),
							split[1],
							Integer.parseInt(split[2]),
							Integer.parseInt(split[3]),
							split[4]));
				}
				return list;
			default:
				break;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	static class Session implements Serializable {
		private static final long serialVersionUID = 5247494384157415L;
		String usrname;
		String session;
		String iconUrl;
		public Session(String usrname, String session, String iconUrl) {
			this.usrname = usrname;
			this.session = session;
			this.iconUrl = iconUrl;
		}
	}
    
    private File getImageFile(String fileName, int quality) {
    	File parent = null;
    	switch (quality) {
    	case MemoryService.IMG_ALBUM:
    		parent = albumImgDir;
    		break;
		case MemoryService.IMG_ORIGIN:
			parent = originImgDir;
			break;
		case MemoryService.IMG_THUMB_H:
			parent = thumbHImgDir;
			break;
		case MemoryService.IMG_THUMB_L:
			parent = thumbLImgDir;
			break;
		default:
			return null;
		}
    	File imgFile = new File(parent, fileName);
    	Log.w("image_path", imgFile.getPath());
    	return imgFile;
    }

	public Bitmap getLocalImage(String fileName, int quality) throws IOException {
		File imgFile = getImageFile(fileName, quality);
    	if (!imgFile.exists()) {
			throw new IOException();
		}
		FileInputStream stream = new FileInputStream(imgFile);
		Options options = new Options();
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
		stream.close();
		return bitmap;
	}
	
	public Uri putLocalImage(String fileName, Bitmap bitmap, int quality) throws IOException {
		File imgFile = getImageFile(fileName, quality);
		if (!imgFile.exists()) {
			throw new IOException();
		}
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(imgFile));
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // TODO format and quality selection
		stream.flush();
		stream.close();
		return Uri.fromFile(imgFile);
	}

	public Uri getLocalImageUri(String fileName, int quality) {
		File imgFile = getImageFile(fileName, quality);
		return Uri.fromFile(imgFile);
	}

	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	public void openSerialFile(int objectType) throws IOException {
		inputStream = new ObjectInputStream(getFileInputStream(objectType));
		outputStream = new ObjectOutputStream(getFileOutputStream(objectType));
	}
	
	public void openUsrSerialFile(int objectType, String usrname) throws IOException {
		inputStream = new ObjectInputStream(getUsrFileInputStream(objectType, usrname));
		outputStream = new ObjectOutputStream(getUsrFileOutputStream(objectType, usrname));
	}

	public int getIntFromSerialFile() throws Exception {
		Object object = inputStream.readObject();
		if (object == null) {
			throw new IOException();
		}
		int result = 0;
		try {
			result = (int) object;
		} catch (Exception e) {
			throw new ClassCastException();
		}
		return result;
	}

	public Object getObjectFromSerialFile() throws Exception {
		Object object = inputStream.readObject();
		if (object == null) {
			throw new IOException();
		}
		return object;
	}

	public boolean writeObjectToSerialFile(Object object) throws IOException {
		outputStream.writeObject(object);
		return true;
	}

	public void closeSerialFile() throws IOException {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		if (outputStream != null) {
			outputStream.close();
			outputStream = null;
		}
	}
    
}
