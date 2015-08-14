package com.yjk.mobilesafety.receiver;

import com.yjk.mobilesafety.service.updateWidgeService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;


public class MyWidgeReceiver extends AppWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Intent i = new Intent(context, updateWidgeService.class);
		context.startService(i);
		Log.d("debuge", "onReceive");
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Log.d("debuge", "onUpdate");
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.d("debuge", "onEnable");
		Intent intent = new Intent(context, updateWidgeService.class);
		context.startService(intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.d("debuge", "onDisable");
		Intent intent = new Intent(context, updateWidgeService.class);
		context.stopService(intent);
	}
}
