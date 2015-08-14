package com.yjk.mobilesafety.service;

import java.util.List;

import com.yjk.mobilesafety.EnterPwdActivity;
import com.yjk.mobilesafety.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * ���Ź����� ��ʶϵͳ���������״̬
 * @author yjk
 *
 */
public class watchDogService extends Service {

	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	
	private InnerReceiver receiver;
	private String tempPackname;
	
	private ScreenOffReceiver screenOffreceiver;
	private ScreenOnReceiver screenOnreceiver;
	
	private DataChangedReceiver datachangeReceiver;
	
	private List<String> protectPackName;
	private Intent intent;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		dao = new AppLockDao(this);
		protectPackName = dao.findAll();
		flag = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		screenOffreceiver = new ScreenOffReceiver();
		registerReceiver(screenOffreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		screenOnreceiver = new ScreenOnReceiver();
		registerReceiver(screenOnreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		
		
		receiver = new InnerReceiver();
		registerReceiver(receiver, new IntentFilter("com.yjk.mobisafety.tempstop"));
		
		datachangeReceiver = new DataChangedReceiver();
		registerReceiver(datachangeReceiver, new IntentFilter("com.yjk.mobilesafety.applockchange"));
		intent = new Intent(watchDogService.this, 
				EnterPwdActivity.class);
		//����û������ջ��Ϣ���ڷ�����activity����Ҫָ����activity���е�����ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startWatchDog();
	}
	
	private void startWatchDog() {
		// TODO Auto-generated method stub
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while(flag){
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					
					//��ѯ���ݿ�̫�� ��ѯ�ڴ�
					if(protectPackName.contains(packname) && !packname.equals(tempPackname)){
						intent.putExtra("packname", packname);
						startActivity(intent);
					}
					//Log.d("debuge", packname);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
		}.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
		unregisterReceiver(receiver);
		unregisterReceiver(screenOffreceiver);
		unregisterReceiver(screenOnreceiver);
		unregisterReceiver(datachangeReceiver);
	}

	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			tempPackname = intent.getStringExtra("packname");
			
		}
	}
	
	class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			tempPackname = null;
			flag = false;
		}
	}
	
	class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			flag = true;
			startWatchDog();
		}
	}
	
	class DataChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			protectPackName = dao.findAll();
		}
	}
}
