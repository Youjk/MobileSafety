package com.yjk.mobilesafety.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法 数据库名为applock.db
	 */
	public BlackNumberDBOpenHelper(Context context){
		super(context, "blacknumber.db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//初始化数据库的表结构
		db.execSQL("create table blacknumber("
				+ "_id integer primary key autoincrement,"
				+ "number varchar(20),"
				+ "mode varchar(20));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
