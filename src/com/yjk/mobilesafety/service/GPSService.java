package com.yjk.mobilesafety.service;


import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.widget.TextView;
import com.yjk.mobilesafety.utils.*;


public class GPSService extends Service {

	//用到位置服务
	private LocationManager lm;
	private MyLocationListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyLocationListener();
		
		//给位置提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); //设置精度
		String provider = lm.getBestProvider(criteria, true);
		
		//provider，每隔多久，超过多少距离，就会调用更新
		lm.requestLocationUpdates(provider, 60000, 100, listener);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}
	
	class MyLocationListener implements LocationListener{

		/**
		 * 位置改变回调该方法
		 */
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a:" + location.getAccuracy() + "\n";
			//标准准的GPS坐标转换成火星坐标
			InputStream is;
			try{
				is = getAssets().open("axisoffset.dat");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				PointDouble double1 = offset.s2c(new PointDouble(location.getLongitude(), 
						location.getLatitude()));
				
				longitude = "j:" + offset.X + "\n";
				latitude = "w:" + offset.Y + "\n";
			}catch(IOException e){
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			//发短信给安全号码
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude 
					+ latitude + accuracy);
			editor.commit();
		}

		/**
		 * 状态发生改变，关闭和开启GPS回调
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * 某一个位置提供者可使用
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * 某一位置提供者不可使用
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}	
	}
}
