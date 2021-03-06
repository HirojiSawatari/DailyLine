package edu.njnu.dailyline;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cn.jpush.android.api.JPushInterface;
import edu.njnu.dailyline.db.dao.UserDao;
import edu.njnu.dailyline.domain.User;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

/**
 * 全局Application类
 * 
 * @author 王进
 * 
 * 2016年7月24日15:01:49
 *
 * 
 */
public class DLApplication extends Application{

	private List<Activity> activitys = new LinkedList<Activity>();//保存所有的Activity
	private List<Service> services = new LinkedList<Service>();//保存所有的service
	private User account;//当前用户
	private int pid;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//极光推送
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        
        
		pid = Process.myPid();
		
		Log.d("DLApplication", "init");
		
	}

	public void addActivity(Activity activity) {
		activitys.add(activity);
	}

	public void removeActivity(Activity activity) {
		activitys.remove(activity);
	}

	public void addService(Service service) {
		services.add(service);
	}

	public void removeService(Service service) {
		services.remove(service);
	}

	public void closeApplication() {
		closeActivitys();
		closeServices();
//		Process.killProcess(pid);
	}

	private void closeActivitys() {
		ListIterator<Activity> iterator = activitys.listIterator();
		while (iterator.hasNext()) {
			Activity activity = iterator.next();
			if (activity != null) {
				activity.finish();
			}
		}
	}

	private void closeServices() {
		ListIterator<Service> iterator = services.listIterator();
		while (iterator.hasNext()) {
			Service service = iterator.next();
			if (service != null) {
				stopService(new Intent(this, service.getClass()));
			}
		}
	}

	//得到当前用户
	public User getCurrentUser() {
		if (account == null) {
			UserDao dao = new UserDao(this);
			account = dao.getCurrentUser();
		}
		return account;
	}
	
	public void setUser(User account){
		this.account = account;
	}
}
