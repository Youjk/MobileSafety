package com.yjk.mobilesafety.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;


public class SystemInfoUtils {
	/**
	 * 获取正在运行的进程的数量
	 * @return
	 */
	public static int getRunningProcessCount(Context context){
		//PackageManger  //包管理器
		//ActivityManger //进程管理器，管理手机的活动信息，动态内容
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		return infos.size();
	}
	
	/**
	 * 获取手机剩余可用内存
	 * @param context 上下文
	 * @return 剩余内存
	 */
	public static long getAvaioMem(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		am.getMemoryInfo(info);
		
		return info.availMem;
	}
	
	/**
	 * 获取手机全部内存
	 * @param context 上下文
	 * @return 剩余内存 //byte
	 */
	public static long getTotalMem(Context context){
		//高版本4.0以上才有totalMem信息，运行在低版本会崩溃
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo info = new MemoryInfo();
//		am.getMemoryInfo(info);
		FileInputStream fis;
		try {
			fis = new FileInputStream("/proc/meminfo");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			for(char c : line.toCharArray()){
				if(c >= '0' && c <= '9'){
					sb.append(c);
				}
			}
			
			return Long.parseLong(sb.toString()) * 1024;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	
}
