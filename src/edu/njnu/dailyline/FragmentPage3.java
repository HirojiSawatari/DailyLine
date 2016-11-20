package edu.njnu.dailyline;

import edu.njnu.dailyline.widget.RefreshableView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class FragmentPage3 extends Fragment implements RefreshableView.RefreshListener {

	private RefreshableView mRefreshableView;
	LinearLayout linearLayout1;
	SharedPreferences wea_info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_3,container, false);
		//初始化定位点资料卡容器布局
		linearLayout1 = (LinearLayout)view.findViewById(R.id.point_collec);
		//初始化表
		getTable();
		return view;
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
		//初始化刷新控件
		mRefreshableView = (RefreshableView)getActivity().findViewById(R.id.refresh_root);
		mRefreshableView.setRefreshListener(this);

        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//开启sp
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//晴天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        }
        if(weaCode == 3){	//雨天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        }
        if(weaCode == 4){	//雪天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
        }
	}
	
	
	//获取广场表
	public void getTable() {
		//TODO:传回十条广场信息，最好为类似List形式
		/*
		//记录数据库记录之后结点信息
		int nextDay = 0;
		String nextPoi = null;
		//记录生成框图时的时间信息以输出时间段
		int endHour = 25;
		int endMinute = 0;
		int endSecond = 0;
		//利用SQLite输出所有点
		db = getActivity().openOrCreateDatabase("Track_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
		Cursor c = db.query("track", null, null, null, null, null, null);  //获得游标
		if(c.moveToLast()){//判断游标是否为空
			while(!c.isBeforeFirst()){	//判断指针后面是否有元素
				int reid = c.getInt(c.getColumnIndex("_id"));
				int p_year = c.getInt(c.getColumnIndex("year"));
				int p_month = c.getInt(c.getColumnIndex("month"));
				int p_day = c.getInt(c.getColumnIndex("day"));
				int p_hour = c.getInt(c.getColumnIndex("hour"));
				int p_minute = c.getInt(c.getColumnIndex("minute"));
				int p_second = c.getInt(c.getColumnIndex("second"));
				String p_wea = c.getString(c.getColumnIndex("weather"));
				String p_poi = c.getString(c.getColumnIndex("address"));
				if(p_wea == null){
					p_wea = "缺测";
				}
				c.moveToPrevious();
				
				if(endHour == 25){	//此时遍历的是第一个点
					endHour = p_hour;
					endMinute = p_minute;
					endSecond = p_second;
				}
				
				if(reid > 1){
					nextDay = c.getInt(c.getColumnIndex("day"));
					nextPoi = c.getString(c.getColumnIndex("address"));
				}
				
				if(!nextPoi.equals(p_poi) || nextDay != p_day || reid == 1){	//若切换poi点或经过一天或为最后一个点即打印该结点
					//添加文字以及图标总容器
					final LinearLayout pwTable = new LinearLayout(getActivity());
					pwTable.setOrientation(LinearLayout.HORIZONTAL);
					pwTable.setBackgroundColor(Color.WHITE);
					pwTable.setPadding(30, 30, 30, 30);
					LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					margin.setMargins(40, 35, 40, 0);	//设置布局距离上下左右距离
				
				
					//添加说明文字至布局
					final LinearLayout pointTable = new LinearLayout(getActivity());
					pointTable.setOrientation(LinearLayout.VERTICAL);
					pointTable.setBackgroundColor(Color.WHITE);	
				
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					params1.weight = 3.0f;	//占四分之三比例

					TextView p_id = new TextView(getActivity());
					TextView p_time = new TextView(getActivity());
					TextView p_loc = new TextView(getActivity());
					p_id.setTextColor(Color.BLACK);
					p_time.setTextColor(Color.BLACK);
					p_loc.setTextColor(Color.BLACK);
					p_id.setText("日期：" + p_year + "年" + p_month + "月" + p_day + "日");
					p_time.setText("时间段：" + p_hour + "时" + p_minute + "分 至 " + endHour + "时" + endMinute + "分");
					p_loc.setText("地点：" + p_poi);
					pointTable.addView(p_id);
					pointTable.addView(p_time);
					pointTable.addView(p_loc);
					pwTable.addView(pointTable, params1);
				
					//添加天气图标至布局
					final ImageView weaView = new ImageView(getActivity());
					weaView.setBackgroundColor(Color.WHITE);	
				
					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					params2.weight = 1.0f;	//占四分之一比例
					weaView.setLayoutParams(params2);
				
					if(p_wea.indexOf("晴") != -1){
						weaView.setImageResource(R.drawable.wea_hare);
					}else if(p_wea.indexOf("多云") != -1){
						weaView.setImageResource(R.drawable.wea_kumori1);
					}else if(p_wea.indexOf("阴") != -1){
						weaView.setImageResource(R.drawable.wea_kumori2);
					}else if(p_wea.indexOf("雷") != -1){
						weaView.setImageResource(R.drawable.wea_kaminari);
					}else if(p_wea.indexOf("雨夹雪") != -1){
						weaView.setImageResource(R.drawable.wea_mizore);
					}else if(p_wea.indexOf("阵雨") != -1){
						weaView.setImageResource(R.drawable.wea_niwakaame);
					}else if(p_wea.indexOf("雨") != -1){
						weaView.setImageResource(R.drawable.wea_ame);
					}else if(p_wea.indexOf("雪") != -1){
						weaView.setImageResource(R.drawable.wea_yuki);
					}else if(p_wea.indexOf("雾") != -1 || p_wea.indexOf("霾") != -1){
						weaView.setImageResource(R.drawable.wea_kiri);
					}else{
						weaView.setImageResource(R.drawable.wea_nashi);
					}
				
					pwTable.addView(weaView);
					//加入总布局
					linearLayout1.addView(pwTable, margin);
					//输出后记录此时的结束点
					if(reid > 1){
						endHour = c.getInt(c.getColumnIndex("hour"));
						endMinute = c.getInt(c.getColumnIndex("minute"));
						endSecond = c.getInt(c.getColumnIndex("second"));
					}
				}
			}
		}
		c.close();
		db.close();
		*/
	}

	public void removeTable() {
		//清除布局中显示的所有点信息
		int count = linearLayout1.getChildCount();
		for(int i = count - 1; i > -1; i --){
			linearLayout1.removeViewAt(i);
		}
	}
	
	public void onRefresh(RefreshableView view) {
		// TODO 自动生成的方法存根
		removeTable();
		getTable();
		mRefreshableView.finishRefresh();
		Toast.makeText(getActivity().getApplicationContext(), R.string.toast_text, Toast.LENGTH_SHORT).show();
	}
}