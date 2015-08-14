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
			//检查发件人是否是黑名单号码，设置短信拦截或者全部拦截
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				
				if("2".equals(result) || "3".equals(result)){
					//拦截短信
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
		
		//短信拦截
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, intentFilter);
		
		//电话拦截
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
					Log.d("debuge", "挂断电话");
					
					//删除呼叫记录
					//另外一个应用程序联系人应用的私有数据
					//观察呼叫记录数据库内容数据库的变化
					Uri  uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, 
							new CallLogObserver(incomingNumber, new Handler()));
					
					endCall();  //调用另一个进程的服务，不在一个线程，可能电话还没来得及挂断，就已经执行完删除操作
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
			//加载ServiceManger的字节码
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
	 * 利用内容提供者删除呼叫记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		// TODO Auto-generated method stub
		ContentResolver resolver = getContentResolver();
		Uri  uri = Uri.parse("content://call_log/calls");
		
		resolver.delete(uri, "number = ?", new String[]{incomingNumber});
	}
}
