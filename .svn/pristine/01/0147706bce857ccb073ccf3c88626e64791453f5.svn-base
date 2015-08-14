package com.yjk.mobilesafety.engine;

import java.util.ArrayList;
import java.util.List;



import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.util.Log;



import com.yjk.mobilesafety.R;
import com.yjk.mobilesafety.domain.TaskInfo;

/**
 * 提供手机里面的进程信息
 * @author yjk
 *
 */
public class TaskInfoProvider {
	public static List<TaskInfo> getTastInfo(Context context){
		 ActivityManager am =(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		 PackageManager pm = context.getPackageManager();
		 
		 List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		 List<TaskInfo> result = new ArrayList<TaskInfo>();
		
		 Log.d("debuge", "" + infos.size());
		 
		 for(RunningAppProcessInfo info : infos){
			 TaskInfo taskInfo = new TaskInfo();
			 String packname = info.processName;
			 MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			 long memsize = memoryInfo[0].getTotalPrivateDirty() * 1024;
			 taskInfo.setPackname(packname);
			 taskInfo.setMemsize(memsize);
			 taskInfo.setChecked(false);
			 try {
				 ApplicationInfo appinfo = pm.getApplicationInfo(packname, 0);
				 String name = appinfo.loadLabel(pm).toString();
				 Drawable icon = appinfo.loadIcon(pm);
				 taskInfo.setName(name);
				 taskInfo.setIcon(icon);
				 
				 if((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
					 //用户进程
					 taskInfo.setUserTask(true);
				 }else{
					 //系统进程
					 taskInfo.setUserTask(false);
				 }

				 result.add(taskInfo);
				 
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_dafalut));
				taskInfo.setName(packname);
				taskInfo.setUserTask(false);
				
				result.add(taskInfo);
			}
		 }
		 
		return result;
	}
}
