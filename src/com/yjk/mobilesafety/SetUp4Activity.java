package com.yjk.mobilesafety;

import com.yjk.mobilesafety.receiver.MyAdmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetUp4Activity extends SetUpBaseActivity {
	
	private SharedPreferences sp;
	private CheckBox cb_protecting;
	private CheckBox cb_start_admin;
	private DevicePolicyManager dpm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup4);
		
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Button btn_finish = (Button) findViewById(R.id.btn_finish);
		Button btn_pre = (Button) findViewById(R.id.btn_pre);
		cb_protecting = (CheckBox) findViewById(R.id.cb_protecting);
		cb_start_admin = (CheckBox) findViewById(R.id.cb_start_admin);
		

		cb_start_admin.setChecked(isAdmin());
		if(isAdmin()){
			cb_start_admin.setText("您已经拥有管理员权限");
		}else{
			cb_start_admin.setText("您没有开启管理员权限");
		}
		
		cb_start_admin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					openAdmin();
					cb_start_admin.setText("您已经拥有管理员权限");
				}else{
					ComponentName   mDeviceAdminSample = new ComponentName(SetUp4Activity.this,MyAdmin.class);	
					dpm.removeActiveAdmin(mDeviceAdminSample);
					cb_start_admin.setText("您没有开启管理员权限");
				}
				
				Editor edit = sp.edit();
				edit.putBoolean("isadmin", isChecked);
				edit.commit();
			}
		});
	
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			cb_protecting.setText("手机防盗已经开启");
			cb_protecting.setChecked(true);
		}else{
			cb_protecting.setText("手机防盗已经关闭");
			cb_protecting.setChecked(false);
		}
		
		cb_protecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cb_protecting.setText("手机防盗已经开启");
				}else{
					cb_protecting.setText("手机防盗已经关闭");
				}
				
				Editor editor = sp.edit();
				editor.putBoolean("protecting",isChecked);
				editor.commit();
			}
		});
		
		btn_finish.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						next();
					}
				});
				
		btn_pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pre();
			}
		});
	}

	//跳转到下一页面
	protected void next() {
		// TODO Auto-generated method stub
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Toast.makeText(this, "您已成功设置手机防盗", 
				Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
		finish();
		
		
	}

	//跳转到上一页面
	protected void pre() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SetUp3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.come_out, R.anim.come_in);
	}
	
	/*
	 * 开启管理员权限
	 */
	public void openAdmin(){
		//创建一个Intent 
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				//我要激活谁
				ComponentName   mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
				
		        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		       //劝说用户开启管理员权限
		        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
		               "开启管理员权限才可以使用一键锁屏和删除数据功能");
		        
		        startActivity(intent);
	}
	
	/**
	 * 判断是否有管理员权限
	 */
	boolean isAdmin(){
		ComponentName   mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		return dpm.isAdminActive(mDeviceAdminSample);
	}
}
