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
 * 业务方法，提供手机里面安装的所有应用的程序信息
 * @author yjk
 *
 */
public class AppInfoProvider {
	
	/**
	 * 获取所有的安装的程序
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		//所有的安装在系统上的应用程序包信息
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		for(PackageInfo info : packInfos){
			//相当于apk包的清单文件;
			AppInfo appInfo = new AppInfo();
			
			String packname = info.packageName;
			Drawable icon = info.applicationInfo.loadIcon(pm);
			String name = info.applicationInfo.loadLabel(pm).toString();
		
			int flag = info.applicationInfo.flags;
			
			if((flag & ApplicationInfo.FLAG_SYSTEM) == 0){
				//不是系统应用
				appInfo.setUserApp(true);
			}else{
				//是系统应用
				appInfo.setUserApp(false);
			}
			
			if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				//没有安装在sd卡
				appInfo.setInRom(true);
			}else{
				//安装在sd卡
				appInfo.setInRom(false);
			}
			
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			
			int uid = info.applicationInfo.uid;    //操作系统分配给应用系统一个固定的编号。一旦一个应用程序被安装到手机，uid就不变了
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
