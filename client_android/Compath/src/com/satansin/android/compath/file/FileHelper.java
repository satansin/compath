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
import com.satansin.android.compath.logic.ImageService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileHelper {
	
	private Context context;

    /** SD卡是否存在**/
    private boolean hasSD = false;

    /** SD卡的路径**/
    private String SDPATH;

//    /** 当前程序包的路径**/
//    private String FILESPATH;
    
    private static final String FILENAME_SESSION = "ssn";
    private static final String FILENAME_CITIES = "cty";
    private static final String DIRNAME_IMAGE = "img";
    private static final String DIRNAME_IMAGE_ORIGIN = "ori";
    private static final String DIRNAME_IMAGE_THUMB_ICON = "tmi";
    
    static final int OBJECT_SESSION = 1;

	static final int OBJECT_CITY_LIST = 2;

    public FileHelper(Context context) {
        this.context = context;
        hasSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        SDPATH = Environment.getExternalStorageDirectory().getPath() + "/Compath";
//        FILESPATH = this.context.getFilesDir().getPath();
        
        File sdDir = new File(SDPATH);
        if (!sdDir.exists()) {
			sdDir.mkdir();
		}
        
        File imageDir = new File(SDPATH + "/" + DIRNAME_IMAGE);
        if (!imageDir.exists()) {
			imageDir.mkdir();
		}
        
        File imageOriginDir = new File(SDPATH + "/" + DIRNAME_IMAGE + "/" + DIRNAME_IMAGE_ORIGIN);
        if (!imageOriginDir.exists()) {
        	imageOriginDir.mkdir();
		}
        
        File imageThumbIconDir = new File(SDPATH + "/" + DIRNAME_IMAGE + "/" + DIRNAME_IMAGE_THUMB_ICON);
        if (!imageThumbIconDir.exists()) {
        	imageThumbIconDir.mkdir();
		}
    }
    
    private String getFilePath(int objectType) {
    	switch (objectType) {
		case OBJECT_SESSION:
			return (SDPATH + "/" + FILENAME_SESSION);

		default:
			break;
		}
    	return "";
    }
    
    private InputStream getFileInputStream(int objectType) throws IOException {
    	switch (objectType) {
		case OBJECT_SESSION:
			return getSDFileInputStream(FILENAME_SESSION);
		case OBJECT_CITY_LIST:
			return getAssetsFileInputStream(FILENAME_CITIES);
		default:
			break;
		}
    	return null;
    }
    
    private OutputStream getFileOutputStream(int objectType) throws IOException {
    	switch (objectType) {
		case OBJECT_SESSION:
			return getSDFileOutputStream(FILENAME_SESSION);
		case OBJECT_CITY_LIST:
			return null;
		default:
			break;
		}
    	return null;
    }
	
	private OutputStream getSDFileOutputStream(String fileName) throws IOException {
		if (!hasSD) {
			return null;
		}
		File file = new File(SDPATH + "/" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}

	private InputStream getSDFileInputStream(String fileName) throws IOException {
		if (!hasSD) {
			return null;
		}
		File file = new File(SDPATH + "/" + fileName);
		if (!file.exists()) {
			return null;
		}
		return new FileInputStream(file);
	}
	
	private InputStream getAssetsFileInputStream(String fileName) throws IOException {
		return context.getAssets().open(fileName);
	}
	
//    /**
//     * 在SD卡上创建文件
//     * 
//     * @throws IOException
//     */
//    public File createSDFile(String fileName) throws IOException {
//        File file = new File(SDPATH + "/" + fileName);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        return file;
//    }
//
//    /**
//     * 删除SD卡上的文件
//     * 
//     * @param fileName
//     */
//    public boolean deleteSDFile(String fileName) {
//        File file = new File(SDPATH + "/" + fileName);
//        if (file == null || !file.exists() || file.isDirectory())
//            return false;
//        return file.delete();
//    }
//
//    /**
//     * 读取SD卡中文本文件
//     * 
//     * @param fileName
//     * @retur
//     */
//    public String readSDFile(String fileName) {
//        StringBuffer sb = new StringBuffer();
//        File file = new File(SDPATH + "/" + fileName);
//        try {
//            FileInputStream fis = new FileInputStream(file);
//            int c;
//            while ((c = fis.read()) != -1) {
//                sb.append((char) c);
//            }
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return sb.toString();
//    }
//
//    public String getFILESPATH() {
//        return FILESPATH;
//    }
//
//    public String getSDPATH() {
//        return SDPATH;
//    }
//
//    public boolean hasSD() {
//        return hasSD; 
//    }

	public Object readObjectFromFile(int objectType) {
		try {
			InputStream stream = getFileInputStream(objectType);
			if (stream == null) {
				return null;
			}

			ObjectInputStream objectInputStream = new ObjectInputStream(stream);
			Object object = objectInputStream.readObject();

			switch (objectType) {
			case OBJECT_SESSION:
				object = (Session) object;
				break;
			default:
				break;
			}
			objectInputStream.close();
			return object;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean writeObjectToFile(Object object, int objectType) {
		try {
			OutputStream stream = getFileOutputStream(objectType);
			if (stream == null) {
				return false;
			}
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void deleteObject(int objectType) {
		try {
			File objectFile = new File(getFilePath(objectType));
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
		public Session(String usrname, String session) {
			this.usrname = usrname;
			this.session = session;
		}
	}
    
    private String getImageFilePath(String url, int quality) {
    	String parent = "";
    	switch (quality) {
		case ImageService.ORIGIN:
			parent = DIRNAME_IMAGE_ORIGIN;
			break;
		case ImageService.THUMB_ICON:
			parent = DIRNAME_IMAGE_THUMB_ICON;
			break;
		default:
			break;
		}
    	return (SDPATH + "/" + DIRNAME_IMAGE + "/" + parent + "/" + url);
    }

	public Bitmap getLocalImage(String url, int quality) {
		try {
			FileInputStream stream = new FileInputStream(getImageFilePath(url, quality));
			return BitmapFactory.decodeStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean putLocalImage(String url, Bitmap bitmap, int quality) {
		try {
			File file = new File(getImageFilePath(url, quality));
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // TODO format and quality selection
			stream.flush();
			stream.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
    
}
