package edu.njnu.dailyline;

import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;

import edu.njnu.dailyline.activity.MainTabActivity;
import edu.njnu.dailyline.activity.UserLogin;
import edu.njnu.dailyline.domain.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class SplashScreen extends Activity {
    
	private SharedPreferences sp;
	
	@Override
	//界面样式
	public void onCreate(Bundle icicle){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(icicle);
		
		sp = this.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		
		
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.splashscreen);
		new Handler().postDelayed(new Runnable(){
			public void run(){
				
				boolean isLogin = sp.getBoolean("isLogin", false);
//				System.out.println("isLogin:"+isLogin);
				if(isLogin){
					
					String account =  sp.getString("accout", "");
					String icon = sp.getString("icon","");
					
//					System.out.println("accout:"+account);
//					System.out.println("icon:"+icon);
					
					if(account!="" && icon!=""){
						User u = new User();
						u.setaccount(account);
						u.setIcon(icon);
						
						((DLApplication) getApplicationContext())
						.setUser(u);
						
						Intent mainIntent = new Intent(SplashScreen.this,MainTabActivity.class);
						SplashScreen.this.startActivity(mainIntent);
					}else{
						Intent mainIntent = new Intent(SplashScreen.this,UserLogin.class);
						SplashScreen.this.startActivity(mainIntent);
					}
				}else{
					Intent mainIntent = new Intent(SplashScreen.this,UserLogin.class);
					SplashScreen.this.startActivity(mainIntent);
				}	
				SplashScreen.this.finish();
			}
		},3000);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		JPushInterface.onResume(SplashScreen.this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		JPushInterface.onPause(SplashScreen.this);
	}
}
		
