package com.yjk.mobilesafety.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * ���ݿⴴ���Ĺ��췽�� ���ݿ���Ϊapplock.db
	 */
	public BlackNumberDBOpenHelper(Context context){
		super(context, "blacknumber.db", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//��ʼ�����ݿ�ı�ṹ
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
