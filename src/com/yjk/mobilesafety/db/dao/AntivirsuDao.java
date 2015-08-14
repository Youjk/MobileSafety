package com.yjk.mobilesafety.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 病毒数据库查询业务类
 * @author yjk
 *
 */
public class AntivirsuDao {
	
	public static boolean isVirus(String md5){
		
		boolean result = false;
		//打开数据库源文件
		String path = "data/data/com.yjk.mobilesafety/files/antivirus.db";
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5 = ?", new String[]{md5});
		
		if(cursor.moveToNext()){
			result = true;
		}
		
		cursor.close();
		db.close();
		
		return result;
	}
}
