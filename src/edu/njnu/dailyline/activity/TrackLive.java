package edu.njnu.dailyline.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import edu.njnu.dailyline.R;
import edu.njnu.dailyline.FragmentPage2.MyLocationListenner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class TrackLive extends Activity {
	
	MapView mMapView = null;
    BaiduMap mBaiduMap;
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
	SharedPreferences wea_info;

    boolean stopThread = false;	//判断停止线程
    
	SQLiteDatabase db;
	Cursor c;	//指针
  	int curDay;
  	int curMonth;
	double lat;
	double lng;
	double befLat;
	double befLng;	//记录前一个记录点的经纬度，初始值均设为0，以便于判断是否为第一个定位点
	int endMin;
	int endHour;	//记录该位置结束点时间信息
	int startMin;
	int startHour;	//记录该位置起始点时间信息
	int curMin;
	int curHour;

    private InfoWindow mInfoWindow;
    
    /**
	 * @param args
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());	//百度地图sdk初始化
		setContentView(R.layout.track_live);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();// 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(0);
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        wea_info = getSharedPreferences("wea_info", 0); // 开启sp
		TextView headText = (TextView) findViewById(
				R.id.TextView1);
		// 默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
		if (weaCode == 1) { // 晴天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_orange));
		}
		if (weaCode == 3) { // 雨天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_gray));
		}
		if (weaCode == 4) { // 雪天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_blue));
		}
        
		final Handler handler = new Handler();	//开定时线程
        
		//获取当前日
      	Time time = new Time("Asia/Hong_Kong");
      	time.setToNow();
      	curDay = time.monthDay;
      	curMonth = time.month + 1;
      	
		db = this.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
		//若不存在track表新建该表
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		c = db.rawQuery("SELECT * FROM track WHERE day = ? and month = ?", new String[]{curDay + "", curMonth + ""}); //获得游标
		
		befLat = 0.0;
		befLng = 0.0;	//记录前一个记录点的经纬度，初始值均设为0，以便于判断是否为第一个定位点
		endMin = 0;
		endHour = 0;	//记录该位置结束点时间信息
		
		if(c.moveToFirst()){//游标移动至最后一位同时判断游标是否为空
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 在此处添加执行的代码
					if(stopThread == true){
						c.close();
						db.close();
						handler.removeCallbacks(this);
						TrackLive.this.finish();

					}
					else if(c.isAfterLast() == true || c.getInt(c.getColumnIndex("day")) != curDay){	//到最后一个或者日期切换 停止计时器
						c.close();
						db.close();
						handler.removeCallbacks(this);
			            Toast.makeText(getApplicationContext(), "Live结束", Toast.LENGTH_SHORT).show(); 
						TrackLive.this.finish();
					}
					else{
						lat = c.getDouble(c.getColumnIndex("lat"));
						lng = c.getDouble(c.getColumnIndex("lon"));
						curMin = c.getInt(c.getColumnIndex("minute"));
						curHour = c.getInt(c.getColumnIndex("hour"));
						//游标后移
						c.moveToNext();
						if(lng < 73 && c.isAfterLast() == false){	//判断是否缺测且不为最后一个结点
							;	//忽略这个点
						}
						else{
							if(befLat == 0.0){	//第一个点，初始化
								befLat = lat;
								befLng = lng;
								endMin = curMin;
								endHour = curHour;
							}
							if((lat != befLat && lng != befLng) || (c.isAfterLast() == true) || c.getInt(c.getColumnIndex("day")) != curDay){	//发现新位置，即位置切换点，或者最后一个结点，或日期切换	  
								if(lng < 73 && befLng < 73){	//最后一个值缺测，之前记录值也为缺测，即最后连续出现缺测值，需忽略
									;
								}
								else{ 
									if(lng < 73){	//仅最后一个值缺测
										lng = befLng;
										lat = befLat;
									}
									startMin = curMin;
									startHour = curHour;	//该时间点记录为上一时间段的起始时间点
									LatLng nPoint = new LatLng(lat,lng);
									BitmapDescriptor bitmap = BitmapDescriptorFactory
											.fromResource(R.drawable.timeline_green);
									MarkerOptions aa = new MarkerOptions()
											.position(nPoint)
											.icon(bitmap);
									//在点击气泡上显示该点时间段
									if(startMin < 10 && endMin < 10){
										aa.title("该点时间段\n" + endHour + ":0" + endMin
												+ " - " + startHour + ":0" + startMin);
									}else if(endMin < 10){
										aa.title("该点时间段\n" + endHour + ":0" + endMin
												+ " - " + startHour + ":" + startMin);
									}else if(startMin < 10){
										aa.title("该点时间段\n" + endHour + ":" + endMin
												+ " - " + startHour + ":0" + startMin);
									}else{
										aa.title("该点时间段\n" + endHour + ":" + endMin
												+ " - " + startHour + ":" + startMin);
									}
									aa.animateType(MarkerAnimateType.grow);
									mBaiduMap.clear();
									mBaiduMap.addOverlay(aa);
									// 移动节点至中心
							        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nPoint));
									//显示详细信息
									TextView infoPoint = new TextView(getApplicationContext());
									infoPoint.setBackgroundResource(R.drawable.popup);
									infoPoint.setText(aa.getTitle());
									infoPoint.setTextColor(Color.BLACK);
									infoPoint.setGravity(Gravity.CENTER);
									LatLng ll = aa.getPosition();
									mInfoWindow = new InfoWindow(infoPoint, ll, -47);
									mBaiduMap.showInfoWindow(mInfoWindow);
								
									endMin = curMin;	
									endHour = curHour;	//该时间点 记录为下一时间段的结束时间点
		        					handler.postDelayed(this, 500);// 500是延时时长
								}
								befLat = lat;
								befLng = lng;	//最后记录该结点经纬度以便于下一结点比较
							}
						}
						handler.postDelayed(this, 50);// 500是延时时长
					}					
				}
			};
			handler.postDelayed(runnable, 1);// 打开定时器，执行操作
		}    
		else{
			c.close();
			db.close();
			Toast.makeText(getApplicationContext(), "本日无记录", Toast.LENGTH_SHORT).show(); 
			TrackLive.this.finish();
		}
	}
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        stopThread = true;
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
    }
}