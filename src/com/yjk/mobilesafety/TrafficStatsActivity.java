package com.yjk.mobilesafety;

import java.util.ArrayList;
import java.util.List;

import com.yjk.mobilesafety.domain.AppInfo;
import com.yjk.mobilesafety.engine.AppInfoProvider;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 流量监控界面
 * @author yjk
 *
 */
public class TrafficStatsActivity extends Activity{
	
	private TextView tv_shang_tra;
	private TextView tv_xia_tra;
	private ListView lv_traffic_item;
	private LinearLayout ll_loading;
	
	private List<AppInfo> appInfos;
	private List<AppInfo> userInfos;
	private List<AppInfo> systemInfos;
	private TrafficStatsAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_traffic_manager);
		
		tv_shang_tra = (TextView) findViewById(R.id.tv_shang_tra);
		tv_xia_tra = (TextView) findViewById(R.id.tv_xia_tra);
		long totleTx = TrafficStats.getMobileTxBytes();
		long totleRx = TrafficStats.getMobileRxBytes();
		
		tv_shang_tra.setText("总上传流量：" + 
				Formatter.formatFileSize(this, totleTx));
		tv_xia_tra.setText("总下载流量:" + 
				Formatter.formatFileSize(this, totleRx));
		
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_traffic_item = (ListView) findViewById(R.id.lv_traffic_item);
		
		fillData();
	}
	
	/**
	 * 获取appInfos数据，并做一些loading操作
	 */
	public void fillData(){
		ll_loading.setVisibility(View.VISIBLE);
		
		new Thread(){
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(TrafficStatsActivity.this);
				userInfos = new ArrayList<AppInfo>();
				systemInfos = new ArrayList<AppInfo>();
				
				for(AppInfo info : appInfos){
					if(info.isUserApp()){
						userInfos.add(info);
					}else{
						systemInfos.add(info);
					}
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(adapter == null){
							adapter = new TrafficStatsAdapter();
							lv_traffic_item.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();
	}

	
	class TrafficStatsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userInfos.size() + systemInfos.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			AppInfo appinfo;
			View view;
			ViewHold viewHolder;
			if(position < userInfos.size()){
				appinfo = userInfos.get(position);
			}else{
				appinfo = systemInfos.get(position - userInfos.size());
			}
		
			if(convertView != null){
				view = convertView;
				viewHolder = (ViewHold) convertView.getTag();
				Log.d("debuge", "复用");
			}else{
				view = View.inflate(TrafficStatsActivity.this,
						R.layout.list_traffic_item, null);
				viewHolder = new ViewHold();
				viewHolder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				viewHolder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				viewHolder.tv_download = (TextView) view.findViewById(R.id.tv_download);
				viewHolder.tv_upload = (TextView) view.findViewById(R.id.tv_upload);
				viewHolder.tv_totle_trafic = (TextView) view.findViewById(R.id.tv_totle_trafic);
				
				view.setTag(viewHolder);
			}
			
			viewHolder.tv_app_name.setText(appinfo.getName());
			viewHolder.iv_app_icon.setImageDrawable(appinfo.getIcon());
			viewHolder.tv_upload.setText(Formatter.formatFileSize(TrafficStatsActivity.this, appinfo.getRx()));
			viewHolder.tv_download.setText(Formatter.formatFileSize(TrafficStatsActivity.this, appinfo.getTx()));
			viewHolder.tv_totle_trafic.setText(Formatter.formatFileSize(TrafficStatsActivity.this, appinfo.getTx() + appinfo.getRx()));
			
			return view;
		}
		
		class ViewHold{
			TextView tv_app_name;
			ImageView iv_app_icon;
			TextView tv_upload;
			TextView tv_download;
			TextView tv_totle_trafic;
		}
	}
}

