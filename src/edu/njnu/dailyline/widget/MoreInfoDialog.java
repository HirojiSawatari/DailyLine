package edu.njnu.dailyline.widget;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import edu.njnu.dailyline.R;

import android.app.Dialog;  
import android.content.Context;  
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
  
public class MoreInfoDialog extends Dialog {  
	
    public MoreInfoDialog(Context context) {  
        super(context);  
    }  
  
    public MoreInfoDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder implements OnGetGeoCoderResultListener {  
    	private TextView[] addressTextView = new TextView[5];
    	private int priCode = 0;	//输出时计数值  
    	GeoCoder mSearch = null; // 搜索模块
    	SQLiteDatabase db;
    	SharedPreferences wea_info;
        private Context context;  
        private String time;;
        
        public Builder(Context context) {  
            this.context = context;  
        }  
        
        public Builder setTime(String time) {  
            this.time = time;  
            return this;  
        } 
        
        public MoreInfoDialog create(FragmentActivity ac, int code) {	//code传入点击list在listView中的序列号
		
        	LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final MoreInfoDialog dialog = new MoreInfoDialog(context,R.style.Dialog);  
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);  
            dialog.addContentView(layout, new 
            		LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            ((TextView) layout.findViewById(R.id.timeView)).setText(time);
            HistogramView hisView = (HistogramView) layout.findViewById(R.id.hisView);
            hisView.start(ac, code, 2);	//2表示柱状图动画开始
            
            wea_info = ac.getSharedPreferences("wea_info", 0); // 开启sp
    		TextView headText = (TextView) layout.findViewById(
    				R.id.TextView1);
    		// 默认阴天界面
    		int weaCode = wea_info.getInt("wea_code", 0);
    		if (weaCode == 1) { // 晴天
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_orange));
    		}
    		if (weaCode == 3) { // 雨天
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_gray));
    		}
    		if (weaCode == 4) { // 雪天
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_blue));
    		}
            
            //获取当前年月日
    		Time time = new Time("Asia/Hong_Kong");
    		time.setToNow();
    		
    		int searHour = time.hour - code;	//需要遍历的小时
            
    		db = ac.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
            db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
	                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
	                "uri VARCHAR, content VARCHAR)"); 
            //查找当日该小时所有数据
    		Cursor c = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", searHour + ""});

    		//向数组增加该事件记录
    		if(c.moveToLast()){	//能移动至最后一行，即游标不为空，即找到一个以上符合条件的行
    			LinearLayout infoLayout = (LinearLayout) layout.findViewById(R.id.infoLayout);
    			//标题
    			TextView title = new TextView(ac);
    			title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 70));
    			title.setGravity(Gravity.CENTER);
    			title.setTextColor(Color.BLACK);
    			title.setText("用户事件信息");
				infoLayout.addView(title);
				SDKInitializer.initialize(ac.getApplicationContext());	//百度地图sdk初始化
				//实例化GeoCoder对象
				mSearch = GeoCoder.newInstance();
				//注册检索监听器
				mSearch.setOnGetGeoCodeResultListener(this);
				LatLng searPoint;	//定位点
				int setCode = 0;	//建立时计数值
    			while(!c.isBeforeFirst()){	//保证游标指向结点存在
    				//事件容器
    				final LinearLayout eveTable = new LinearLayout(ac);
    				eveTable.setOrientation(LinearLayout.VERTICAL);
    				eveTable.setBackgroundColor(Color.WHITE);
    				eveTable.setPadding(30, 30, 30, 30);
					LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					margin.setMargins(40, 35, 40, 0);	//设置布局距离上下左右距离
					//加入总布局
    				infoLayout.addView(eveTable, margin);
    				
    				//地址
    				addressTextView[setCode] = new TextView(ac);
    				addressTextView[setCode].setTextColor(Color.BLACK);
    				eveTable.addView(addressTextView[setCode]);
                	setCode ++;	//计数值加一
    				searPoint = new LatLng(Double.valueOf(c.getString(c.getColumnIndex("lat"))), Double.valueOf(c.getString(c.getColumnIndex("lon"))));
    				// 反Geo搜索
                	mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                		.location(searPoint));
                	
                	//详细信息
                	TextView eventInfo = new TextView(ac);
                	eventInfo.setTextColor(Color.BLACK);
                	eventInfo.setText("事件详情：" + c.getString(c.getColumnIndex("content")));
                	eveTable.addView(eventInfo);
                	
    				//图片
    				ImageView eventPic = new ImageView(ac);
    				eventPic.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    				eventPic.setImageURI(Uri.parse(c.getString(c.getColumnIndex("uri"))));
    				eveTable.addView(eventPic);

    				c.moveToPrevious();
    			}
    		}
    		c.close();
    		db.close();
    		
            dialog.setContentView(layout);  
            return dialog;  
        }

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO 自动生成的方法存根
			
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// TODO 自动生成的方法存根
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				addressTextView[priCode].setText("详细地址：错误地址");
				priCode ++;	//计数值加一
				return;
	        }
			addressTextView[priCode].setText("详细地址：" + result.getAddress());
			priCode ++;
			return;
		}  
    }  
} 