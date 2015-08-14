package com.yjk.mobilesafety.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.yjk.mobilesafety.db.dao.BlackNumberDao;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {

	private TelephonyManager tm;
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private Mylistener listener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//��鷢�����Ƿ��Ǻ��������룬���ö������ػ���ȫ������
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				
				if("2".equals(result) || "3".equals(result)){
					//���ض���
					abortBroadcast();
				}
			}
			
			
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("debuge", "service kai qi");
		
		//��������
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, intentFilter);
		
		//�绰����
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new Mylistener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

	
	class  Mylistener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String result = dao.findMode(incomingNumber);
				
				//Log.d("debuge", result);
				Log.d("debuge", incomingNumber);
				if("1".equals(result) || "3".equals(result)){
					Log.d("debuge", "�Ҷϵ绰");
					
					//ɾ�����м�¼
					//����һ��Ӧ�ó�����ϵ��Ӧ�õ�˽������
					//�۲���м�¼���ݿ��������ݿ�ı仯
					Uri  uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, 
							new CallLogObserver(incomingNumber, new Handler()));
					
					endCall();  //������һ�����̵ķ��񣬲���һ���̣߳����ܵ绰��û���ü��Ҷϣ����Ѿ�ִ����ɾ������
				}
				
				break;

			default:
				break;
			}
		}
	}

	
	private class CallLogObserver extends ContentObserver{
		
		private String incomingNumber;
		
		public CallLogObserver(String incommingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incommingNumber;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
			super.onChange(selfChange);
		}
	}
	
	public void endCall() {
		// TODO Auto-generated method stub
		//IBinder binder = ServiceManger.
		try {
			//����ServiceManger���ֽ���
			Class clazz = CallSmsSafeService.class.getClassLoader().
					loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(binder).endCall();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������ṩ��ɾ�����м�¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		// TODO Auto-generated method stub
		ContentResolver resolver = getContentResolver();
		Uri  uri = Uri.parse("content://call_log/calls");
		
		resolver.delete(uri, "number = ?", new String[]{incomingNumber});
	}
}
