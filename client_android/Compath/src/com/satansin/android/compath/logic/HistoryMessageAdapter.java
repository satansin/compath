package com.satansin.android.compath.logic;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HistoryMessageAdapter {

	public static final String KEY_ROWID = "id";
	public static final String KEY_GROUPID = "groupId";
	public static final String KEY_DATE = "date";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_OWNER = "owner";
	public static final String KEY_FROM = "from";
	
	private DatabaseUtil dbUtil;
	private SQLiteDatabase mDb;

	private static final String DATABASE_TABLE = "historyMessage";

	private final Context mCtx;

	public HistoryMessageAdapter(Context mCtx) {
		this.mCtx = mCtx;
	}
	
   public SQLiteDatabase getMDb() {
		return mDb;
	}
   
   public HistoryMessageAdapter open() throws SQLException {
		dbUtil = new DatabaseUtil(mCtx);
		mDb = dbUtil.getWritableDatabase();
		return this;
	}

	public void close() {
		dbUtil.close();
	}
	
	public Cursor fetchByGroupId(int groupId) {
		String sql = "select id, date, content, owner from "+DATABASE_TABLE+" where groupId="+groupId+" ;";
		return mDb.rawQuery(sql, null);
	}
}
