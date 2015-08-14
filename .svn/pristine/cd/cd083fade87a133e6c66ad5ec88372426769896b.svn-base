package com.yjk.mobilesafety.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.yjk.mobilesafety.db.ApplockDBOpenHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 程序锁锁数据库名单
 * 删除，增加，返回所有，查找是否在数据库
 * @author yjk
 *
 */
public class AppLockDao {
	
	private Context context;
	private ApplockDBOpenHelper helper;
	public AppLockDao(Context context){
		helper = new ApplockDBOpenHelper(context);
		this.context = context;
	}
	
	/**
	 * 添加一个要锁定应用程序的包名
	 * @param 包名
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into applock (packname) values(?)",
				new String[]{packname});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.yjk.mobilesafety.applockchange");
		context.sendBroadcast(intent);
	}
	
	/**
	 * 删除一个程序锁记录
	 * @param packname
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from applock where packname = ?", 
				new String[]{packname});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.yjk.mobilesafety.applockchange");
		context.sendBroadcast(intent);
	}
	
	public boolean find(String packname){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id from applock where packname = ? ", 
				new String[]{packname});
		
		if(cursor.moveToNext()){
			return true;
		}
		
		cursor.close();
		db.close();
		return false;
	}
	
	/**
	 * 查询全部的包名
	 * @param packname
	 * @return
	 */
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select packname from applock", new String[]{});
		
		List<String> result = new ArrayList<String>();
		while(cursor.moveToNext()){
			result.add(cursor.getString(0));
		}
		
		cursor.close();
		db.close();
		return result;
	}
}
