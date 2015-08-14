package com.yjk.mobilesafety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import com.yjk.mobilesafety.utils.StreamTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.media.MediaCodec.BufferInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * splashҳ�棬չʾLogo�������£����ݳ�ʼ��
 * @author yjk
 *
 */
public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String desprition; //�°汾������Ϣ
	private String apkUrl;  //�°汾apk���ص�ַ
	private TextView tv_update_info;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("�汾��" + getVersionName());
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		
		//�������ݿ�
		copyDB("address.db");
		copyDB("antivirus.db");
		
		boolean update = sp.getBoolean("update", false);
		if(update){
			//�������
			checkUpdate();
		}else{
			hander.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					enterHome();
				}
			}, 2000);
		}
		
		//��������
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.splash_root).setAnimation(aa);
		
		//�������ͼ��
		installShortCut();
	}

	/**
	 * ������ݷ�ʽ
	 */
	private void installShortCut() {
		// TODO Auto-generated method stub
		boolean shortcut = sp.getBoolean("shortcut", false);
		if(shortcut){
			return;
		}
		
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//��ݷ�ʽ ����3����Ҫ��Ϣ 1������ 2 ͼ�� 3��ʲô��
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ʿ");
		intent.putExtra(intent.EXTRA_SHORTCUT_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClass(SplashActivity.this, SplashActivity.class);
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		
		sendBroadcast(intent);
		
		Editor editor = sp.edit();
		editor.putBoolean("shortcut", true);
		editor.commit();
	}


	/**
	 * ��assert�����ݿ⿽����data/data/����/files/Ŀ¼��
	 * @param dbname
	 */
	private void copyDB(String dbname) {
		// TODO Auto-generated method stub
		//ֻҪ������һ�ξͲ��ÿ���
		try{
			File file = new File(getFilesDir(), dbname);
			//����Ѿ��������ˣ��Ͳ���Ҫ�ڿ���
			if(file.exists() && file.length() > 0){
				return;
			}
			InputStream is = getAssets().open(dbname);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = is.read(buffer)) != -1){
				fos.write(buffer, 0, len);
			}
			fos.flush();
			is.close();
			fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private Handler hander = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SHOW_UPDATE_DIALOG:
				Log.d(TAG, "��ʾ�����Ի���");
				showUpdateDialog();
				//enterHome();
				break;
			case ENTER_HOME:
				enterHome();
				break;
			case URL_ERROR:
				enterHome();
				Toast.makeText(SplashActivity.this, "URL����", Toast.LENGTH_LONG).show();
				break;
			case NETWORK_ERROR:
				enterHome();
				Toast.makeText(SplashActivity.this, "����������ʧ��", Toast.LENGTH_LONG).show();
				break;
			case JSON_ERROR:
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON��������", Toast.LENGTH_LONG).show();
				break;	
			default:
				break;
			
			}
		}
	};
	
	/**
	 * ���������Ի���
	 */
	private void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ����");
		//builder.setCancelable(false);  //����ȡ���Ի���
		//���ȡ���Ի�����ֱ�ӽ���������
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage(desprition);
		
		builder.setPositiveButton("��������", new OnClickListener() {
			
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//����APK�������滻��װ
				if(Environment.getExternalStorageState().
						equals(Environment.MEDIA_MOUNTED)){
					//sdcard����
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkUrl, Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/mobilesafe2.0apk", new AjaxCallBack<File>() {
								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									// TODO Auto-generated method stub
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), 
											"����ʧ��", Toast.LENGTH_LONG);
									super.onFailure(t, errorNo, strMsg);
								}
								
								@Override
								public void onLoading(long count, long current) {
									// TODO Auto-generated method stub
									super.onLoading(count, current);
									//��ǰ���ذٷֱ�
									tv_update_info.setVisibility(View.VISIBLE);
									int progress = (int) (current * 100/count);
									tv_update_info.setText("���ؽ���" +progress + "%");
								}
								
								@Override
								public void onSuccess(File t) {
									// TODO Auto-generated method stub
									super.onSuccess(t);
									installApk(t);
								}

								/*
								 * ��װapk
								 * @param t
								 */
								private void installApk(File t) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_VIEW);
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									intent.setDataAndType(Uri.fromFile(t), 
											"application/vnd.android.package-archive");
									
									startActivity(intent);
								}
							});
				}else{
					Toast.makeText(getApplicationContext(), 
							"û��Sd������װ������", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		
		builder.setNegativeButton("�´���˵", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome(); 
			}
			
		});
		
		builder.show();
	}
	
	/**
	 * ������ҳ��
	 */
	private void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	};
	
	
	/**
	 * ����Ƿ����°汾
	 */
	private void checkUpdate() {
		// TODO Auto-generated method stub
		new Thread(){
			public void run() {
				//URLhttp:// http://192.168.1.101:8080/updateinfo.html
				Message msg = Message.obtain();
				
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.servelurl));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					Log.d("debuge", "" + code);
					if(code == 200){
						String result = StreamTools.readFromStream(conn.getInputStream());
						Log.d("debuge", result);
						JSONObject obj = new JSONObject(result);
						//�õ��������İ汾��Ϣ
						String version = (String) obj.get("version");
						desprition = (String) obj.get("description");
						apkUrl = (String) obj.get("apkurl");
						
						//У���Ƿ����°汾
						if(version.equals(getVersionName())){
							//�汾һ�£�û���°汾��������ҳ��
							msg.what = ENTER_HOME;
							//Log.d(TAG, "ENTER_HOME");
						}else{
							//���°汾�����������Ի���
							msg.what = SHOW_UPDATE_DIALOG;
							//Log.d(TAG, "SHOW_UPDATE_DIALOG");
						}
							
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what= JSON_ERROR;
					e.printStackTrace();
				}finally{
					
					long endTime = System.currentTimeMillis();
					//���ǻ��˶���ʱ��
					long dTime = endTime - startTime;
					//˯2000����
					if(dTime < 2000){
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					hander.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * �õ�Ӧ�ó���İ汾����
	 */
	private String getVersionName(){
		//���������ֻ���APK
		PackageManager pm = getPackageManager();
		//�õ�ָ��apk�Ĺ����嵥�ļ�
		try {
			PackageInfo info = pm.getPackageInfo("com.yjk.mobilesafety", 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}