package com.yjk.mobilesafety.service;

import java.lang.reflect.Array;

import com.yjk.mobilesafety.R;
import com.yjk.mobilesafety.utils.NumberAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MyAddressService extends Service {

	/**
	 * 电话服务
	 */
	
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	private outCallReceiver receiver;
	private SharedPreferences sp;
	
	private WindowManager wm;
	private View view;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class MyListenerPhone extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:  //来电响铃
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				if(TextUtils.isEmpty(address))
					address = "未知";
				
				myToast(address);
				break;
				
			case TelephonyManager.CALL_STATE_IDLE:
					if(view != null){
						wm.removeView(view);
						view = null;
					}
				break;

			default:
				break;
			}
		}
	}
	
	class outCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//这就是我们拿到的拨出去的电话号码
			String phone = getResultData();
			//查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
			if(TextUtils.isEmpty(address))
				address = "未知";
			
			myToast(address);
		}

	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = this.getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		//监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		//监听去电
		receiver = new outCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
	
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	
	private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	
	public void myToast(String address) {
		// TODO Auto-generated method stub
		view = View.inflate(this, R.layout.address_show, null);
		
		//增加点击事件
		view.setOnClickListener(new OnClickListener() {
			
			long[] mbits = new long[2];
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				System.arraycopy(mbits, 1, mbits, 0, mbits.length - 1);
				mbits[mbits.length - 1] = SystemClock.uptimeMillis();
				if(mbits[0] >= (SystemClock.uptimeMillis() - 500)){
					params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
					params.y = wm.getDefaultDisplay().getHeight() / 2 - view.getHeight() / 2;
					wm.updateViewLayout(v, params);
					
					Editor editor = sp.edit();
					editor.putFloat("x", params.x);
					editor.putFloat("y", params.y);
					editor.commit();
				}
			}
		});
		
		//给view对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			
			float startX;
			float startY;
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getRawX();
					startY = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					float newX = event.getRawX();
					float newY = event.getRawY();
					float dx = newX - startX;
					float dy = newY - startY;
					startX = event.getRawX();
					startY = event.getRawY();
					
					params.x += dx;
					params.y += dy;
					
					//考虑边界问题
					if(params.x < 0){
						params.x = 0;
					}
					if(params.y < 0){
						params.y = 0;
					}
					if(params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())){
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}	
					if(params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())){
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					
					wm.updateViewLayout(view, params);
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putFloat("x", params.x);
					editor.putFloat("y", params.y);
					editor.commit();
					break;
				default:
					break;
				}
				
				//事件已经消费，父类不响应触摸事件
				return false;
			}
		});
		
		//设置来电显示背景
		int[] bgid = {
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_gray,
				R.drawable.call_locate_green
		};
		view.setBackgroundResource(bgid[sp.getInt("which", 3)]);
	
		
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		textView.setText(address);
		
		float x = sp.getFloat("x", 100);
		float y = sp.getFloat("y", 100);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //与窗体左上角对齐
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //如果左边对齐就x就是离左边位置，
        params.x = (int) x;
        //如果是上边对齐y就是离上边距离
        params.y = (int) y;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //具有电话优先级的窗体类型，记得添加权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
		
		wm.addView(view, params);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
		
		//取消广播监听
		unregisterReceiver(receiver);
		receiver = null;
	}

}
