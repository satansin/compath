package com.satansin.android.compath.logic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUtil extends SQLiteOpenHelper{
	public static final String DATABASE_NAME = "compath.db";
	public static final int DATABASE_VERSION = 1;

	private static final String US_TABLE_CREATE = "create table userSession (id integer primary key autoincrement, "
			+ "usrname text not null,session text not null);";

	private static final String HM_TABLE_CREATE = "create table historyMessage (id integer primary key autoincrement, "
			+ "groupId integer not null,date text not null,content integer not null,owner text not null£¬from text not null);";




	public DatabaseUtil(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(US_TABLE_CREATE);
		db.execSQL(HM_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
