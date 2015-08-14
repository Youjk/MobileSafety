package com.yjk.mobilesafety.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

import com.yjk.mobilesafety.domain.AppInfo;

/**
 * ҵ�񷽷����ṩ�ֻ����氲װ������Ӧ�õĳ�����Ϣ
 * @author yjk
 *
 */
public class AppInfoProvider {
	
	/**
	 * ��ȡ���еİ�װ�ĳ���
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		//���еİ�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		for(PackageInfo info : packInfos){
			//�൱��apk�����嵥�ļ�;
			AppInfo appInfo = new AppInfo();
			
			String packname = info.packageName;
			Drawable icon = info.applicationInfo.loadIcon(pm);
			String name = info.applicationInfo.loadLabel(pm).toString();
		
			int flag = info.applicationInfo.flags;
			
			if((flag & ApplicationInfo.FLAG_SYSTEM) == 0){
				//����ϵͳӦ��
				appInfo.setUserApp(true);
			}else{
				//��ϵͳӦ��
				appInfo.setUserApp(false);
			}
			
			if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				//û�а�װ��sd��
				appInfo.setInRom(true);
			}else{
				//��װ��sd��
				appInfo.setInRom(false);
			}
			
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			
			int uid = info.applicationInfo.uid;    //����ϵͳ�����Ӧ��ϵͳһ���̶��ı�š�һ��һ��Ӧ�ó��򱻰�װ���ֻ���uid�Ͳ�����
			long tx = TrafficStats.getUidTxBytes(uid);
			long rx = TrafficStats.getUidRxBytes(uid);
			appInfo.setUid(uid);
			appInfo.setTx(tx);
			appInfo.setRx(rx);
			
			appInfos.add(appInfo);
		}
		
		return appInfos;
	}
}
