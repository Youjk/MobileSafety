package com.yjk.mobilesafety;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yjk.mobilesafety.utils.AvaliableUtils;
import com.yjk.mobilesafety.utils.NumberAddressQueryUtils;
import com.yjk.mobilesafety.utils.StreamTools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberQueryActivity extends Activity {
	
	protected static final int NUMBER_OK = 1;
	protected static final int NUMBER_WRONG = 0;
	private EditText phoneNumber;
	private TextView tv_showResult;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_number_query);
		
		phoneNumber = (EditText) findViewById(R.id.query_number);
		tv_showResult = (TextView) findViewById(R.id.tv_showResult);
		
		phoneNumber.addTextChangedListener(new TextWatcher() {
			
			/**
			 * 当文本发生变化的时候回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() > 3){
					
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case NUMBER_OK:
				Bundle data = msg.getData();
				if(TextUtils.isEmpty(data.getString("numberMessage"))){
					tv_showResult.setText("未知");
				}
				else
					tv_showResult.setText(data.getString("numberMessage"));
				break;
			case NUMBER_WRONG:
				tv_showResult.setText("未知");
				break;
			}
		};
	};
	
	public void queryNumber(View view){
		final String phone = phoneNumber.getText().toString();
		
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "号码为空", Toast.LENGTH_LONG).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			phoneNumber.startAnimation(shake);
		}else{
			
			if(phone.length() < 7){
				tv_showResult.setText("请输入7-11为手机号码(段)");
				return;
			}
			
			//网络查询或者本地查询
			//首先本地查询，查询失败则用网络查询
			String address = NumberAddressQueryUtils.queryNumber(phone);
			if(!TextUtils.isEmpty(address)){
				tv_showResult.setText(address);
				return;
			}
			//若果网络可用，则使用网络查询
			if(AvaliableUtils.isNetworkAvailable(NumberQueryActivity.this)){
				NetQueryNumber(phone);
			}else{
				//本地数据库查询
			}
		}
	}
	
	/*
	 * 网络查询手机归属地
	 */
	private void NetQueryNumber(final String phone){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub{
					Message msg = new Message();
					try {
						//使用百度的接口返回json数据
						URL url = new URL(getString(R.string.queryNumberUrl)+"?num=" + phone);
						//联网
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setConnectTimeout(4000);
						conn.setRequestProperty("apikey",  "fe13f2a5a026033526959d1439e8b8b4");
						int code = conn.getResponseCode();
						Log.d("debuge", "" + code);
						if(code == 200){
							String result = StreamTools.readFromStream(conn.getInputStream());
							Log.d("debuge", result);
							JSONObject obj = new JSONObject(result);
							String result1 = obj.getString("showapi_res_body");
							JSONObject obj1 = new JSONObject(result1);
							String province = obj1.getString("prov");
							String city = obj1.getString("city");
							String name = obj1.getString("name");
							Log.d("debuge", province + city + name);
							msg.what = NUMBER_OK;
							Bundle data = new Bundle();
							data.putString("numberMessage", 
									province + city + "\n" + name);
							msg.setData(data);
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = NUMBER_WRONG;
					}finally{
						handler.sendMessage(msg);
					}
				}
			}
		).start();	
	}
}
