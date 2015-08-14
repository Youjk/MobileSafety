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

	//�õ�λ�÷���
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
		
		//��λ���ṩ����������
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); //���þ���
		String provider = lm.getBestProvider(criteria, true);
		
		//provider��ÿ����ã��������پ��룬�ͻ���ø���
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
		 * λ�øı�ص��÷���
		 */
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a:" + location.getAccuracy() + "\n";
			//��׼׼��GPS����ת���ɻ�������
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
			
			//�����Ÿ���ȫ����
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude 
					+ latitude + accuracy);
			editor.commit();
		}

		/**
		 * ״̬�����ı䣬�رպͿ���GPS�ص�
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * ĳһ��λ���ṩ�߿�ʹ��
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * ĳһλ���ṩ�߲���ʹ��
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}	
	}
}