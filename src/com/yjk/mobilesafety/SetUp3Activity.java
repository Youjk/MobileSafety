package com.yjk.mobilesafety;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUp3Activity extends SetUpBaseActivity {
	
	private Button choose_contact;
	private EditText safe_phone_number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup3);
			
		safe_phone_number = (EditText) findViewById(R.id.safe_phone_number);
		choose_contact = (Button) findViewById(R.id.choose_contact);
		Button btn_next = (Button) findViewById(R.id.btn_next);
		Button btn_pre = (Button) findViewById(R.id.btn_pre);
		
		String safeNumber = sp.getString("safenumber", "");
		safe_phone_number.setText(safeNumber);
		
		choose_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				SetUp3Activity.this.startActivityForResult(intent, 1);
			}
		});
		
		btn_next.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						next();
					}
				});
				
		btn_pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pre();
			}
		});
	}
	
	//跳转到下一页面
		protected void next(){
			
			String number = safe_phone_number.getText().toString();
			//没设置安全号码
			if(TextUtils.isEmpty(number)){
				Toast.makeText(this, "请输入安全号码", Toast.LENGTH_LONG).show();
				return;
			}
			//保存安全号码
			Editor editor = sp.edit();
			editor.putString("safenumber", number);
			editor.commit();
			
			Intent intent = new Intent(this, SetUp4Activity.class);
			startActivity(intent);
			finish();
			
			overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
		}
		
		//跳转到上一页面
		protected void pre(){
			Intent intent = new Intent(this, SetUp2Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.come_out, R.anim.come_in);
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case 1:
				if(resultCode == RESULT_OK){
					Uri contactData = data.getData();
					Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
					cursor.moveToFirst();
					
					//获得DATA表中的名字
		            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));  
		            //条件为联系人ID
		            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));  
		            // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
		            Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
		                     null,   
		                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,   
		                     null,   
		                     null);  
		             while (phone.moveToNext()) {  
		                 String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
		                 safe_phone_number.setText(usernumber.replace("-", "").replace(" ", ""));  
		             }  
		             
		             phone.close();
		  
		        }  
				break;

			default:
				break;
			}
		}
}
