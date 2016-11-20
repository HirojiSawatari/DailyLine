package edu.njnu.dailyline.activity;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.njnu.dailyline.DLApplication;
import edu.njnu.dailyline.R;
import edu.njnu.dailyline.domain.LoginResult;
import edu.njnu.dailyline.domain.LoginResult.Data;
import edu.njnu.dailyline.domain.User;
import edu.njnu.dailyline.services.LoginService;

public class UserLogin extends Activity {

	private long exitTime = 0;
	private String userName = null;
	private String userPass = null;
	private SharedPreferences sp;

	private static final String APP_ID = "wx49ac4e79a9e0919a";

	// private IWXAPI api;//第三方app和微信通信的OpenAPI接口。

	// 双击返回键退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	// 界面样式
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);

		sp = this.getSharedPreferences("config.xml", Context.MODE_PRIVATE);

		// wj:2016年8月10日19:24:34
		// 注册到微信
		// regToWx();

		// 微信登录按钮button1
		Button button1 = (Button) findViewById(R.id.button_wx);

		// 确认登录按钮button2
		Button button2 = (Button) findViewById(R.id.button_ok);

		// 用户名输入框
		final EditText editText1 = (EditText) findViewById(R.id.name_edit);

		// 密码输入框
		final EditText editText2 = (EditText) findViewById(R.id.pass_edit);

		button1.getBackground().setAlpha(60);
		button2.getBackground().setAlpha(60);
		// 微信登录按键监听
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// //微信登陆
				// if(!api.isWXAppInstalled()){
				// Toast.makeText(UserLogin.this,"请安装微信客户端之后再进行登录",1).show();
				// return;
				// }
				//
				// getCode();

			}
		});
		// 确认登录按键监听
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				userName = editText1.getText().toString().trim(); // 用户名
				userPass = editText2.getText().toString().trim(); // 密码
				
				/*
				new Thread() {
					public void run() {
						LoginService loginService = new LoginService(
								UserLogin.this);
						try {
							LoginResult res = loginService.login(userName,
									userPass);
							User u = new User();
							Data data = res.getData();
							u.setaccount(data.getAccount());
							// u.setSex(res.getData().getSex());
							u.setIcon(data.getIcon());

							// TODO:存储到sp
							Editor edit = sp.edit();
							edit.putString("accout", data.getAccount());
							edit.putString("icon", data.getIcon());
							edit.putBoolean("isLogin", true);
							edit.commit();

							System.out.println("save in sp");
							
							// 设置为全局的user，在哪个页面都可以访问。
							((DLApplication) getApplicationContext())
									.setUser(u);

							// 跳转到主界面
							Intent mainIntent = new Intent(UserLogin.this,
									MainTabActivity.class);
							UserLogin.this.startActivity(mainIntent);
							UserLogin.this.finish();

						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();
				
				*/
				
				// Intent mainIntent = new
				// Intent(UserLogin.this,MainTabActivity.class);
				// UserLogin.this.startActivity(mainIntent);
				// UserLogin.this.finish();

				// 登陆
				// DLRequestAsynManager.getInstance().login(userName,
				// userPass,new DLObjectCallback<User>() {
				//
				// @Override
				// public void onSuccess(User user) {
				// // Log.i("UserLogin", user.getName());
				//
				// System.out.println(user.getaccount());
				// //TODO：存储用户信息到sqlite
				//
				// //暂时直接跳转主界面
				 Intent mainIntent = new
				 Intent(UserLogin.this,MainTabActivity.class);
				 UserLogin.this.startActivity(mainIntent);
				 UserLogin.this.finish();
				// }
				//
				// @Override
				// public void onError(int error, String msg) {
				//
				// }
				// });

			}
		});
	}

	/** -------------------------微信第三方登录---------------------- */
	/**
	 * 注册到微信
	 **/
	// private void regToWx(){
	// // 通过WXAPIFactory工厂,获得IWXAPI的实例
	// api = WXAPIFactory.createWXAPI(UserLogin.this, APP_ID, true);
	// // 将应用的appid注册到微信
	// api.registerApp(APP_ID);
	// }
	// //获取微信访问getCode
	// private void getCode(){
	// final SendAuth.Req req = new SendAuth.Req();
	// req.scope = "snsapi_userinfo";
	// req.state = "wechat_sdk_demo_test";
	// api.sendReq(req);
	// }

}