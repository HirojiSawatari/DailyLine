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
	//��ʼ����ͼ
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_1,container, false);
		return view;		
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//����sp
        //���״̬����
		addButton = (ImageButton)getActivity().findViewById(R.id.imageButton2);
        addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				Intent intent = new Intent(getActivity(),PicActivity.class);
				getActivity().startActivity(intent);
			}
        });
        
        //��λ��ʼ��
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
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
		
		//ʱ�����ʼ��
		listView = (ListView) getActivity().findViewById(R.id.listview);
		listView.setDividerHeight(0);
		timelineAdapter = new TimelineAdapter(getActivity(), getData());
		listView.setAdapter(timelineAdapter);
		listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back));

		//ʱ�������¼�
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO �Զ����ɵķ������
				MoreInfoDialog.Builder builder = new MoreInfoDialog.Builder(getActivity());
				Time time = new Time("Asia/Hong_Kong");
				time.setToNow();
				builder.setTime(time.year + "��" + (time.month + 1) + "��" + time.monthDay + "��  " + (time.hour - arg2) + "ʱ");
				builder.create(getActivity(), arg2).show();
			}
		});
		
		ImageView firstIcon = (ImageView) getActivity().findViewById(R.id.firstIcon);
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //Ĭ���������
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_sunny));
        	firstIcon.setImageResource(R.drawable.head_sunny);	
        }
        if(weaCode == 3){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_rain));
        	firstIcon.setImageResource(R.drawable.head_rain);	
        }
        if(weaCode == 4){	//ѩ��
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
        	listView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_back_snow));
        	firstIcon.setImageResource(R.drawable.head_snow);	
        }
        
        temperaText = (TextView) getActivity().findViewById(R.id.temperaText);
        cityName = (TextView) getActivity().findViewById(R.id.cityName);
        String curCity = wea_info.getString("city_name", "�Ͼ�");
        String curTemp = wea_info.getString("tempera", "28��/33��");
        temperaText.setText(curTemp);
        cityName.setText(curCity);
	}
	
	//��ȡʱ��������
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//��ȡ��ǰ������
		Time time = new Time("Asia/Hong_Kong");
		time.setToNow();
		
		String packName = null;
		int curYear = time.year;
		int curMonth = (time.month) + 1;
		int curDay = time.monthDay;
		
		String[] useApp = new String[60];
		int[] useTime = new int[60];
		
		//����SQLite������е�
		db = getActivity().openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
		//��������track���½��ñ�
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)"); 
		Cursor c = db.query("track", null, null, null, null, null, null);  //����α�
		
		int rbefHour;
		int rcurHour;
		
		if(c.moveToLast()){//�α��ƶ������һλͬʱ�ж��α��Ƿ�Ϊ��
			int befListHour = -1; 	//��¼ListView��һ��������Сʱ��Ϣ
			rbefHour = c.getInt(c.getColumnIndex("hour"));	//��һ��ʱ��ֵ
			while(!c.isBeforeFirst()){	//��֤�α�ָ�������		
				if(c.getInt(c.getColumnIndex("day")) != curDay){	//�����л�������
					break;
				}
				packName = c.getString(c.getColumnIndex("appname"));
				rcurHour = c.getInt(c.getColumnIndex("hour"));
				if(rcurHour != rbefHour){	//��һСʱ�����ѱ�����ϣ����
					if(befListHour - rbefHour > 1){	//�м�������һ��Сʱδ��APP��Ϣ��¼
						for(int i = befListHour - 1; i > rbefHour; i--){
							Map<String, Object> map = new HashMap<String, Object>();
							map = printNoIcon(i);	//��������Ӧ�ó�����Ϣ��list
							list.add(map);
						}
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map = printIcon(useApp, useTime, rbefHour);
					befListHour = rbefHour;
					//��������event���½��ñ�
					db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
			                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
			                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
			                "uri VARCHAR, content VARCHAR)"); 
					Cursor c1 = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", rbefHour + ""});
					if(c1.moveToFirst()){	//���ƶ�����һ�У����α겻Ϊ�գ����ҵ�һ�����Ϸ�����������						
						map.put("isShowEvent", "1");	//�����¼���ǩ
						map.put("eventName", "�������¼�");
					}
					list.add(map);
					useApp = new String[60];
					useTime = new int[60];
				}
				
				//����������Ӹý���¼
				if(!packName.equals("")){
					if(!packName.contains("launcher")){	//�ų�ϵͳ����������
						int i = 0;
						while(useTime[i] > 0){
							if(useApp[i].equals(packName)){	//�ҵ���ͬ������¼
								useTime[i] ++;
								break;
							}
							i++;
						}
						if(useTime[i] == 0){	//δ������ͬ������¼
							useApp[i] = packName;
							useTime[i] = 1;
						}
					}
				}
				rbefHour = rcurHour;	//���¸�ֵ
				c.moveToPrevious();
			}
			//������һ���ṹ��
			if(befListHour - rbefHour > 1){	//�м�������һ��Сʱδ��APP��Ϣ��¼
				for(int i = befListHour - 1; i > rbefHour; i--){	//�����������APP�¼���list
					Map<String, Object> map = new HashMap<String, Object>();
					map = printNoIcon(i);	//��������Ӧ�ó�����Ϣ��list
					list.add(map);
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map = printIcon(useApp, useTime, rbefHour);
			//��ʼ����event����������event���½��ñ�
			db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
	                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
	                "uri VARCHAR, content VARCHAR)"); 
			Cursor c1 = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", rbefHour + ""});
			if(c1.moveToFirst()){	//���ƶ�����һ�У����α겻Ϊ�գ����ҵ�һ�����Ϸ�����������							
				map.put("isShowEvent", "1");	//�����¼���ǩ	
				map.put("eventName", "�������¼�");
			}
			list.add(map);
			//���һ���м�¼���������ж��Ƿ���ڸ�ʱ��֮ǰδ��APP��¼�ĵ�
			if(rbefHour > 0){	//δ��0�㣬����֮ǰ��Сʱ��¼
				for(int i = rbefHour - 1; i >= 0; i--){
					map = new HashMap<String, Object>();
					map = printNoIcon(i);	//��������Ӧ�ó�����Ϣ��list
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
			ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "DailyLine");//����ͼƬ������ͼ��
		}
		//����������
		else if(index == 1){
			ScreenShot.saveImageToGallery(getActivity(), ScreenShot.createBitmap(listView, getActivity()), "DailyLine");//����ͼƬ������ͼ��
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
		 oks.setText("ʱ��������ܲ���");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 oks.setImagePath("/sdcard/DailyLine/list.jpg");
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
	
	/**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            city = location.getCity();
            city = city.substring(0, city.length() - 1);
            
            //��sp���������
            SharedPreferences.Editor editor = wea_info.edit();
			editor.putString("city_name",city); 
			editor.commit();
			
			cityName.setText(city);
         	//wj:2016��8��10��15:50:33 
            //��ѯ�������
            new Thread(){
            	public void run() {
            		try {
            			List<String>  result = QueryService.getWeather(city);
    						
    					for(int i = 0 ;i<result.size();i++){
    					System.out.println(result.get(i));
    				}
    						
            			weather = result.get(7).substring(result.get(7).length()-2);
            			temperature = result.get(8);
            			
            			if(!temperature.equals("")){	//��Ϊ��
            				temperaText.setText(temperature);
            			}
            			//System.out.println(weather+","+temperature);
    						
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            		if(!temperature.equals("")){	//��Ϊ��
 
            			if(weather.indexOf("ѩ") != -1){
            				//�����������¶�
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 4);
            				editor.putString("tempera", temperature);
            				editor.commit();
            			}
            			else if(weather.indexOf("��") != -1){
            				//�����������¶�
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 3);
            				editor.putString("tempera", temperature);
            				editor.commit();
            			}
            			else if(weather.indexOf("��") != -1){
            				//�����������¶�
            				SharedPreferences.Editor editor = wea_info.edit();
            				editor.putInt("wea_code", 1);
            				editor.putString("tempera", temperature);
        					editor.commit();
            			}
            			else{
        					//�����������¶�
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
	
	//ͼ����ʾ
	private Map<String, Object> printIcon(String [] useApp, int [] useTime, int rbefHour){
		//�Ӵ�Сð������
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
		//ͼ����ʾ��ʱ���
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
		map.put("noApp", "��ʱ���δʹ���ֻ�");
		return map;
	}
}