package com.satansin.android.compath.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {
	
	public static String getEncryptedPassword(String pwd) {
		byte[] btInput = pwd.getBytes();
		char[] md5String = new String("0123456789ABCDEF").toCharArray();
		try {
			MessageDigest mdInst;
			mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = md5String[byte0 >>> 4 & 0xf]; 
				str[k++] = md5String[byte0 & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

}
