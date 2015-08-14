package com.yjk.mobilesafety.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法 数据库名为blacknumber.db
	 */
	public ApplockDBOpenHelper(Context context){
		super(context, "applock.db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//初始化数据库的表结构
				db.execSQL("create table applock("
						+ "_id integer primary key autoincrement,"
						+ "packname varchar(20));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
