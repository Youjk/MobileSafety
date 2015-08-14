package com.yjk.mobilesafety;

import java.io.IOException;

import com.yjk.mobilesafety.utils.SmsUtils;
import com.yjk.mobilesafety.utils.SmsUtils.BackUpCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class AToolActivity extends Activity {
	
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_atool);
	}
	
	/**
	 * 号码查询TextView点击事件
	 */
	
	public void numberQuery(View view){
		Intent intent = new Intent(this, NumberQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 点击事件：短信的备份
	 * @param view
	 */
	public void smsBackup(View view){
		
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在备份");
		pd.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					SmsUtils.backupSms(AToolActivity.this, new BackUpCallBack() {
						
						@Override
						public void onSmsBackUp(int progress) {
							// TODO Auto-generated method stub
							pd.setProgress(progress);
						}
						
						@Override
						public void beforeBackUp(int max) {
							// TODO Auto-generated method stub
							pd.setMax(max);
							pd.setProgress(0);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolActivity.this, "备份短信成功", Toast.LENGTH_LONG).show();
						}
					});
				}catch (Exception e) {
					// TODO Auto-generated catch block
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolActivity.this, "备份短信失败", Toast.LENGTH_LONG).show();
						}
					});
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
			}
		}).start();
	}
	
	/**
	 * 点击事件：短信的还原
	 * @param view
	 */
	public void smsRestore(View view){
		
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在恢复");
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					SmsUtils.restoreSms(AToolActivity.this,
							false, new SmsUtils.reStoreSmsCallBack() {
								
								@Override
								public void onSmsRestore(int progress) {
									// TODO Auto-generated method stub
									pd.setProgress(progress);
								}
								
								@Override
								public void beforeReStore(int max) {
									// TODO Auto-generated method stub
									pd.setMax(max);
									pd.setProgress(0);
								}
							});
					
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolActivity.this, "短信恢复成功", Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolActivity.this, "短信恢复失败", Toast.LENGTH_LONG).show();
						}
					});
				}finally{
					pd.dismiss();
				}
			}
		}).run();
	}

}
