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
			cb_start_admin.setText("���Ѿ�ӵ�й���ԱȨ��");
		}else{
			cb_start_admin.setText("��û�п�������ԱȨ��");
		}
		
		cb_start_admin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					openAdmin();
					cb_start_admin.setText("���Ѿ�ӵ�й���ԱȨ��");
				}else{
					ComponentName   mDeviceAdminSample = new ComponentName(SetUp4Activity.this,MyAdmin.class);	
					dpm.removeActiveAdmin(mDeviceAdminSample);
					cb_start_admin.setText("��û�п�������ԱȨ��");
				}
				
				Editor edit = sp.edit();
				edit.putBoolean("isadmin", isChecked);
				edit.commit();
			}
		});
	
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			cb_protecting.setText("�ֻ������Ѿ�����");
			cb_protecting.setChecked(true);
		}else{
			cb_protecting.setText("�ֻ������Ѿ��ر�");
			cb_protecting.setChecked(false);
		}
		
		cb_protecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cb_protecting.setText("�ֻ������Ѿ�����");
				}else{
					cb_protecting.setText("�ֻ������Ѿ��ر�");
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

	//��ת����һҳ��
	protected void next() {
		// TODO Auto-generated method stub
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Toast.makeText(this, "���ѳɹ������ֻ�����", 
				Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
		finish();
		
		
	}

	//��ת����һҳ��
	protected void pre() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SetUp3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.come_out, R.anim.come_in);
	}
	
	/*
	 * ��������ԱȨ��
	 */
	public void openAdmin(){
		//����һ��Intent 
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				//��Ҫ����˭
				ComponentName   mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
				
		        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		       //Ȱ˵�û���������ԱȨ��
		        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
		               "��������ԱȨ�޲ſ���ʹ��һ��������ɾ�����ݹ���");
		        
		        startActivity(intent);
	}
	
	/**
	 * �ж��Ƿ��й���ԱȨ��
	 */
	boolean isAdmin(){
		ComponentName   mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		return dpm.isAdminActive(mDeviceAdminSample);
	}
}
