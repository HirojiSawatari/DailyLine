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
    
    boolean stopThread = false;	//�ж�ֹͣ�߳�
    
	SQLiteDatabase db;
	Cursor c;	//ָ��
  	int curDay;
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
	
	Marker markTemp;
	LatLng latlngTemp;
	String newTitle;
    private InfoWindow mInfoWindow;

	private PopMenu popMenu;
	private ImageButton showButton;

	SharedPreferences wea_info;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		SDKInitializer.initialize(getActivity().getApplicationContext());	//�ٶȵ�ͼsdk��ʼ��
		return inflater.inflate(R.layout.fragment_2, null);		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        //��λ�㶯̬��ʾ��ť
        showButton = (ImageButton)getActivity().findViewById(R.id.imageButton2);
        showButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		Intent intent = new Intent(getActivity(),TrackLive.class);
				getActivity().startActivity(intent);
        	}
        });
        
        //��λ
        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //mBaiduMap.setMaxAndMinZoomLevel(16, 5);	//���������С���ŷ�Χ���迼�ǣ�
        // ������λͼ��
        mBaiduMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(0);
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        //������������
        getActivity().findViewById(R.id.imageButton1).setOnClickListener(this);
      		popMenu = new PopMenu(getActivity());
      		popMenu.addItems(new String[]{"�������㳡", "������������"});
      		popMenu.setOnItemClickListener(this);
        
        //�����չ켣��
        //�������ߵ����꼯
        List<LatLng> points = new ArrayList<LatLng>();
        //��ȡ��ǰ��
      	Time time = new Time("Asia/Hong_Kong");
      	time.setToNow();
      	curDay = time.monthDay;
      	
		db = getActivity().openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
		//��������track���½��ñ�
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		c = db.query("track", null, null, null, null, null, null);  //����α�
		
		//��ǵ㼯��
		List<Marker> mMarker = new ArrayList<Marker>();
		
		befLat = 0.0;
		befLng = 0.0;	//��¼ǰһ����¼��ľ�γ�ȣ���ʼֵ����Ϊ0���Ա����ж��Ƿ�Ϊ��һ����λ��
		endMin = 0;
		endHour = 0;	//��¼��λ�ý�����ʱ����Ϣ
		
		if(c.moveToLast()){//�α��ƶ������һλͬʱ�ж��α��Ƿ�Ϊ��
			while(!c.isBeforeFirst()){	//��֤�α�ָ�������		
				if(c.getInt(c.getColumnIndex("day")) != curDay){	//�����л�������
					break;
				}
				lat = c.getDouble(c.getColumnIndex("lat"));
				lng = c.getDouble(c.getColumnIndex("lon"));
				curMin = c.getInt(c.getColumnIndex("minute"));
				curHour = c.getInt(c.getColumnIndex("hour"));
				//�α�ǰ��
				c.moveToPrevious();
				if(lng < 73 && c.isBeforeFirst() == false){	//�ж��Ƿ�ȱ���Ҳ�Ϊ���һ�����
	        		continue;	//���������
	        	}
				if(befLat == 0.0){	//��һ���㣬��ʼ��
					befLat = lat;
					befLng = lng;
					endMin = curMin;
					endHour = curHour;
				}
				if((lat != befLat && lng != befLng) || (c.isBeforeFirst() == true) || c.getInt(c.getColumnIndex("day")) != curDay){	//������λ�ã���λ���л��㣬�������һ����㣬�������л�	  
					if(lng < 73){	//���һ��ֵȱ�⣬֮ǰ��¼ֵҲΪȱ�⣬�������������ȱ��ֵ�������
						if(befLng < 73){
							continue;
						}
						lng = befLng;
						lat = befLat;
					}
					startMin = curMin;
					startHour = curHour;	//��ʱ����¼Ϊ��һʱ��ε���ʼʱ���
					   
					//Ѱ��list��������ͬλ�ö�λ��
					int i;
					for(i = 0; i < mMarker.size(); i++){
						markTemp = mMarker.get(i);
						latlngTemp = markTemp.getPosition();
						if(latlngTemp.latitude == lat && latlngTemp.longitude == lng){	//������ͬλ�õ㣬ֻ���޸�ԭ��Ϣ����
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
					if(i == mMarker.size()){	//list���޸�λ�õ���Ϣ
						//��ǵ�
						BitmapDescriptor bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.timeline_green);
						
						MarkerOptions aa = new MarkerOptions()
							.position(new LatLng(lat,lng))
							.icon(bitmap);
						
						//�ڵ����������ʾ�õ�ʱ���
						if(startMin < 10 && endMin < 10){
							aa.title("�õ�ʱ���\n" + startHour + ":0" + startMin
									+ " - " + endHour + ":0" + endMin);
						}else if(startMin < 10){
							aa.title("�õ�ʱ���\n" + startHour + ":0" + startMin
									+ " - " + endHour + ":" + endMin);
						}else if(endMin < 10){
							aa.title("�õ�ʱ���\n" + startHour + ":" + startMin
									+ " - " + endHour + ":0" + endMin);
						}else{
							aa.title("�õ�ʱ���\n" + startHour + ":" + startMin
									+ " - " + endHour + ":" + endMin);
						}
						
						aa.animateType(MarkerAnimateType.grow);
						//mBaiduMap.addOverlay(aa); ��ֹ���࣬ע��
						mMarker.add((Marker)mBaiduMap.addOverlay(aa));
					}
					//�켣��Ҫ������۵�
					points.add(new LatLng(lat,lng));
					
					endMin = curMin;
					endHour = curHour;	//��ʱ��� ��¼Ϊ��һʱ��εĽ���ʱ���
				}
				befLat = lat;
				befLng = lng;	//����¼�ý�㾭γ���Ա�����һ���Ƚ�
			}
		}
		c.close();
		db.close();
		//�������
		if(points.size() >= 2){
		    BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
			OverlayOptions trackPolyline = new PolylineOptions().width(15).dottedLine(true).customTexture(mGreenTexture).points(points);
			mBaiduMap.addOverlay(trackPolyline);
		}
		
		//������¼�����
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

        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//����sp
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //Ĭ���������
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        }
        if(weaCode == 3){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        }
        if(weaCode == 4){	//ѩ��
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override
	public void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();
    }  
    @Override
	public void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    }

    public void onClick(View arg0) {
		// TODO �Զ����ɵķ������
		if(arg0.getId() == R.id.imageButton1){
			popMenu.showAsDropDown(arg0);
		}
	}

	@Override
	public void onItemClick(int index) {
		// TODO �Զ����ɵķ������
		//�������㳡
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
			//ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "ʱ�����ͼ");//����ͼƬ������ͼ��
		}
		//����������
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
	
	//����һ������
	private void showShare() {
		 ShareSDK.initSDK(getActivity());
		 OnekeyShare oks = new OnekeyShare();
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 
			 
		 // ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle("DailyLine");
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		 oks.setTitleUrl("http://dky.njnu.edu.cn");
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText("�켣��ͼ�����ܲ���");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 oks.setImagePath("/sdcard/DailyLine/map.png");//ȷ��SDcard������ڴ���ͼƬ
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		 oks.setUrl("http://dky.njnu.edu.cn");
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		 oks.setComment("���ǲ��������ı�");
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		 oks.setSiteUrl("http://dky.njnu.edu.cn");
		 
		 // ��������GUI
		 oks.show(getActivity());
	}
}