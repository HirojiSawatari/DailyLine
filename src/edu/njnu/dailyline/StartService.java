package edu.njnu.dailyline;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import edu.njnu.dailyline.activity.MainTabActivity;
import edu.njnu.dailyline.domain.Position;
import edu.njnu.dailyline.utils.StepDetector;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class StartService extends Service {

	SQLiteDatabase db;
	private int lCode;
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;
	private boolean isStop = false;
	private static int delay = 1000; // 1s
	private static int period = 1000; // 1s
	//private static String strUrl = "http://192.168.1.113:9191/sf/";
	private LocationClient locationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private Position position;

	public static Boolean flag = false;
	private SensorManager sensorManager;
	private StepDetector stepDetector;
	private int total_step;
	
	double lat;
	double lng;
	String strLat;
	String strLng;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//开启新线程用于记步
		new Thread(new Runnable() {
			public void run() {
				startStepDetector();
			}
		}).start();
		
		//打开SQLite数据库
		db = openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);
		//若不存在track表新建该表
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)");  
		
		locationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		locationClient.registerLocationListener(myListener); // 注册监听函数
		//设置定位SDK参数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true);        
        option.setCoorType("bd09ll");       
        option.setProdName("LocationDemo");
		locationClient.setLocOption(option);
		
		//获取轨迹结点计数值
		SharedPreferences track_info = getSharedPreferences("track", 0);
		lCode = track_info.getInt("loccode", 0);
		if(lCode < 0){
			lCode = 0;
			SharedPreferences.Editor editor = track_info.edit();
			editor.putInt("loccode", lCode);
			editor.commit();
		}
		
		//通知栏
		CharSequence name="DailyLine";
		CharSequence name2="用户行为记录中";
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
        Builder builder = new Notification.Builder(this);  
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, new Intent(this,MainTabActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);  
        builder.setContentIntent(contentIndent) .setSmallIcon(R.drawable.icon_start_service)//设置状态栏里面的图标（小图标） 　　　　　　　　　　　　　　　　　　　　.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))//下拉下拉列表里面的图标（大图标） 　　　　　　　.setTicker("this is bitch!") //设置状态栏的显示的信息  
               .setWhen(System.currentTimeMillis())//设置时间发生时间  
               .setAutoCancel(false)//设置可以清除  
               .setContentTitle(name)//设置下拉列表里的标题  
               .setContentText(name2);//设置上下文内容  
        Notification notification = builder.getNotification();  
        notificationManager.notify(1,notification);  
        notification.flags = Notification.FLAG_ONGOING_EVENT; 
        //保证服务前台运行
        startForeground(1, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 触发定时器
		if (!isStop) {
			//Log.i("K", "开始服务");
			startTimer();
		}
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		//Log.d("Res", "onDestroy");
		locationClient.stop();
		db.close();
		// 停止定时器
		if (isStop) {
			Log.i("T", "服务停止");
			stopTimer();
		}
		//停止前台服务
		stopForeground(true);
		//广播进程被Kill
		Intent des_intent = new Intent("edu.njnu.dailyline.destroy");  
	    sendBroadcast(des_intent);
		super.onDestroy();
		if (stepDetector != null) {
			sensorManager.unregisterListener(stepDetector);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 定时器 一分钟执行一次
	 */
	private void startTimer() {
		//定时器
		if (mTimer == null) {
			mTimer = new Timer();
		}
		//Log.i(TAG, "count: " + String.valueOf(count++));
		isStop = true;
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					//Log.i(TAG, "count: " + String.valueOf(count++));
					do {
						try {
							locationClient.start();
							if (locationClient != null && locationClient.isStarted()){
								locationClient.requestLocation();
							}
							else{
								Thread.sleep(1000*60);//暂停相应时间								
							}
						} catch (InterruptedException e) {
						}
					} while (isStop);
				}
			};
		}

		if (mTimer != null && mTimerTask != null){
			mTimer.schedule(mTimerTask, delay, period);
		}
	}
	
	private void stopTimer() {

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		isStop = false;

	}

	/*
	 * 获取定位点信息
	 */
	@SuppressLint("SimpleDateFormat")
	private void getLocationInfo(BDLocation location) {
		if (location != null) {
			//初始化定位点要素
			position = new Position();
			
			lat = location.getLatitude();
			lng = location.getLongitude();
			strLat = String.format("%.3f", lat);
			strLng = String.format("%.3f", lng);	//保留三位小数（保留三位到四位之间为宜）			
			position.setLat(Double.valueOf(strLat).doubleValue());
			position.setLon(Double.valueOf(strLng).doubleValue());
			
			//获取系统时间
			Time time = new Time("Asia/Hong_Kong");
			time.setToNow();
			
			position.setYear(time.year);
			position.setMonth((time.month) + 1);
			position.setDay(time.monthDay);
			position.setHour(time.hour);
			position.setMinute(time.minute);
			position.setSecond(time.second);
			
			//获取前台APP包名
			position.setApppack(getTopApp());
			
			//获取当前步数
			total_step = StepDetector.CURRENT_SETP;	//关闭服务归零，需重写
			position.setStep(total_step);		
			
			PostData(position);
    		
		} else {
			
		}
	}

	/*
	 * 向后台Post定位数据
	 */
	public void PostData(Position p) {	//参数全部为公用值
		//保存至本地SQLite数据库
		db.execSQL("INSERT INTO track VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
				new Object[]{p.getLat(), p.getLon(), p.getYear(), p.getMonth(), p.getDay(), 
				p.getHour(), p.getMinute(), p.getSecond(), p.getApppack(), p.getStep()});
	}

	//获取定位数据
	public class MyLocationListener implements BDLocationListener {
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;	
			}
			locationClient.stop();
			getLocationInfo(location);
		}
		public void onReceivePoi(BDLocation poiLocation) {
			
		}
	}
	
	//步数测量
	private void startStepDetector() {
		flag = true;
		stepDetector = new StepDetector(this);
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);//获取传感器管理器的实例
		Sensor sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获得传感器的类型，这里获得的类型是加速度传感器
		//此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		sensorManager.registerListener(stepDetector, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	//查询当前前台APP
	private String getTopApp() {
        String topActivity = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                }
            }
        }
		return topActivity;
        
    }
}


