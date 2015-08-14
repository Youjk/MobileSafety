package com.yjk.mobilesafety.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yjk.mobilesafety.db.BlackNumberDBOpenHelper;
import com.yjk.mobilesafety.domain.BlackNumberInfo;

/**
 *   黑名单数据库的增删改查业务类
 *   @author yjk
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
	
	public BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询黑名单号码是否存在
	 */
	
	public boolean find(String number){
		boolean result = false;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number = ?", 
				new String[]{number});
		while(cursor.moveToNext()){
			result = true;
		}
		
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * 查询黑名单号码的拦截模式
	 * @return 返回号码的拦截模式 如果不是则返回空
	 */
	
	public String findMode(String number){
		String result = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number = ?", 
				new String[]{number});
		while(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		
		cursor.close();
		db.close();
		return result;
	}
	
	/*
	 * 插入号码
	 * @param number 拦截号码
	 * @param mode 拦截模式  1、电话拦截 2、短信拦截 3、全部拦截
	 */
	
	public void add(String number, String mode){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		db.execSQL("insert into blacknumber " +
				"(number, mode) values(?, ?);", 
					new String[]{number, mode});
		
		db.close();
	}
	
	/*
	 * 修改号码的拦截模式
	 * @param number 要修改的拦截号码
	 * @param newmode 新的拦截模式
	 */
	public void update(String number, String newmode){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		db.execSQL("update blacknumber set mode = ? where number = ?;",
				new String[]{newmode, number});
		
		db.close();
	}
	
	/*
	 * 删除黑名单号码
	 * @param number 要删除的号码
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		db.execSQL("delete from blacknumber where number = ?;",
				new String[]{number});
		
		db.close();
	}
	
	/**
	 *   查询所有黑名单号码
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number, mode from blacknumber order by _id desc ", null);
		
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 *   查询部分黑名单号码
	 * @param offset   从哪个位置开始查询
	 * @param maxNumber 一次最多获取多少条记录
	 */
	public List<BlackNumberInfo> findPart(int offset, int maxNumber){
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber order by _id desc limit ? offset ? ", 
				new String[]{String.valueOf(maxNumber), String.valueOf(offset)});
		
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		
		cursor.close();
		db.close();
		return result;
	}
}
