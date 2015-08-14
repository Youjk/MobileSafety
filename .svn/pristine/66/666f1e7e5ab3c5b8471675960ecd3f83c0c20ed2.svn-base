package com.yjk.mobilesafety;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 清理缓存界面
 * @author yjk
 *
 */
public class CleanCacheActivity extends Activity{
	
	private TextView tv_scan_status;
	private ProgressBar pb_clean;
	private ListView lv_cacheinfo;
	
	private PackageManager pm;
	private List<cacheInfo> cacheInfos;
	private CacheInfoAdapter adapter;
	private long totleCache;
	private int deletedIndex;
	private int progress = 0;
	private int max;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cleancache);
		
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		pb_clean = (ProgressBar) findViewById(R.id.pb_clean);
		lv_cacheinfo = (ListView) findViewById(R.id.lv_cacheinfo);
	
		pm = getPackageManager();
		cacheInfos = new ArrayList<CleanCacheActivity.cacheInfo>();
		adapter = new CacheInfoAdapter();
		lv_cacheinfo.setAdapter(adapter);
		
		totleCache = 0;
		scanCache();
	}
	
	/**
	 * 扫描手机里所有应用程序的缓存信息
	 */
	private void scanCache(){
		
		totleCache = 0;
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Method[]  methods = PackageManager.class.getMethods();
				Method method = null;
				for(Method m : methods){
					if("getPackageSizeInfo".equals(m.getName())){
						method = m;
						break;
					}
				}
				
				
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_clean.setMax(infos.size());
				progress = 0;
				max = infos.size();
				pb_clean.setProgress(0);
				
				for(PackageInfo info : infos){
					try {
						method.invoke(pm, info.packageName, new MyDataObserver());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private class MyDataObserver extends IPackageStatsObserver.Stub{

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
//			long cache = pStats.cacheSize;
//			long code = pStats.codeSize;
//			long data = pStats.dataSize;
			final cacheInfo cacheinfo = new cacheInfo();
			cacheinfo.cache = pStats.cacheSize;
			String packname = pStats.packageName;
			cacheinfo.packname = packname;
			
			try {
				ApplicationInfo appinfo = pm.getApplicationInfo(packname, 0);
				String name = appinfo.loadLabel(pm).toString();
				Drawable icon = appinfo.loadIcon(pm);
				
				cacheinfo.name = name;
				cacheinfo.icon = icon;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
 				e.printStackTrace();
 				cacheinfo.name = packname;
				cacheinfo.icon = getResources().getDrawable(R.drawable.ic_dafalut);
			}

			progress++;
			pb_clean.setProgress(progress);
			runOnUiThread(new Runnable() {
				public void run() {
					tv_scan_status.setText("正在扫描:" + cacheinfo.name);
					if(cacheinfo.cache > 0){
						totleCache += cacheinfo.cache;
						cacheInfos.add(cacheinfo);
						adapter.notifyDataSetChanged();
					}
					
					if(progress == max){
						tv_scan_status.setText("扫描完毕,总缓存" + 
								Formatter.formatFileSize(CleanCacheActivity.this, totleCache));
					}
				}
			});
		}	
	}
	
	
	class cacheInfo{
		long cache;
		String name;
		Drawable icon;
		String packname;
	}
	
	class CacheInfoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cacheInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;
			ViewHolder viewHolder;
			if(convertView != null){
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(CleanCacheActivity.this,
							R.layout.list_item_cacheinfo, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				viewHolder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				viewHolder.tv_app_cache = (TextView) view.findViewById(R.id.tv_app_cache);
				viewHolder.iv_clean_cache = (ImageView) view.findViewById(R.id.iv_clean_cache);
				
				view.setTag(viewHolder);
			}
			
			final cacheInfo info = cacheInfos.get(position);
			viewHolder.iv_app_icon.setImageDrawable(info.icon);
			viewHolder.tv_app_name.setText(info.name);
			viewHolder.tv_app_cache.setText("缓存" +
					Formatter.formatFileSize(CleanCacheActivity.this, info.cache));
			
			viewHolder.iv_clean_cache.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + cacheInfos.get(position).packname));
					startActivityForResult(intent, 0);
				}
			});
			
			return view;
		}
		
		class ViewHolder{
			ImageView iv_app_icon;
			TextView tv_app_name;
			TextView tv_app_cache;
			ImageView iv_clean_cache;
		}
		
}
	
	class MypackDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			System.out.println(succeeded);
			if(succeeded){
				cacheInfos.clear();
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
						tv_scan_status.setText("缓存清理完毕");
					}
				});
			}
		}
	}
	
	/**
	 * 一键清理缓存
	 * @param view
	 */
	public void clearAll(View view){
		Method localMethod;
		try {
			localMethod = pm.getClass().getMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);  
	        localMethod.invoke(pm,Integer.MAX_VALUE,new MypackDataObserver());  
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0){
			scanCache();
		}
	}
}
