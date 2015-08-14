package com.yjk.mobilesafety;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView tv_safenumber;
	private ImageView iv_protecting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		if(configed){
			//�����ֻ�����ҳ��
			setContentView(R.layout.activity_lost_find);
			tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
			iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
			
			//��ʾ��ȫ����
			String safeNumber = sp.getString("safenumber", "");
			tv_safenumber.setText(safeNumber);
			
			//��ʾ�Ƿ����ֻ�����ͼ��
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				iv_protecting.setImageResource(R.drawable.lock);
			}else{
				iv_protecting.setImageResource(R.drawable.unlock);
			}
			
		}else{
			//������ҳ��
			Intent intent = new Intent(this, SetUp1Activity.class);
			startActivity(intent);
			
			finish();
		}	
	}
	
	/*
	 * ���½����ֻ�����������ҳ��
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this, SetUp1Activity.class);
		startActivity(intent);
		finish();
	}
}
