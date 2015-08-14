package com.yjk.mobilesafety.service;

import java.util.Timer;
import java.util.TimerTask;

import com.yjk.mobilesafety.R;
import com.yjk.mobilesafety.receiver.MyWidgeReceiver;
import com.yjk.mobilesafety.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class updateWidgeService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		onReceiver = new ScreenOnReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		awm = AppWidgetManager.getInstance(this);
		startTimer();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopTimer();
		unregisterReceiver(onReceiver);
		unregisterReceiver(offReceiver);
		
	}
	
	private void stopTimer() {
		// TODO Auto-generated method stub
		if(timer != null && task != null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

	/**
	 * ��Ļ�ر�receiver
	 * @author yjk
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			stopTimer();
		}
		
	}
	
	/**
	 * ��Ļ�ر�receiver
	 * @author yjk
	 *
	 */
	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			startTimer();
		}
		
	}

	public void startTimer() {
		// TODO Auto-generated method stub
		if(timer == null && task == null){
			timer = new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d("debuge", "����widge");
					
					//���ø������
					ComponentName provider = new ComponentName(updateWidgeService.this, 
							MyWidgeReceiver.class);
					
					RemoteViews  views = new RemoteViews(getPackageName(), R.layout.process_widget);
					views.setTextViewText(R.id.process_count, "�������еĽ���:" 
							+ SystemInfoUtils.getRunningProcessCount(updateWidgeService.this) + "��");
					views.setTextViewText(R.id.process_memory, "ʣ������ڴ�:"
							+ Formatter.formatFileSize(updateWidgeService.this, SystemInfoUtils.getAvaioMem(updateWidgeService.this)));
					
					//����һ������������һ��Ӧ�ó����ִ��
					//�Զ���㲥��ɱ����̨�Ľ���
					Intent intent = new Intent();
					intent.setAction("com.yjk.mobilesafety.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, 
							intent, PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					
					awm.updateAppWidget(provider, views);
				}
			};
			timer.schedule(task, 0, 10000);
		}
	}
}
