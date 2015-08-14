package com.yjk.mobilesafety.receiver;

import java.util.List;

import com.yjk.mobilesafety.R;
import com.yjk.mobilesafety.service.updateWidgeService;
import com.yjk.mobilesafety.utils.SystemInfoUtils;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class killAllReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("debuge", "�յ�ɱ�����̹㲥");
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	
		 List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		 for(RunningAppProcessInfo info : infos){
			 am.killBackgroundProcesses(info.processName);
		 }
		 
		 //����С����
		//���ø������
			ComponentName provider = new ComponentName(context, 
					MyWidgeReceiver.class);
			
			RemoteViews  views = new RemoteViews(context.getPackageName(), R.layout.process_widget);
			views.setTextViewText(R.id.process_count, "�������еĽ���:" 
					+ SystemInfoUtils.getRunningProcessCount(context) + "��");
			views.setTextViewText(R.id.process_memory, "ʣ������ڴ�:"
					+ Formatter.formatFileSize(context, SystemInfoUtils.getAvaioMem(context)));
			
	
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			awm.updateAppWidget(provider, views);
	}

}
