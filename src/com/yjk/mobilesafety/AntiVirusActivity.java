package com.yjk.mobilesafety;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.yjk.mobilesafety.db.dao.AntivirsuDao;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity{
	
	protected static final int SCANING = 0;
	protected static final int SCANFINISH = 1;
	private ImageView iv_scan;
	private ProgressBar pb_atvirtu;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private Button btn_scan;
	
	boolean isScanning;
	private RotateAnimation ra;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SCANING:
				ScanInfo scaninfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("����ɨ��:" + scaninfo.name);
				
				TextView tv = new TextView(AntiVirusActivity.this);
				if(scaninfo.isvirtus){
					tv.setText("���ֲ���:" + scaninfo.name);
					tv.setTextColor(Color.RED);
				}else{
					tv.setText("ɨ�谲ȫ:" + scaninfo.name);
					tv.setTextColor(Color.BLACK);
				}
				
				ll_container.addView(tv, 0);
				break;
				
			case SCANFINISH:
				iv_scan.clearAnimation();
				tv_scan_status.setText("ɨ�����");
				isScanning = false;
				btn_scan.setText("ȫ��ɨ��");
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_antivirtus);
		
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		pb_atvirtu = (ProgressBar) findViewById(R.id.pb_atvirtu);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		btn_scan = (Button) findViewById(R.id.btn_scan);
		
		isScanning = false;
		ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
	}
	
	/**
	 * ɨ�財������¼�
	 * @param view
	 */
	public void startScanVirtus(View view){
		if(!isScanning){
			isScanning = true;
			btn_scan.setText("ֹͣɨ��");
			iv_scan.startAnimation(ra);
			scanVirus();
		}else{
			isScanning = false;
			btn_scan.setText("ȫ��ɨ��");
			iv_scan.clearAnimation();
			ll_container.removeAllViews();
		}
	
	}
	
	/**
	 * ɨ�財��
	 */
	private void scanVirus(){
		pm = getPackageManager();
		tv_scan_status.setText("���ڳ�ʼ��ɱ������...");
		
		new Thread(){
			public void run() {
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_atvirtu.setMax(infos.size());
				int progress = 0;
				for(PackageInfo info : infos){
					
					//ֹͣɨ��ֱ�ӷ���
					if(!isScanning){
						return;
					}
					
					String dataDir = info.applicationInfo.dataDir;
					String sourceDir = info.applicationInfo.sourceDir;
					String md5 = getFileMd5(sourceDir);
					
					ScanInfo scaninfo = new ScanInfo();
					scaninfo.packname = info.packageName;
					scaninfo.name = info.applicationInfo.loadLabel(pm).toString();
					if(AntivirsuDao.isVirus(md5)){
						//���ֲ���
						scaninfo.isvirtus = true;
					}else{
						//ɨ�谲ȫ
						scaninfo.isvirtus = false;
					}
					
					progress++;
					pb_atvirtu.setProgress(progress);
					Message msg = new Message();
					msg.what = SCANING;
					msg.obj = scaninfo;
					handler.sendMessage(msg);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println(dataDir); 
					System.out.println(sourceDir);
					System.out.println(md5);
					System.out.println("......"); 
				}
				
				Message msg =new Message();
				msg.what = SCANFINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	/**
	 * ��ȡ�ļ���md5
	 * @param �ļ�ȫ·��
	 * @return
	 */
	private String getFileMd5(String path){
		
		File file = new File(path);
		StringBuffer sb =new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = fis.read(buffer)) != -1){
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			for(byte b : result){
				int number = b & 0xff; //����
				String str = Integer.toHexString(number);
				if(str.length() == 1){
					sb.append("0");
				}
				
				sb.append(str);
			}
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		return sb.toString();
	}
	
	/**
	 * ɨ����Ϣ���ڲ���
	 * @author yjk
	 *
	 */
	class ScanInfo{
		String packname;
		String name;
		boolean isvirtus;
	}
}
