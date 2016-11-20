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

    boolean stopThread = false;	//�ж�ֹͣ�߳�
    
	SQLiteDatabase db;
	Cursor c;	//ָ��
  	int curDay;
  	int curMonth;
	double lat;
	double lng;
	double befLat;
	double befLng;	//��¼ǰһ����¼��ľ�γ�ȣ���ʼֵ����Ϊ0���Ա����ж��Ƿ�Ϊ��һ����λ��
	int endMin;
	int endHour;	//��¼��λ�ý�����ʱ����Ϣ
	int startMin;
	int startHour;	//��¼��λ����ʼ��ʱ����Ϣ
	int curMin;
	int curHour;

    private InfoWindow mInfoWindow;
    
    /**
	 * @param args
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());	//�ٶȵ�ͼsdk��ʼ��
		setContentView(R.layout.track_live);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();// ������λͼ��
        mBaiduMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(0);
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        wea_info = getSharedPreferences("wea_info", 0); // ����sp
		TextView headText = (TextView) findViewById(
				R.id.TextView1);
		// Ĭ���������
		int weaCode = wea_info.getInt("wea_code", 0);
		if (weaCode == 1) { // ����
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_orange));
		}
		if (weaCode == 3) { // ����
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_gray));
		}
		if (weaCode == 4) { // ѩ��
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_blue));
		}
        
		final Handler handler = new Handler();	//����ʱ�߳�
        
		//��ȡ��ǰ��
      	Time time = new Time("Asia/Hong_Kong");
      	time.setToNow();
      	curDay = time.monthDay;
      	curMonth = time.month + 1;
      	
		db = this.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
		//��������track���½��ñ�
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		c = db.rawQuery("SELECT * FROM track WHERE day = ? and month = ?", new String[]{curDay + "", curMonth + ""}); //����α�
		
		befLat = 0.0;
		befLng = 0.0;	//��¼ǰһ����¼��ľ�γ�ȣ���ʼֵ����Ϊ0���Ա����ж��Ƿ�Ϊ��һ����λ��
		endMin = 0;
		endHour = 0;	//��¼��λ�ý�����ʱ����Ϣ
		
		if(c.moveToFirst()){//�α��ƶ������һλͬʱ�ж��α��Ƿ�Ϊ��
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// �ڴ˴����ִ�еĴ���
					if(stopThread == true){
						c.close();
						db.close();
						handler.removeCallbacks(this);
						TrackLive.this.finish();

					}
					else if(c.isAfterLast() == true || c.getInt(c.getColumnIndex("day")) != curDay){	//�����һ�����������л� ֹͣ��ʱ��
						c.close();
						db.close();
						handler.removeCallbacks(this);
			            Toast.makeText(getApplicationContext(), "Live����", Toast.LENGTH_SHORT).show(); 
						TrackLive.this.finish();
					}
					else{
						lat = c.getDouble(c.getColumnIndex("lat"));
						lng = c.getDouble(c.getColumnIndex("lon"));
						curMin = c.getInt(c.getColumnIndex("minute"));
						curHour = c.getInt(c.getColumnIndex("hour"));
						//�α����
						c.moveToNext();
						if(lng < 73 && c.isAfterLast() == false){	//�ж��Ƿ�ȱ���Ҳ�Ϊ���һ�����
							;	//���������
						}
						else{
							if(befLat == 0.0){	//��һ���㣬��ʼ��
								befLat = lat;
								befLng = lng;
								endMin = curMin;
								endHour = curHour;
							}
							if((lat != befLat && lng != befLng) || (c.isAfterLast() == true) || c.getInt(c.getColumnIndex("day")) != curDay){	//������λ�ã���λ���л��㣬�������һ����㣬�������л�	  
								if(lng < 73 && befLng < 73){	//���һ��ֵȱ�⣬֮ǰ��¼ֵҲΪȱ�⣬�������������ȱ��ֵ�������
									;
								}
								else{ 
									if(lng < 73){	//�����һ��ֵȱ��
										lng = befLng;
										lat = befLat;
									}
									startMin = curMin;
									startHour = curHour;	//��ʱ����¼Ϊ��һʱ��ε���ʼʱ���
									LatLng nPoint = new LatLng(lat,lng);
									BitmapDescriptor bitmap = BitmapDescriptorFactory
											.fromResource(R.drawable.timeline_green);
									MarkerOptions aa = new MarkerOptions()
											.position(nPoint)
											.icon(bitmap);
									//�ڵ����������ʾ�õ�ʱ���
									if(startMin < 10 && endMin < 10){
										aa.title("�õ�ʱ���\n" + endHour + ":0" + endMin
												+ " - " + startHour + ":0" + startMin);
									}else if(endMin < 10){
										aa.title("�õ�ʱ���\n" + endHour + ":0" + endMin
												+ " - " + startHour + ":" + startMin);
									}else if(startMin < 10){
										aa.title("�õ�ʱ���\n" + endHour + ":" + endMin
												+ " - " + startHour + ":0" + startMin);
									}else{
										aa.title("�õ�ʱ���\n" + endHour + ":" + endMin
												+ " - " + startHour + ":" + startMin);
									}
									aa.animateType(MarkerAnimateType.grow);
									mBaiduMap.clear();
									mBaiduMap.addOverlay(aa);
									// �ƶ��ڵ�������
							        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nPoint));
									//��ʾ��ϸ��Ϣ
									TextView infoPoint = new TextView(getApplicationContext());
									infoPoint.setBackgroundResource(R.drawable.popup);
									infoPoint.setText(aa.getTitle());
									infoPoint.setTextColor(Color.BLACK);
									infoPoint.setGravity(Gravity.CENTER);
									LatLng ll = aa.getPosition();
									mInfoWindow = new InfoWindow(infoPoint, ll, -47);
									mBaiduMap.showInfoWindow(mInfoWindow);
								
									endMin = curMin;	
									endHour = curHour;	//��ʱ��� ��¼Ϊ��һʱ��εĽ���ʱ���
		        					handler.postDelayed(this, 500);// 500����ʱʱ��
								}
								befLat = lat;
								befLng = lng;	//����¼�ý�㾭γ���Ա�����һ���Ƚ�
							}
						}
						handler.postDelayed(this, 50);// 500����ʱʱ��
					}					
				}
			};
			handler.postDelayed(runnable, 1);// �򿪶�ʱ����ִ�в���
		}    
		else{
			c.close();
			db.close();
			Toast.makeText(getApplicationContext(), "�����޼�¼", Toast.LENGTH_SHORT).show(); 
			TrackLive.this.finish();
		}
	}
	
	/**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        stopThread = true;
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }
}