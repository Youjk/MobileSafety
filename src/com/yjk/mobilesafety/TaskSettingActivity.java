package com.yjk.mobilesafety;

import com.yjk.mobilesafety.service.AutoCleanService;
import com.yjk.mobilesafety.utils.ServiceUtil;

import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	
	private SharedPreferences sp;
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_setting);
		
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isShowSystem = sp.getBoolean("showsystem", false);
		cb_show_system.setChecked(isShowSystem);
		
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
			}
		});
		
		//锁屏广播只能动态注册
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
				if(isChecked){
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isServiceRunning = ServiceUtil.isServiceRunning(this,
				"com.yjk.mobilesafety.service.AutoCleanService");
		cb_auto_clean.setChecked(isServiceRunning);
	}
}
