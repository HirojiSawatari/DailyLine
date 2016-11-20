package edu.njnu.dailyline;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class AddEventLoc extends Activity {
	
	MapView mMapView = null;
	LocationClient mLocClient;
    BaiduMap mBaiduMap;
	SharedPreferences wea_info;
    
    double pointLat = 0.0;
    double pointLng = 0.0;
    
    public MyLocationListenner myListener = new MyLocationListenner();

    /**
	 * @param args
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());	//�ٶȵ�ͼsdk��ʼ��
		setContentView(R.layout.add_location);
		
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
		
		ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		
		imageButton1.setOnClickListener(new OnClickListener() {	//ȷ��
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				//�ش�ֵ
				if(pointLat != 0){
					Intent rIntent = new Intent();
					rIntent.putExtra("lat", pointLat);
					rIntent.putExtra("lng", pointLng);
					AddEventLoc.this.setResult(0, rIntent);
				}
        		AddEventLoc.this.finish();
			}	
		});
		
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				AddEventLoc.this.finish();
			}	
		});
		
		mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // ������λͼ��
        mBaiduMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        mLocClient.setLocOption(option);
        mLocClient.start();

        //����¼�����
        mBaiduMap.setOnMapClickListener(new OnMapClickListener(){
			@Override
			public void onMapClick(LatLng point) {
				// TODO �Զ����ɵķ������
				pointLat = point.latitude;
				pointLng = point.longitude;
				//���ͼ��
				mBaiduMap.clear();
				//��ʾ��
				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka);
				OverlayOptions option = new MarkerOptions()
					.position(point)
					.icon(bitmap);
				mBaiduMap.addOverlay(option);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO �Զ����ɵķ������
				return false;
			}
        	
        });
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
