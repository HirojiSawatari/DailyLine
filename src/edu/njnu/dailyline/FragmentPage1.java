package edu.njnu.dailyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import edu.njnu.dailyline.AddEventLoc.MyLocationListenner;
import edu.njnu.dailyline.activity.PicActivity;
import edu.njnu.dailyline.services.QueryService;
import edu.njnu.dailyline.utils.ScreenShot;
import edu.njnu.dailyline.widget.MoreInfoDialog;
import edu.njnu.dailyline.widget.PopMenu;
import edu.njnu.dailyline.widget.PopMenu.OnItemClickListener;
import edu.njnu.dailyline.widget.TimelineAdapter;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class FragmentPage1 extends Fragment implements OnClickListener, OnItemClickListener {
	
	private ListView listView;  
    List<String> data ;  
    private TimelineAdapter timelineAdapter;  
    
	SQLiteDatabase db;
	PackageManager pm;
	Drawable dw;
	
	private PopMenu popMenu;
	private ImageButton addButton;

	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private TextView temperaText;
	private TextView cityName;
	
	String city = "";
	String weather = "";
	String temperature = "";
	
	SharedPreferences wea_info;
    
	@Override
	//初始化地图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_1,container, false);
		return view;		
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//开启sp
        //添加状态按键
		addButton = (ImageButton)getActivity().findViewById(R.id.imageButton2);
        addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent(getActivity(),PicActivity.class);
				getActivity().startActivity(intent);
			}
        });
        
        //定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setScanSpan(0);
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        mLocClient.setLocOption(option);
        mLocClient.start();
        
		
		//分享按键下拉栏
		getActivity().findViewById(R.id.imageButton1).setOnClickListener(this);
		popMenu = new PopMenu(getActivity());
		popMenu.addItems(new String[]{"分享至广场", "分享至第三方"});
		popMenu.setOnItemClickListener(this);
		
		//时间轴初始化
		listView = (ListView) getActivity().findViewById(R.id.listview);
		listView.setDividerHeight(0);
		timelineAdapter = new TimelineAdapter(getActivity(), getData());
		listView.setAdapter(timelineAdapter);
		listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back));

		//时间轴点击事件
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自动生成的方法存根
				MoreInfoDialog.Builder builder = new MoreInfoDialog.Builder(getActivity());
				Time time = new Time("Asia/Hong_Kong");
				time.setToNow();
				builder.setTime(time.year + "年" + (time.month + 1) + "月" + time.monthDay + "日  " + (time.hour - arg2) + "时");
				builder.create(getActivity(), arg2).show();
			}
		});
		
		ImageView firstIcon = (ImageView) getActivity().findViewById(R.id.firstIcon);
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//晴天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_sunny));
        	firstIcon.setImageResource(R.drawable.head_sunny);	
        }
        if(weaCode == 3){	//雨天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_rain));
        	firstIcon.setImageResource(R.drawable.head_rain);	
        }
        if(weaCode == 4){	//雪天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_snow));
        	firstIcon.setImageResource(R.drawable.head_snow);	
        }
        
        temperaText = (TextView) getActivity().findViewById(R.id.temperaText);
        cityName = (TextView) getActivity().findViewById(R.id.cityName);
        String curCity = wea_info.getString("city_name", "南京");
        String curTemp = wea_info.getString("tempera", "28℃/33℃");
        temperaText.setText(curTemp);
        cityName.setText(curCity);
	}
	
	//获取时间轴数据
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//获取当前年月日
		Time time = new Time("Asia/Hong_Kong");
		time.setToNow();
		
		String packName = null;
		int curYear = time.year;
		int curMonth = (time.month) + 1;
		int curDay = time.monthDay;
		
		String[] useApp = new String[60];
		int[] useTime = new int[60];
		
		//利用SQLite输出所有点
		db = getActivity().openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
		//若不存在track表新建该表
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		Cursor c = db.query("track", null, null, null, null, null, null);  //获得游标
		
		int rbefHour;
		int rcurHour;
		
		if(c.moveToLast()){//游标移动至最后一位同时判断游标是否为空
			int befListHour = -1; 	//记录ListView上一行数据中小时信息
			rbefHour = c.getInt(c.getColumnIndex("hour"));	//第一个时间值
			while(!c.isBeforeFirst()){	//保证游标指向结点存在		
				if(c.getInt(c.getColumnIndex("day")) != curDay){	//日期切换，跳出
					break;
				}
				packName = c.getString(c.getColumnIndex("appname"));
				rcurHour = c.getInt(c.getColumnIndex("hour"));
				if(rcurHour != rbefHour){	//上一小时数据已遍历完毕，输出
					if(befListHour - rbefHour > 1){	//中间间隔超过一个小时未有APP信息记录
						for(int i = befListHour - 1; i > rbefHour; i--){
							Map<String, Object> map = new HashMap<String, Object>();
							map = printNoIcon(i);	//建立不含应用程序信息的list
							list.add(map);
						}
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map = printIcon(useApp, useTime, rbefHour);
					befListHour = rbefHour;
					//若不存在event表新建该表
					db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
			                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
			                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
			                "uri VARCHAR, content VARCHAR)"); 
					Cursor c1 = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", rbefHour + ""});
					if(c1.moveToFirst()){	//能移动至第一行，即游标不为空，即找到一个以上符合条件的行						
						map.put("isShowEvent", "1");	//加上事件标签
						map.put("eventName", "有其他事件");
					}
					list.add(map);
					useApp = new String[60];
					useTime = new int[60];
				}
				
				//向数组中添加该结点记录
				if(!packName.equals("")){
					if(!packName.contains("launcher")){	//排除系统桌面启动器
						int i = 0;
						while(useTime[i] > 0){
							if(useApp[i].equals(packName)){	//找到相同包名记录
								useTime[i] ++;
								break;
							}
							i++;
						}
						if(useTime[i] == 0){	//未发现相同包名记录
							useApp[i] = packName;
							useTime[i] = 1;
						}
					}
				}
				rbefHour = rcurHour;	//重新赋值
				c.moveToPrevious();
			}
			//输出最后一个结构体
			if(befListHour - rbefHour > 1){	//中间间隔超过一个小时未有APP信息记录
				for(int i = befListHour - 1; i > rbefHour; i--){	//先输出不存在APP事件的list
					Map<String, Object> map = new HashMap<String, Object>();
					map = printNoIcon(i);	//建立不含应用程序信息的list
					list.add(map);
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map = printIcon(useApp, useTime, rbefHour);
			//开始处理event，若不存在event表新建该表
			db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
	                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
	                "uri VARCHAR, content VARCHAR)"); 
			Cursor c1 = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", rbefHour + ""});
			if(c1.moveToFirst()){	//能移动至第一行，即游标不为空，即找到一个以上符合条件的行							
				map.put("isShowEvent", "1");	//加上事件标签	
				map.put("eventName", "有其他事件");
			}
			list.add(map);
			//最后一个有记录结点输出，判断是否存在该时段之前未有APP记录的点
			if(rbefHour > 0){	//未到0点，补齐之前的小时记录
				for(int i = rbefHour - 1; i >= 0; i--){
					map = new HashMap<String, Object>();
					map = printNoIcon(i);	//建立不含应用程序信息的list
					list.add(map);
				}
			}
		}
		c.close();
		db.close();
		return list;
	}
	
	public Drawable getAppIcon(String packname){
		Drawable iconDw = null;
		pm = getActivity().getPackageManager();
		ApplicationInfo info = null;
		try{
			info = pm.getApplicationInfo(packname, 0);
			iconDw = info.loadIcon(pm);
		} catch (NameNotFoundException e){
			e.printStackTrace();
		}
		return iconDw;
		
	}
	
	public void onClick(View arg0) {
		// TODO 自动生成的方法存根
		if(arg0.getId() == R.id.imageButton1){
			popMenu.showAsDropDown(arg0);
		}
	}

	@Override
	public void onItemClick(int index) {
		// TODO 自动生成的方法存根
		//分享至广场
		if(index == 0){
			ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "DailyLine");//保存图片并插入图库
		}
		//第三方分享
		else if(index == 1){
			ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "DailyLine");//保存图片并插入图库
			showShare();
		}
	}	
	
	//调用一键分享
	private void showShare() {
		 ShareSDK.initSDK(getActivity());
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle("DailyLine");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://dky.njnu.edu.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("时间轴分享功能测试");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath("/sdcard/DailyLine/list.jpg");
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://dky.njnu.edu.cn");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://dky.njnu.edu.cn");
		 
		 // 启动分享GUI
		 oks.show(getActivity());
	}
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            city = location.getCity();
            city = city.substring(0, city.length() - 1);
            
            //向sp存入城市名
            SharedPreferences.Editor editor = wea_info.edit();
			editor.putString("city_name",city); 
			editor.commit();
			
			cityName.setText(city);
         	//wj:2016年8月10日15:50:33 
            //查询天气情况
            new Thread(){
            	public void run() {
            		try {
            			List<String>  result = QueryService.getWeather(city);
    						
    					for(int i = 0 ;i<result.size();i++){
    					System.out.println(result.get(i));
    				}
    						
            			weather = result.get(7).substring(result.get(7).length()-2);
            			temperature = result.get(8);
            			
            			if(!temperature.equals("")){	//不为空
            				temperaText.setText(temperature);
            			}
            			//System.out.println(weather+","+temperature);
    						
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            		if(!temperature.equals("")){	//不为空
 
            			if(weather.indexOf("雪") != -1){
            				//存入天气与温度
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 4);
            				editor.putString("tempera", temperature);
            				editor.commit();
            			}
            			else if(weather.indexOf("雨") != -1){
            				//存入天气与温度
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 3);
            				editor.putString("tempera", temperature);
            				editor.commit();
            			}
            			else if(weather.indexOf("晴") != -1){
            				//存入天气与温度
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 1);
            				editor.putString("tempera", temperature);
        					editor.commit();
            			}
            			else{
        					//存入天气与温度
        					SharedPreferences.Editor editor = wea_info.edit();
        					editor.putInt("wea_code", 2);
        					editor.putString("tempera", temperature);
        					editor.commit();
        				}
            		}
            	};
            }.start();
            
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	
	//图标显示
	private Map<String, Object> printIcon(String [] useApp, int [] useTime, int rbefHour){
		//从大到小冒泡排序
		int tempTime;   
		String tempApp, timeText;
	    int size = useTime.length;   
	    for (int i = 0; i < size - 1; i++) {   
	        for (int j = i; j < size - 1; j++) {   
	            if (useTime[i] < useTime[j]) {
	            	tempTime = useTime[i];
	            	tempApp = useApp[i];
	                useTime[i] = useTime[j];  
	                useApp[i] = useApp[j]; 
	                useTime[j] = tempTime;   
	                useApp[j] = tempApp;
	            }   
	        }   
	    }
	    Map<String, Object> map = new HashMap<String, Object>();
		//图标显示临时检测
		dw = getAppIcon(useApp[0]);		
		map.put("firstIcon", dw);
		dw = getAppIcon(useApp[1]);		
		map.put("secondIcon", dw);
		dw = getAppIcon(useApp[2]);	
		map.put("thirdIcon", dw);
		dw = getAppIcon(useApp[3]);		
		map.put("fourthIcon", dw);
		dw = getAppIcon(useApp[4]);		
		map.put("fifthIcon", dw);
		timeText = rbefHour + " : 00";
		map.put("time", timeText);
		return map;	    
	}
	
	private Map<String, Object> printNoIcon(int rbefHour){
	    String timeText;
		Map<String, Object> map = new HashMap<String, Object>();
		timeText = rbefHour + " : 00";
		map.put("time", timeText);
		map.put("noApp", "该时间段未使用手机");
		return map;
	}
}