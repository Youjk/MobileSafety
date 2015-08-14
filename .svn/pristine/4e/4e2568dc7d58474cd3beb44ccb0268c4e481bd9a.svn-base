package com.yjk.mobilesafety.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class BootCompeleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		
		//读取是否已经开启了防盗保护，如果没有则什么都不做
		boolean protecting = sp.getBoolean("protecting", false);
		if(!protecting){
			return;
		}
		
		//读取之前保存的sim信息
		String saveedSim = sp.getString("sim", "");
		
		//读取当前sim卡信息
		TelephonyManager tm = (TelephonyManager) context.
				getSystemService(context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber();
		
		if(TextUtils.isEmpty(currentSim) || TextUtils.isEmpty(saveedSim)){
			return;
		}
		
		if(saveedSim.equals(currentSim)){
			//sim卡没有变更
		}else{
			//sim卡已经变更 发一个短信给安全号码
			Toast.makeText(context, "SIM卡已经变更", 
					Toast.LENGTH_LONG).show();
			
			String safenumber = sp.getString("safenumber", "");
			@SuppressWarnings("deprecation")
			SmsManager smsManger = SmsManager.getDefault();
			smsManger.sendTextMessage(safenumber, null, "SIM changed", null, null);
		}
	}

}
