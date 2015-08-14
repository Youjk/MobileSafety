package com.yjk.mobilesafety;

import java.util.ArrayList;
import java.util.List;

import com.yjk.mobilesafety.db.dao.BlackNumberDao;
import com.yjk.mobilesafety.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_callsms_safe;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> list_blacknumber = new ArrayList<BlackNumberInfo>();
	private BlackAdapter adapter;
	
	private LinearLayout ll_loading;
	int offset = 0;
	int maxNumber = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_safe);
		
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		
		dao = new BlackNumberDao(this);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		adapter = new BlackAdapter();
		lv_callsms_safe.setAdapter(adapter);
		fillData();
		//list_blacknumber = dao.findAll();
		
		//list_view做滚动事件的监听器
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			
			//滚动的状态变化的时候
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					//空闲状态
					//判断当前listview滚动的位置
					int lastpostion = lv_callsms_safe.getLastVisiblePosition();
					//集合里面有20个item，0-19
					if(lastpostion == list_blacknumber.size() - 1){
						offset += maxNumber;
						fillData();
					}
					
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					//手指触摸状态
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					//惯性滑翔状态
					break;
				default:
					break;
				}
			}
			
			//滚动时候调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void fillData() {
		// TODO Auto-generated method stub
		ll_loading.setVisibility(View.VISIBLE);
		
		new Thread(){
			public void run() {
				List<BlackNumberInfo> temp = dao.findPart(offset, maxNumber);
				
				//如果加载完了，就提示用户加载完毕
				if(temp.size() == 0){
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(CallSmsSafeActivity.this,
									"已加载完毕", Toast.LENGTH_SHORT).show();
							ll_loading.setVisibility(View.INVISIBLE);
						}
					});
					
					return;
				}

				list_blacknumber.addAll(temp);
				
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();
	}

	class BlackAdapter extends BaseAdapter{

		private ViewHolder viewHolder;
		private View view;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list_blacknumber.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list_blacknumber.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			final int index = position;
			BlackNumberInfo info = list_blacknumber.get(position);
			
			if(convertView != null){
				view = convertView;
				viewHolder = (ViewHolder) convertView.getTag();
			}else{
				view = View.inflate(CallSmsSafeActivity.this, 
							R.layout.list_item_blacknumber, null);
				
				viewHolder = new ViewHolder();
				viewHolder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
						builder.setTitle("警告");
						builder.setMessage("您确定将此号码从黑名单中删除?");
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								//list_blacknumber = dao.findAll();
								dao.delete(list_blacknumber.get(index).getNumber());
								list_blacknumber.remove(index);
								adapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						});
						
						builder.show();
					}
				});
				view.setTag(viewHolder);
			}
			
			viewHolder.tv_number.setText(info.getNumber());
			
			if("1".equals(info.getMode())){
				viewHolder.tv_mode.setText("电话拦截");
			}
			else if("2".equals(info.getMode())){
				viewHolder.tv_mode.setText("短信拦截");
			}else{
				viewHolder.tv_mode.setText("全部拦截");
			}
			
			return view;
		}
		
		class ViewHolder{
			TextView tv_number;
			TextView tv_mode;
			ImageView iv_delete;
		}
	}
	
	
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button btn_ok;
	private Button btn_cancle;
	private EditText et_black_number;
	
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this,
				R.layout.dialog_add_balcknumber, null);
		
		et_black_number = (EditText) contentView.findViewById(R.id.et_black_number);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		btn_ok = (Button) contentView.findViewById(R.id.ok);
		btn_cancle = (Button) contentView.findViewById(R.id.cancel);
		
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//手机号码
				String number = et_black_number.getText().toString();
				if(TextUtils.isEmpty(number)){
					Toast.makeText(CallSmsSafeActivity.this, 
							"号码不能为空", Toast.LENGTH_LONG).show();
					
					return;	
				}
				//拦截模式
				String mode;
				boolean phoneIntercept = cb_phone.isChecked();
				boolean smsIntercept = cb_sms.isChecked();
			
				if(phoneIntercept && smsIntercept){
					mode = "3";
				}else if(smsIntercept){
						mode = "2";
					}
				else if(phoneIntercept){
						mode = "1";
					}	
				else{
					Toast.makeText(CallSmsSafeActivity.this, 
							"请至少勾选一个拦截模式", Toast.LENGTH_LONG).show();
					return;
				}
				
				Log.d("debuge", mode);
				
				//如果号码已经存在则使用update
				if(dao.find(number)){
					dao.update(number, mode);
				}else{
					//不存在则使用add
					dao.add(number, mode);
				}
				
				boolean isNumberExits = false;
				for(BlackNumberInfo info : list_blacknumber){
					if(info.getNumber().equals(number)){
						info.setMode(mode);
						isNumberExits = true;
						break;
					}
				}
				
				if(!isNumberExits){
					BlackNumberInfo info = new BlackNumberInfo();
					info.setNumber(number);
					info.setMode(mode);
					list_blacknumber.add(0, info);
				}
				//list_blacknumber = dao.findAll();
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		dialog.setView(contentView);
		dialog.show();
	}
	
}
