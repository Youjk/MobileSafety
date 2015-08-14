package com.yjk.mobilesafety;

import java.util.ArrayList;
import java.util.List;

import com.yjk.mobilesafety.db.dao.AppLockDao;
import com.yjk.mobilesafety.domain.AppInfo;
import com.yjk.mobilesafety.engine.AppInfoProvider;
import com.yjk.mobilesafety.utils.DensityUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	
	private TextView tv_neicun_left;
	private TextView tv_sdcard_left;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private TextView tv_status;
	
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	
	/**
	 * ������������
	 */
	private PopupWindow pw;
	
	//����app
	private List<AppInfo> appInfos = null;
	
	//�û�app
	private List<AppInfo> userAppInfos = null;
	//ϵͳ����
	private List<AppInfo> systemApp = null;
	
	private AppManagerAdapter adapter;
	private AppInfo appinfo;   //���������Ŀ
	
	private AppLockDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_app_manager);
		
		dao = new AppLockDao(this);
		
		tv_neicun_left = (TextView) findViewById(R.id.tv_neicun_left);
		tv_sdcard_left = (TextView) findViewById(R.id.tv_sdcard_left);
		tv_status = (TextView) findViewById(R.id.tv_status);
		
		
		
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		
		tv_sdcard_left.setText("sd������:" + Formatter.formatFileSize(this, sdsize));
		tv_neicun_left.setText("�ڴ����:" + Formatter.formatFileSize(this, romsize));
	
		lv_app_manager = (ListView) findViewById(R.id.lv_software_item);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		
		
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(position == 0){
					return true;
				}else if(position == userAppInfos.size() + 1){
					return true;
				}else if(position <= userAppInfos.size()){
					int newposition = position - 1;
					appinfo = userAppInfos.get(newposition);
				}else{
					int newposition = position  -userAppInfos.size() - 2;
					appinfo = systemApp.get(newposition);
				}
				
				ImageView iv_app_lock = (ImageView) view.findViewById(R.id.iv_app_lock);
				if(dao.find(appinfo.getPackname())){
					//���������ɾ����Ӧ���ݿ�
					dao.delete(appinfo.getPackname());
					iv_app_lock.setImageResource(R.drawable.unlock);
				}else{
					//���������Ӷ�Ӧ���ݿ�
					dao.add(appinfo.getPackname());
					iv_app_lock.setImageResource(R.drawable.lock);
				}
				return true;
			}
		});
		
		/**
		 * ����listview�ĵ���¼�
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position == 0){
					return;
				}else if(position == userAppInfos.size() + 1){
					return;
				}else if(position <= userAppInfos.size()){
					int newposition = position - 1;
					appinfo = userAppInfos.get(newposition);
				}else{
					int newposition = position  -userAppInfos.size() - 2;
					appinfo = systemApp.get(newposition);
				}
				
				//�ɵĵ�������ر�
				dissmissOldPopupWindow();
				
				//��������
				View contentView = View.inflate(AppManagerActivity.this,
						R.layout.popup_item, null);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				
				pw = new PopupWindow(contentView, -2, -2);
				//����Ч���Ĳ��ű���Ҫ�����б�����ɫ��
				pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] locations = new int[2];
				view.getLocationInWindow(locations);
				int dp = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dp);
				int py = DensityUtil.dip2px(getApplicationContext(), 10);
				pw.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, locations[0] + px, locations[1] - py);
			
				
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(100);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(100);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);
			}
		});
		
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			//������ʱ����õķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
				//һ����������pupwindow
				dissmissOldPopupWindow();
				//firstVisibleItem��list�����е�λ��
				if(userAppInfos != null && systemApp != null){
					if(firstVisibleItem <= userAppInfos.size()){
						tv_status.setText("�û�����:" + userAppInfos.size() + "��");
					}else{
						tv_status.setText("ϵͳ����:" + systemApp.size() + "��");
					}
				}
			}
		});
		
		getData();
	}
	
	private void getData() {
		// TODO Auto-generated method stub
		ll_loading.setVisibility(View.VISIBLE);
		tv_status.setVisibility(View.INVISIBLE);
		new Thread(){
			public void run() {
				
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				
				
				userAppInfos = new ArrayList<AppInfo>();
				systemApp = new ArrayList<AppInfo>();
				
				for(AppInfo info : appInfos){
					if(info.isUserApp()){
						userAppInfos.add(info);
					}else{
						systemApp.add(info);
					}
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						if(adapter == null){
							adapter = new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);
						}
						else{
							adapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
						tv_status.setText("�û�����:" + userAppInfos.size() + "��");
						tv_status.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	protected void dissmissOldPopupWindow() {
		// TODO Auto-generated method stub
		if(pw != null && pw.isShowing()){
			pw.dismiss();
			pw = null;
		}
	}

	private class AppManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfos.size() + 1 + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;
			ViewHolder viewHolder;
			AppInfo appinfo;
			
			if(position == 0){//
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(20);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:" + userAppInfos.size() + "��");
				return tv;
			}
			else if(position == userAppInfos.size() + 1){
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemApp.size() + "��");
				tv.setTextSize(20);
				return tv;
			}else if(position <= userAppInfos.size()){
				appinfo = userAppInfos.get(position - 1);
			}else{
				appinfo = systemApp.get(position - userAppInfos.size() - 2);
			}
			
			if(convertView != null && !(convertView instanceof TextView)){
				view = convertView;
				viewHolder = (ViewHolder) convertView.getTag();
			}else{
				viewHolder = new ViewHolder();
				view =View.inflate(AppManagerActivity.this,
						R.layout.list_item_appinfo, null);
				viewHolder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				viewHolder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				viewHolder.tv_app_location = (TextView) view.findViewById(R.id.tv_app_location);
				viewHolder.tv_app_type = (TextView) view.findViewById(R.id.tv_app_type);
				viewHolder.iv_app_lock = (ImageView) view.findViewById(R.id.iv_app_lock);
				
				view.setTag(viewHolder);
			}
			
		
			viewHolder.tv_app_name.setText(appinfo.getName());
			viewHolder.iv_app_icon.setImageDrawable(appinfo.getIcon());
			if(appinfo.isInRom()){
				viewHolder.tv_app_location.setText("�ֻ��ڴ�");
			}else{
				viewHolder.tv_app_location.setText("�ⲿ�洢");
			}
			
			if(appinfo.isUserApp()){
				viewHolder.tv_app_type.setText("�û�Ӧ��");
			}else{
				viewHolder.tv_app_type.setText("ϵͳӦ��");
			}
			
			if(dao.find(appinfo.getPackname())){
				viewHolder.iv_app_lock.setImageResource(R.drawable.lock);
			}else{
				viewHolder.iv_app_lock.setImageResource(R.drawable.unlock);
			}
			
			
			return view;
		}
		
		class ViewHolder{
			ImageView iv_app_icon;
			TextView tv_app_name;
			TextView tv_app_location;
			TextView tv_app_type;
			ImageView iv_app_lock;
		}
		
	}
	
	/**
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿؼ�
	 * @param path 
	 * @return
	 */
	private long getAvailSpace(String path){
		StatFs statf = new StatFs(path);
		//statf.getBlockCount(); //��ȡ�����ĸ���
		long size = statf.getBlockSize(); //��ȡÿ�������Ĵ�С
		long count = statf.getAvailableBlocks(); //��ȡ���ÿռ������ĸ���
		
		return size*count;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dissmissOldPopupWindow();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dissmissOldPopupWindow();
		
		switch (v.getId()) {
		case R.id.ll_uninstall:
			if(appinfo.isUserApp()){
				uninstallApplication();
			}else{
				Toast.makeText(this, "ϵͳӦ����RootȨ���²���ж��", 
						Toast.LENGTH_LONG).show();
			}		
			break;
		case R.id.ll_start:
			startApplication();
			break;
			
		case R.id.ll_share:
			
			break;
		default:
			break;
		}
	}
	
	private void uninstallApplication() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appinfo.getPackname()));
		
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		getData();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void startApplication(){
		PackageManager pm = getPackageManager();
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
		//��ȡ���еĿ���������activity
//		List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
	
		Intent intent = pm.getLaunchIntentForPackage(appinfo.getPackname());
		if(intent != null){
			startActivity(intent);
		}else{
			Toast.makeText(this, "����������Ӧ��", Toast.LENGTH_LONG).show();
		}
	}
}


