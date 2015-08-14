package com.yjk.mobilesafety.receiver;

import com.yjk.mobilesafety.R;
import com.yjk.mobilesafety.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	private DevicePolicyManager dpm;
	private SharedPreferences sp;
	boolean isAdmin;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Log.d("debuge", "1");
		//获取设备管理
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		isAdmin = sp.getBoolean("isadmin", false);
		
		dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String safenumber = sp.getString("safenumber", "");
		boolean protecting = sp.getBoolean("protecting", false);
		
		//没设置手机防盗直接退出
		if(!protecting){
			return;
		}
		
		//写接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		for(Object b : objs){
			//具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			//发送者
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
	
			//Toast.makeText(context, sender, Toast.LENGTH_LONG).show();
			if(sender.equals(safenumber)){
				if("#*location*#" .equals(body)){
					//得到手机的GPS
					Log.d("debuge", "gps");
					
					//开启获取位置服务
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					
					String lastlocation = sp.getString("lastlocation", "");
					Log.d("debuge", lastlocation);
					if(TextUtils.isEmpty(lastlocation)){
						//位置没得到
						SmsManager.getDefault().sendTextMessage(sender, null, 
								"正在获取位置", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, 
								lastlocation, null, null);
					}
					
					
					abortBroadcast();
				}else if("#*alarm*#" .equals(body)){
					//手机报警
					Log.d("debuge", "alarm");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					//删除数据库
					Log.d("debuge", "wipedata");
					deleteData();
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					//锁屏
					Log.d("debuge", "lockscreen");
					lockscreen();
					abortBroadcast();
				}
			}
		}
	}
	

	/**
	 * 一键锁屏
	 */
	
	public void lockscreen(){
		dpm.lockNow();
		if(!isAdmin){
			return;
		}	
		
		Log.d("debuge", "4");
		dpm.lockNow(); //锁屏
		//dpm.resetPassword("123", 0); //设置屏幕密码
		//dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE); //清除SD卡上的数据
		//dpm.wipeData(0);  //恢复出厂设置
	}
	
	public void deleteData(){
		if(!isAdmin){
			return;
		}
		dpm.wipeData(0);  //恢复出厂设置
		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);  //删除存储卡
	}

}
