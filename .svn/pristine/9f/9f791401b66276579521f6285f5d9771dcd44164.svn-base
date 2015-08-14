package com.yjk.mobilesafety.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	/*
	 * 校验某个服务是否还活着
	 */
	
	public static boolean isServiceRunning(Context context, String seriviceNmae){
		//检验服务是否还活着
		ActivityManager am = (ActivityManager) context.
				getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		
		for(RunningServiceInfo info : infos){
			if(info.service.getClassName().equals(seriviceNmae))
				return true;
		}
		return false;
	}
}
