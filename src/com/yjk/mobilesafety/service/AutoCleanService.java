package com.yjk.mobilesafety.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanService extends Service {

	private SreeenOffReceiver receiver;
	private ActivityManager am;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		receiver = new SreeenOffReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	};
	
	private class SreeenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			List<RunningAppProcessInfo> infos= am.getRunningAppProcesses();
			for(RunningAppProcessInfo info : infos){
				am.killBackgroundProcesses(info.processName);
			}
		}
	}

}
