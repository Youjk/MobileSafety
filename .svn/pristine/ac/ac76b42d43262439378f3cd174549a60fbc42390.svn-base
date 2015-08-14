package com.yjk.mobilesafety;

import com.yjk.mobilesafety.service.CallSmsSafeService;
import com.yjk.mobilesafety.service.MyAddressService;
import com.yjk.mobilesafety.service.watchDogService;
import com.yjk.mobilesafety.ui.SettingClickView;
import com.yjk.mobilesafety.ui.SettingItemView;
import com.yjk.mobilesafety.utils.ServiceUtil;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

public class SettingActivity extends Activity {
	private SharedPreferences sp;
	private SettingItemView siv_update;
	private SettingItemView siv_show_address;
	private SettingItemView siv_callsms_safe;
	private SettingItemView siv_watchdog;
	
	//设置来电时背景
	private SettingClickView scv_changebg;
	private Intent ShowAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		//初始化设置是否自动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean("update", false);
		siv_update.setChecked(update);

		siv_update.setOnClickListener(new OnClickListener() {
	    
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//已经打开自动升级
				Editor editor = sp.edit();
				if(siv_update.isChecked()){
					siv_update.setChecked(!siv_update.isChecked());
					editor.putBoolean("update", false);
				}
				else{
					siv_update.setChecked(!siv_update.isChecked());
					editor.putBoolean("update", true);
				}
				
				editor.commit();
			}
		}); 
		
		//设置显示是否来电显示地点
		boolean isServiceRunning = ServiceUtil.isServiceRunning(this, 
				"com.yjk.mobilesafety.service.MyAddressService");
		
		ShowAddress = new Intent(this, MyAddressService.class);
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_show_address.setChecked(isServiceRunning);
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(siv_show_address.isChecked()){
					siv_show_address.setChecked(false);
					stopService(ShowAddress);
					
				}else{
					siv_show_address.setChecked(true);
					startService(ShowAddress);
				}
				
				//scv_changebg.setClickable(siv_show_address.isChecked());
			}
		});
		
		//设置来电归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		//scv_changebg.setClickable(siv_show_address.isChecked());
		scv_changebg.setTitle("归属地提示框风格");
		final String items[] = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
		scv_changebg.setDesc(items[sp.getInt("which", 3)]);
		
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//保存选择参数
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						
						scv_changebg.setDesc(items[which]);
						
						//取消对话框
						dialog.dismiss();
					}
				});
				
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		
		//设置是否开启黑名单拦截
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		boolean isLanjieServiceRunning = ServiceUtil.isServiceRunning(this, 
				"com.yjk.mobilesafety.service.CallSmsSafeService");
		
		siv_callsms_safe.setChecked(isLanjieServiceRunning);
		final Intent intentCallsmsService = new Intent(SettingActivity.this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(siv_callsms_safe.isChecked()){
					stopService(intentCallsmsService);
					siv_callsms_safe.setChecked(false);
				}else{
					startService(intentCallsmsService);
					siv_callsms_safe.setChecked(true);
				}
			}
		});
		
		//设置是否开启看门狗服务
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		boolean isWathdogOpen = ServiceUtil.isServiceRunning(SettingActivity.this,
				"com.yjk.mobilesafety.service.watchDogService");
		siv_watchdog.setChecked(isWathdogOpen);
		
		siv_watchdog.setOnClickListener(new OnClickListener() {
			
			Editor editor = sp.edit();
			Intent intent = new Intent(SettingActivity.this,
					watchDogService.class);
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(siv_watchdog.isChecked()){
					siv_watchdog.setChecked(false);
					stopService(intent);
				}else{
					siv_watchdog.setChecked(true);
					startService(intent);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isServiceRunning = ServiceUtil.isServiceRunning(this, 
				"com.yjk.mobilesafety.service.MyAddressService");
		
		siv_show_address.setChecked(isServiceRunning);
		
		boolean isLanjieServiceRunning = ServiceUtil.isServiceRunning(this, 
				"com.yjk.mobilesafety.service.CallSmsSafeService");
		
		siv_callsms_safe.setChecked(isLanjieServiceRunning);
		
		boolean isWathdogOpen = ServiceUtil.isServiceRunning(SettingActivity.this,
				"com.yjk.mobilesafety.service.watchDogService");
		siv_watchdog.setChecked(isWathdogOpen);
	}
}
