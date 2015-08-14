package com.yjk.mobilesafety;

import com.yjk.mobilesafety.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SetUp2Activity extends SetUpBaseActivity {
	private SettingItemView siv_setup2_sim;
	/**
	 * ��ȡsim����Ϣ
	 */
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup2);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
		Button btn_next = (Button) findViewById(R.id.btn_next);
		Button btn_pre = (Button) findViewById(R.id.btn_pre);
		
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			//û�󶨹�
			siv_setup2_sim.setChecked(false);
		}else{
			//�󶨹�
			siv_setup2_sim.setChecked(true);
		}
		
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				if(siv_setup2_sim.isChecked()){
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", "");
				}else{
					siv_setup2_sim.setChecked(true);
					String sim = tm.getSimSerialNumber();
					
					editor.putString("sim", sim);
					editor.commit();
				}
			}
		});
		
		btn_next.setOnClickListener(new OnClickListener() {
			
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
	protected void next(){
		//ȡ���Ƿ��SIM��
		if(!siv_setup2_sim.isChecked()){
			Toast.makeText(this,
					"�빴ѡSIM����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		Intent intent = new Intent(this, SetUp3Activity.class);
		startActivity(intent);	
		finish();
		
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
	}
	
	//��ת����һҳ��
	protected void pre(){
		Intent intent = new Intent(this, SetUp1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.come_out, R.anim.come_in);
	}
}
