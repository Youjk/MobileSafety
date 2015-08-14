package com.yjk.mobilesafety.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * ���ŵĹ�����
 * @author yjk
 *
 */
public class SmsUtils {
	
	public interface BackUpCallBack{
		/**
		 * ��ʼ���ݵ�ʱ�����ñ������ֵ
		 * @param max  ���������ֵ
		 */
		public void beforeBackUp(int max);
		
		/**
		 * ���ݹ��������ý�����ֵ
		 * @param progress
		 */
		public void onSmsBackUp(int progress);
	}
	
	/**
	 * �����û��Ķ���
	 * @param context ������ pd ������
	 * @throws Exception 
	 */
	public static void backupSms(Context context, BackUpCallBack callback) throws Exception{
		
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
	
		//���û�����Ϣһ����������������һ����ʽд���ļ���
		
		XmlSerializer serializer = Xml.newSerializer(); // ��ȡxml�ļ���������
		
		
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}
			, null, null, null);
		//��ʼ�������ý��������ֵ
		callback.beforeBackUp(cursor.getCount());
		int process = 0;
		
		//��ʼ��������
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		serializer.attribute(null, "max", Integer.toString(cursor.getCount()));
	
		while(cursor.moveToNext()){
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			serializer.startTag(null, "sms");
			
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			
			process++;
			callback.onSmsBackUp(process);
		}
		
		serializer.endTag(null,  "smss");
		serializer.endDocument();
	}
	
	/**
	 * ��ԭ����
	 * @param context
	 * @param flag �Ƿ�����ԭ������
	 * @throws Exception 
	 */
	public static void restoreSms(Context context,boolean flag, reStoreSmsCallBack callback) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		//�ɵĵĶ���ɾ����
		if(flag){
			resolver.delete(uri, null, null);
		}
		
		//��ȡsd����xml�ļ�
		String xmlPath = Environment.getExternalStorageDirectory().getPath() + "/backup.xml";
		InputStream is = new FileInputStream(xmlPath);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		
		int eventType = parser.getEventType();
		int max = 0;
		int progress = 0;
		
		String body = "";
		String address = "";
		String type = "";
		String date = "";
		//��ȡmax
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			
			case XmlPullParser.START_TAG:
				if(parser.getName().equals("smss")){
					max = Integer.parseInt(parser.getAttributeValue(0));
					callback.beforeReStore(max);
				}
				else if(parser.getName().equals("sms")){

				}else if(parser.getName().equals("body")){

					body = parser.getText();
				}else if(parser.getName().equals("address")){

					address = parser.getText();
				}else if(parser.getName().equals("type")){

					type = parser.getText();
				}else if(parser.getName().equals("date")){
					date = parser.getText();
					progress++;
					
					ContentValues values = new ContentValues();
					values.put("body", body);
					values.put("address", address);
					values.put("type", type);
					values.put("date", date);
					resolver.insert(uri, values);
					callback.onSmsRestore(progress);
				}
			default:
				break;
			}
			
			eventType = parser.next();
		}
		
		//��ȡÿһ������
		
		//���뵽����Ӧ��
		
	}
	
	public interface reStoreSmsCallBack{
		/**
		 * ��ʼ��ԭ��ʱ�����ö�������
		 * @param max  ���������ֵ
		 */
		public void beforeReStore(int max);
		
		/**
		 * ��ԭ�������Ѿ���ԭ�˶�����
		 * @param progress
		 */
		public void onSmsRestore(int progress);
	}
}
