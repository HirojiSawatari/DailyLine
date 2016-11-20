package edu.njnu.dailyline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;

import edu.njnu.dailyline.activity.TrackLive;
import edu.njnu.dailyline.widget.PopMenu;
import edu.njnu.dailyline.widget.PopMenu.OnItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class FragmentPage2 extends Fragment implements OnClickListener, OnItemClickListener {

	MapView mMapView = null;
	LocationClient mLocClient;
    BaiduMap mBaiduMap;
    public MyLocationListenner myListener = new MyLocationListenner();
    
    boolean stopThread = false;	//判断停止线程
    
	SQLiteDatabase db;
	Cursor c;	//指针
  	int curDay;
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
	
	Marker markTemp;
	LatLng latlngTemp;
	String newTitle;
    private InfoWindow mInfoWindow;

	private PopMenu popMenu;
	private ImageButton showButton;

	SharedPreferences wea_info;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		SDKInitializer.initialize(getActivity().getApplicationContext());	//百度地图sdk初始化
		return inflater.inflate(R.layout.fragment_2, null);		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        //定位点动态显示按钮
        showButton = (ImageButton)getActivity().findViewById(R.id.imageButton2);
        showButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		Intent intent = new Intent(getActivity(),TrackLive.class);
				getActivity().startActivity(intent);
        	}
        });
        
        //定位
        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //mBaiduMap.setMaxAndMinZoomLevel(16, 5);	//设置最大最小缩放范围（需考虑）
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
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
        
        //画当日轨迹线
        //构造折线点坐标集
        List<LatLng> points = new ArrayList<LatLng>();
        //获取当前日
      	Time time = new Time("Asia/Hong_Kong");
      	time.setToNow();
      	curDay = time.monthDay;
      	
		db = getActivity().openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
		//若不存在track表新建该表
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		c = db.query("track", null, null, null, null, null, null);  //获得游标
		
		//标记点集合
		List<Marker> mMarker = new ArrayList<Marker>();
		
		befLat = 0.0;
		befLng = 0.0;	//记录前一个记录点的经纬度，初始值均设为0，以便于判断是否为第一个定位点
		endMin = 0;
		endHour = 0;	//记录该位置结束点时间信息
		
		if(c.moveToLast()){//游标移动至最后一位同时判断游标是否为空
			while(!c.isBeforeFirst()){	//保证游标指向结点存在		
				if(c.getInt(c.getColumnIndex("day")) != curDay){	//日期切换，结束
					break;
				}
				lat = c.getDouble(c.getColumnIndex("lat"));
				lng = c.getDouble(c.getColumnIndex("lon"));
				curMin = c.getInt(c.getColumnIndex("minute"));
				curHour = c.getInt(c.getColumnIndex("hour"));
				//游标前移
				c.moveToPrevious();
				if(lng < 73 && c.isBeforeFirst() == false){	//判断是否缺测且不为最后一个结点
	        		continue;	//忽略这个点
	        	}
				if(befLat == 0.0){	//第一个点，初始化
					befLat = lat;
					befLng = lng;
					endMin = curMin;
					endHour = curHour;
				}
				if((lat != befLat && lng != befLng) || (c.isBeforeFirst() == true) || c.getInt(c.getColumnIndex("day")) != curDay){	//发现新位置，即位置切换点，或者最后一个结点，或日期切换	  
					if(lng < 73){	//最后一个值缺测，之前记录值也为缺测，即最后连续出现缺测值，需忽略
						if(befLng < 73){
							continue;
						}
						lng = befLng;
						lat = befLat;
					}
					startMin = curMin;
					startHour = curHour;	//该时间点记录为上一时间段的起始时间点
					   
					//寻找list中有无相同位置定位点
					int i;
					for(i = 0; i < mMarker.size(); i++){
						markTemp = mMarker.get(i);
						latlngTemp = markTemp.getPosition();
						if(latlngTemp.latitude == lat && latlngTemp.longitude == lng){	//发现相同位置点，只需修改原信息即可
							if(startMin < 10 && endMin < 10){
								newTitle = markTemp.getTitle() + "\n" + startHour + ":0" + startMin
										+ " - " + endHour + ":0" + endMin;
							}else if(startMin < 10){
								newTitle = markTemp.getTitle() + "\n" + startHour + ":0" + startMin
										+ " - " + endHour + ":" + endMin;
							}else if(endMin < 10){
								newTitle = markTemp.getTitle() + "\n" + startHour + ":" + startMin
										+ " - " + endHour + ":0" + endMin;
							}else{
								newTitle = markTemp.getTitle() + "\n" + startHour + ":" + startMin
										+ " - " + endHour + ":" + endMin;
							}
							markTemp.setTitle(newTitle);
							mMarker.remove(i);
							mMarker.add(markTemp);
							break;
						}
					}
					if(i == mMarker.size()){	//list中无该位置点信息
						//标记点
						BitmapDescriptor bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.timeline_green);
						
						MarkerOptions aa = new MarkerOptions()
							.position(new LatLng(lat,lng))
							.icon(bitmap);
						
						//在点击气泡上显示该点时间段
						if(startMin < 10 && endMin < 10){
							aa.title("该点时间段\n" + startHour + ":0" + startMin
									+ " - " + endHour + ":0" + endMin);
						}else if(startMin < 10){
							aa.title("该点时间段\n" + startHour + ":0" + startMin
									+ " - " + endHour + ":" + endMin);
						}else if(endMin < 10){
							aa.title("该点时间段\n" + startHour + ":" + startMin
									+ " - " + endHour + ":0" + endMin);
						}else{
							aa.title("该点时间段\n" + startHour + ":" + startMin
									+ " - " + endHour + ":" + endMin);
						}
						
						aa.animateType(MarkerAnimateType.grow);
						//mBaiduMap.addOverlay(aa); 防止冗余，注销
						mMarker.add((Marker)mBaiduMap.addOverlay(aa));
					}
					//轨迹线要素添加折点
					points.add(new LatLng(lat,lng));
					
					endMin = curMin;
					endHour = curHour;	//该时间点 记录为下一时间段的结束时间点
				}
				befLat = lat;
				befLng = lng;	//最后记录该结点经纬度以便于下一结点比较
			}
		}
		c.close();
		db.close();
		//构造对象
		if(points.size() >= 2){
		    BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
			OverlayOptions trackPolyline = new PolylineOptions().width(15).dottedLine(true).customTexture(mGreenTexture).points(points);
			mBaiduMap.addOverlay(trackPolyline);
		}
		
		//点击点事件监听
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
            	TextView infoPoint = new TextView(getActivity().getApplicationContext());
            	infoPoint.setBackgroundResource(R.drawable.popup);
            	infoPoint.setText(marker.getTitle());
            	infoPoint.setTextColor(Color.BLACK);
            	infoPoint.setGravity(Gravity.CENTER);
            	LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(infoPoint, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
		});

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
            builder.target(ll).zoom(16.0f);
            //builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	
	@Override
	public void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override
	public void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();
    }  
    @Override
	public void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
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
			mMapView.getMap().snapshot(new SnapshotReadyCallback() {
                public void onSnapshotReady(Bitmap snapshot) {
                    File file = new File("/mnt/sdcard/DailyLine/map.png");
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        if (snapshot.compress(
                                Bitmap.CompressFormat.PNG, 100, out)) {
                            out.flush();
                            out.close();
                        };
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
			//ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "时间轴截图");//保存图片并插入图库
		}
		//第三方分享
		else if(index == 1){
			mMapView.getMap().snapshot(new SnapshotReadyCallback() {
                public void onSnapshotReady(Bitmap snapshot) {
                    File file = new File("/mnt/sdcard/DailyLine/map.png");
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        if (snapshot.compress(
                                Bitmap.CompressFormat.PNG, 100, out)) {
                            out.flush();
                            out.close();
                        };
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
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
		 oks.setText("轨迹地图分享功能测试");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath("/sdcard/DailyLine/map.png");//确保SDcard下面存在此张图片
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
}