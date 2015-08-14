package com.yjk.mobilesafety;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class SetUpBaseActivity extends Activity{
	protected GestureDetector dector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		dector = new GestureDetector(this, 
				new GestureDetector.SimpleOnGestureListener(){
			/*
			 * ����ָ�����滬����ʱ�򣬵���
			 * 
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// ����б���������
				if(Math.abs(e2.getRawY() - e1.getRawY()) > 100)
					return true;
				//������X�Ử�����������Σ�����㶵��С�Ļ���
				if(Math.abs(velocityX) < 200)
					return true;
				if(e2.getRawX() - e1.getRawX() > 200){
					//��һҳ
					pre();
				}
				else if(e1.getRawX() - e2.getRawX() > 200){
					//��һҳ
					next();
					
				}
				return true;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		dector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	abstract protected void next();
	abstract protected void pre();
}
