package com.yjk.mobilesafety;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class SetUp1Activity extends SetUpBaseActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup1);

		Button btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				next();
			}
		});
	}
	
	/*
	 * 跳转下一页面
	 */
	@Override
	protected void next(){
		Intent intent = new Intent(this, SetUp2Activity.class);
		startActivity(intent);
		finish();
		//要求在finish()或者startActivity(intent)后面执行
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
	}


	@Override
	protected void pre() {
		// TODO Auto-generated method stub
		
	}
	
}
