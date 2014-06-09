package com.satansin.android.compath.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class UserSessionAdapter {
	public static final String KEY_ROWID = "id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_SESSION = "session";
	
	private DatabaseUtil dbUtil;
	private SQLiteDatabase mDb;

	private static final String DATABASE_TABLE = "userSession";

	private final Context mCtx;

	public UserSessionAdapter(Context mCtx) {
		this.mCtx = mCtx;
	}
	
   public SQLiteDatabase getMDb() {
		return mDb;
	}
   
   public UserSessionAdapter open() throws SQLException {
		dbUtil = new DatabaseUtil(mCtx);
		mDb = dbUtil.getWritableDatabase();
		return this;
	}

	public void close() {
		dbUtil.close();
	}
	
	
	public long insertUS(String username,String session) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_SESSION, session);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean updateUS(long rowId, String username,String session) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_SESSION, session);
		return mDb.update(DATABASE_TABLE, initialValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}
	
	public Cursor fetchIdByUsername(String username) {
		String sql = "select id from "+DATABASE_TABLE+" where username="+username+" ;";
		return mDb.rawQuery(sql, null);
	}

}
