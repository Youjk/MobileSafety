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
		//��ȡ�豸����
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		isAdmin = sp.getBoolean("isadmin", false);
		
		dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String safenumber = sp.getString("safenumber", "");
		boolean protecting = sp.getBoolean("protecting", false);
		
		//û�����ֻ�����ֱ���˳�
		if(!protecting){
			return;
		}
		
		//д���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		for(Object b : objs){
			//�����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			//������
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
	
			//Toast.makeText(context, sender, Toast.LENGTH_LONG).show();
			if(sender.equals(safenumber)){
				if("#*location*#" .equals(body)){
					//�õ��ֻ���GPS
					Log.d("debuge", "gps");
					
					//������ȡλ�÷���
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					
					String lastlocation = sp.getString("lastlocation", "");
					Log.d("debuge", lastlocation);
					if(TextUtils.isEmpty(lastlocation)){
						//λ��û�õ�
						SmsManager.getDefault().sendTextMessage(sender, null, 
								"���ڻ�ȡλ��", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, 
								lastlocation, null, null);
					}
					
					
					abortBroadcast();
				}else if("#*alarm*#" .equals(body)){
					//�ֻ�����
					Log.d("debuge", "alarm");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					//ɾ�����ݿ�
					Log.d("debuge", "wipedata");
					deleteData();
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					//����
					Log.d("debuge", "lockscreen");
					lockscreen();
					abortBroadcast();
				}
			}
		}
	}
	

	/**
	 * һ������
	 */
	
	public void lockscreen(){
		dpm.lockNow();
		if(!isAdmin){
			return;
		}	
		
		Log.d("debuge", "4");
		dpm.lockNow(); //����
		//dpm.resetPassword("123", 0); //������Ļ����
		//dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE); //���SD���ϵ�����
		//dpm.wipeData(0);  //�ָ���������
	}
	
	public void deleteData(){
		if(!isAdmin){
			return;
		}
		dpm.wipeData(0);  //�ָ���������
		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);  //ɾ���洢��
	}

}
