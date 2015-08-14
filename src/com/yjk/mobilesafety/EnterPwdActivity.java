package com.yjk.mobilesafety;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {
	
	private EditText et_password;
	private TextView tv_name;
	private ImageView iv_icon;
	private String packname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_enterpwd);
		
		et_password = (EditText) findViewById(R.id.et_password);
		
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");
	}
	
	public void unlock(View view){
		String password = et_password.getText().toString();
		if(TextUtils.isEmpty(password)){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
		}
		
		if("123".equals(password)){
			Intent intent = new Intent();
			intent.setAction("com.yjk.mobisafety.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
		}
	}
	
	public void Exit(View view){
		//打开桌面
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.addCategory(Intent.CATEGORY_HOME);
		startActivity(home);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//打开桌面
		Intent home = new Intent(Intent.ACTION_MAIN);  
		home.addCategory(Intent.CATEGORY_HOME);   
		startActivity(home); 
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	
}
