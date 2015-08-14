package com.yjk.mobilesafety.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	
	private static String path = "data/data/com.yjk.mobilesafety/files/address.db";
	/*
	 * 传一个号码进来，返回一个归属地
	 */
	public static String queryNumber(String number){
		
		SQLiteDatabase database = SQLiteDatabase.
				openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String address = "";
		//手机号码13 14 15 16 18
		//手机号码
		if(number.length() < 7)
			return "";
		if(number.startsWith("1")){
			//手机号码
		
			Cursor cursor= database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?);", 
					new String[]{number.substring(0, 7)});
			
			while(cursor.moveToNext()){
				String location = cursor.getString(0);
				address = location;
			}
		}else if(number.startsWith("0")) {
				//处理长途座机号码
				//010-59790386
				Cursor cursor = database.rawQuery("select location from data2 where area = ?;",
						new String[]{number.substring(1, 3)});
				
				while(cursor.moveToNext()){
					String location = cursor.getString(0);
					address = location.substring(0, location.length() - 2);
				}
				cursor.close();
				
				//0855-59790386
				cursor = database.rawQuery("select location from data2 where area = ?",
						new String[]{number.substring(1, 4)});
				
				while(cursor.moveToNext()){
					String location = cursor.getString(0);
					address = location.substring(0, location.length() - 2);
				}
				cursor.close();	
			}

		return address;
	}
}
