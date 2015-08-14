package com.yjk.mobilesafety;

import java.util.ArrayList;
import java.util.List;

import com.yjk.mobilesafety.TaskManagerActivity.MyTaskManagerAdapter.ViewHolder;
import com.yjk.mobilesafety.domain.AppInfo;
import com.yjk.mobilesafety.domain.TaskInfo;
import com.yjk.mobilesafety.engine.TaskInfoProvider;
import com.yjk.mobilesafety.utils.SystemInfoUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity{
	
	private TextView tv_process_count;
	private TextView tv_memory_info;
	private ListView lv_task_item;
	private LinearLayout ll_loading;
	private TextView tv_status;
	
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private List<TaskInfo> taskInfos;
	private MyTaskManagerAdapter adapter;
	
	long avaiMem;
	long totalMem;
	int processCount;
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_task_manager);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		tv_status = (TextView) findViewById(R.id.tv_status);
		lv_task_item = (ListView) findViewById(R.id.lv_task_item);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		
		avaiMem = SystemInfoUtils.getAvaioMem(this);
		totalMem = SystemInfoUtils.getTotalMem(this);
		processCount = SystemInfoUtils.getRunningProcessCount(this);
		
		fillData();
		setTitle();

		lv_task_item.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				// TODO Auto-generated method stub
				if(position == 0){//
					return;
				}
				else if(position == userTaskInfos.size() + 1){
					return;
				}else if(position <= userTaskInfos.size()){
					taskInfo = userTaskInfos.get(position - 1);
				}else{
					taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
				}
				
				if(taskInfo.getPackname().equals("com.yjk.mobilesafety")){
					return;
				}
				
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				if(taskInfo.isChecked()){
					taskInfo.setChecked(false);
					viewHolder.cb_task.setChecked(false);
				}else{
					taskInfo.setChecked(true);
					viewHolder.cb_task.setChecked(true);
				}
			}
		});
		
		lv_task_item.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if(userTaskInfos != null && systemTaskInfos != null){
					if(firstVisibleItem <= userTaskInfos.size()){
						tv_status.setText("用户进程:" + userTaskInfos.size() + "个");
					}else{
						tv_status.setText("系统进程:" + systemTaskInfos.size() + "个");
					}
				}
			}
		});
		
		
	}
	
	/**
	 * 设置运行进程个数，内存等标题
	 */
	private void setTitle() {
		// TODO Auto-generated method stub
	
		tv_process_count.setText("运行中进程：" 
				+ processCount + "个");
		tv_memory_info.setText("剩余/总内存:" +
				Formatter.formatFileSize(this, avaiMem) 
				+ "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	/**
	 * 获取数据，更新listview
	 */
	private void fillData() {
		// TODO Auto-generated method stub
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				taskInfos = TaskInfoProvider.getTastInfo(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				
				
				for(TaskInfo taskInfo : taskInfos){
					if(taskInfo.isUserTask()){
						userTaskInfos.add(taskInfo);
					}else{
						systemTaskInfos.add(taskInfo);
					}
				}
				
				Log.d("debuge", "userTaskInfos : " + userTaskInfos.size());
				Log.d("debuge", "systemTaskInfos : "+ systemTaskInfos.size());
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						tv_status.setVisibility(View.VISIBLE);
						tv_status.setText("用户进程:" + userTaskInfos.size() + "个");
						if(adapter == null){
							adapter = new MyTaskManagerAdapter();
							lv_task_item.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						
						ll_loading.setVisibility(View.INVISIBLE);
					}
					
				});
			};
		}.start();
	}


	class MyTaskManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(sp.getBoolean("showsystem", false)){
				return userTaskInfos.size() + systemTaskInfos.size() + 2;
			}else{
				return userTaskInfos.size() + 1;
			}
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
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
			final TaskInfo taskInfo;
			
			if(position == 0){//
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(20);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户进程:" + userTaskInfos.size() + "个");
				Log.d("debuge", ""+ position);
				return tv;
			}
			else if(position == userTaskInfos.size() + 1){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统进程:" + systemTaskInfos.size() + "个");
				tv.setTextSize(20);
				Log.d("debuge", ""+ position);
				return tv;
			}else if(position <= userTaskInfos.size()){
				taskInfo = userTaskInfos.get(position - 1);
				Log.d("debuge", ""+ position);
			}else{
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
				Log.d("debuge", ""+ position);
			}
			
			if(convertView != null && !(convertView instanceof TextView)){
				view = convertView;
				viewHolder = (ViewHolder) convertView.getTag();
			}else{
				viewHolder = new ViewHolder();
				view = View.inflate(TaskManagerActivity.this,
						R.layout.list_item_taskinfo, null);
				viewHolder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				viewHolder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
				viewHolder.tv_task_mem = (TextView) view.findViewById(R.id.tv_task_mem);
				viewHolder.tv_task_type = (TextView) view.findViewById(R.id.tv_task_type);
				viewHolder.cb_task = (CheckBox) view.findViewById(R.id.cb_task);
				
				view.setTag(viewHolder);
			}
			
		
			viewHolder.tv_task_name.setText(taskInfo.getName());
			viewHolder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
			viewHolder.tv_task_mem.setText("占用内存:" 
					+ Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemsize()));
			if(taskInfo.isUserTask()){
				viewHolder.tv_task_type.setText("用户进程");
			}else{
				viewHolder.tv_task_type.setText("系统进程");
			}
			
			if(taskInfo.getPackname().equals("com.yjk.mobilesafety")){
				viewHolder.cb_task.setVisibility(View.INVISIBLE);
			}else{
				viewHolder.cb_task.setVisibility(View.VISIBLE);
				viewHolder.cb_task.setChecked(taskInfo.isChecked());
			}
			
			
			return view;
		}
		
		class ViewHolder{
			ImageView iv_task_icon;
			TextView tv_task_name;
			TextView tv_task_mem;
			TextView tv_task_type;
			CheckBox cb_task;
		}

	}
	
	/**
	 * 全选按钮的点击事件
	 * @param view
	 */
	public void selectAll(View view){
		for(TaskInfo taskinfo : taskInfos){
			if(!taskinfo.isChecked()){
				taskinfo.setChecked(true);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * 反选按钮
	 * @param view
	 */
	public void selectOpposite(View view){
		for(TaskInfo taskinfo : taskInfos){
				taskinfo.setChecked(!taskinfo.isChecked());
				adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 清理按钮响应事件
	 * @param view
	 */
	public void killProcess(View view){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		long killMemory = 0;
		int count = 0;
		
		for(TaskInfo taskinfo : taskInfos){
			
			//自己进程不能被杀死
			if(taskinfo.getPackname().equals("com.yjk.mobilesafety")){
				continue;
			}
			
			if(taskinfo.isChecked()){
				am.killBackgroundProcesses(taskinfo.getPackname());
				if(taskinfo.isUserTask()){
					userTaskInfos.remove(taskinfo);
				}else{
					systemTaskInfos.remove(taskinfo);
				}
				
				count++;
				killMemory += taskinfo.getMemsize();
			}
		}

		taskInfos.clear();
		taskInfos.addAll(userTaskInfos);
		taskInfos.addAll(systemTaskInfos);
		
		processCount -= count;
		avaiMem += killMemory;
		setTitle();
		
		adapter.notifyDataSetChanged();
		
		Toast.makeText(this, "杀死了" + count + "个进程," + "为您清理了" + Formatter.formatFileSize(this, killMemory) + "内存", 
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 设置按钮响应事件
	 * @param view
	 */
	public void setting(View view){
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
