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
		
		//�������߳����ڼǲ�
		new Thread(new Runnable() {
			public void run() {
				startStepDetector();
			}
		}).start();
		
		//��SQLite���ݿ�
		db = openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);
		//��������track���½��ñ�
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)");  
		
		locationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		locationClient.registerLocationListener(myListener); // ע���������
		//���ö�λSDK����
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true);        
        option.setCoorType("bd09ll");       
        option.setProdName("LocationDemo");
		locationClient.setLocOption(option);
		
		//��ȡ�켣������ֵ
		SharedPreferences track_info = getSharedPreferences("track", 0);
		lCode = track_info.getInt("loccode", 0);
		if(lCode < 0){
			lCode = 0;
			SharedPreferences.Editor editor = track_info.edit();
			editor.putInt("loccode", lCode);
			editor.commit();
		}
		
		//֪ͨ��
		CharSequence name="DailyLine";
		CharSequence name2="�û���Ϊ��¼��";
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
        Builder builder = new Notification.Builder(this);  
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, new Intent(this,MainTabActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);  
        builder.setContentIntent(contentIndent) .setSmallIcon(R.drawable.icon_start_service)//����״̬�������ͼ�꣨Сͼ�꣩ ����������������������������������������.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))//���������б������ͼ�꣨��ͼ�꣩ ��������������.setTicker("this is bitch!") //����״̬������ʾ����Ϣ  
               .setWhen(System.currentTimeMillis())//����ʱ�䷢��ʱ��  
               .setAutoCancel(false)//���ÿ������  
               .setContentTitle(name)//���������б���ı���  
               .setContentText(name2);//��������������  
        Notification notification = builder.getNotification();  
        notificationManager.notify(1,notification);  
        notification.flags = Notification.FLAG_ONGOING_EVENT; 
        //��֤����ǰ̨����
        startForeground(1, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// ������ʱ��
		if (!isStop) {
			//Log.i("K", "��ʼ����");
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
		// ֹͣ��ʱ��
		if (isStop) {
			Log.i("T", "����ֹͣ");
			stopTimer();
		}
		//ֹͣǰ̨����
		stopForeground(true);
		//�㲥���̱�Kill
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
	 * ��ʱ�� һ����ִ��һ��
	 */
	private void startTimer() {
		//��ʱ��
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
								Thread.sleep(1000*60);//��ͣ��Ӧʱ��								
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
	 * ��ȡ��λ����Ϣ
	 */
	@SuppressLint("SimpleDateFormat")
	private void getLocationInfo(BDLocation location) {
		if (location != null) {
			//��ʼ����λ��Ҫ��
			position = new Position();
			
			lat = location.getLatitude();
			lng = location.getLongitude();
			strLat = String.format("%.3f", lat);
			strLng = String.format("%.3f", lng);	//������λС����������λ����λ֮��Ϊ�ˣ�			
			position.setLat(Double.valueOf(strLat).doubleValue());
			position.setLon(Double.valueOf(strLng).doubleValue());
			
			//��ȡϵͳʱ��
			Time time = new Time("Asia/Hong_Kong");
			time.setToNow();
			
			position.setYear(time.year);
			position.setMonth((time.month) + 1);
			position.setDay(time.monthDay);
			position.setHour(time.hour);
			position.setMinute(time.minute);
			position.setSecond(time.second);
			
			//��ȡǰ̨APP����
			position.setApppack(getTopApp());
			
			//��ȡ��ǰ����
			total_step = StepDetector.CURRENT_SETP;	//�رշ�����㣬����д
			position.setStep(total_step);		
			
			PostData(position);
    		
		} else {
			
		}
	}

	/*
	 * ���̨Post��λ����
	 */
	public void PostData(Position p) {	//����ȫ��Ϊ����ֵ
		//����������SQLite���ݿ�
		db.execSQL("INSERT INTO track VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
				new Object[]{p.getLat(), p.getLon(), p.getYear(), p.getMonth(), p.getDay(), 
				p.getHour(), p.getMinute(), p.getSecond(), p.getApppack(), p.getStep()});
	}

	//��ȡ��λ����
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
	
	//��������
	private void startStepDetector() {
		flag = true;
		stepDetector = new StepDetector(this);
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);//��ȡ��������������ʵ��
		Sensor sensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//��ô����������ͣ������õ������Ǽ��ٶȴ�����
		//�˷�������ע�ᣬֻ��ע����Ż���Ч��������SensorEventListener��ʵ����Sensor��ʵ������������
		sensorManager.registerListener(stepDetector, sensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	//��ѯ��ǰǰ̨APP
	private String getTopApp() {
        String topActivity = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //��ȡ60��֮�ڵ�Ӧ������
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);

                //ȡ��������е�һ��app������ǰ���е�app
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


