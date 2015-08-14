package com.yjk.mobilesafety.ui;

import com.yjk.mobilesafety.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private CheckBox cb_status;
	private TextView tv_title;
	private TextView tv_desc;
	
	private String desc_on;
	private String desc_off;
	private void initView(Context context) {
		// TODO Auto-generated method stub
		//把一个布局文件View并且加载在SettingItemView
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
		
		tv_desc.setText(desc_off);
	}
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.yjk.mobilesafety",
				"tv_title");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.yjk.mobilesafety",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.yjk.mobilesafety",
				"desc_off");
		
		initView(context);
		tv_title.setText(title);	
	}

	public SettingItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	/*
	 * 校验组合控件是否选中
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/*
	 * 设置组合控件的状态
	 */
	
	public void setChecked(boolean checked){
		if(checked){
			tv_desc.setText(desc_on);
		}else{
			tv_desc.setText(desc_off);
		}
		cb_status.setChecked(checked);
	}
	
	/*
	 * 组合控件的描述信息
	 */
	
	public void setDesc(String text){
		tv_desc.setText(text);
	}
}
