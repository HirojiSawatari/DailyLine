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

	// private IWXAPI api;//������app��΢��ͨ�ŵ�OpenAPI�ӿڡ�

	// ˫�����ؼ��˳�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
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
	// ������ʽ
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);

		sp = this.getSharedPreferences("config.xml", Context.MODE_PRIVATE);

		// wj:2016��8��10��19:24:34
		// ע�ᵽ΢��
		// regToWx();

		// ΢�ŵ�¼��ťbutton1
		Button button1 = (Button) findViewById(R.id.button_wx);

		// ȷ�ϵ�¼��ťbutton2
		Button button2 = (Button) findViewById(R.id.button_ok);

		// �û��������
		final EditText editText1 = (EditText) findViewById(R.id.name_edit);

		// ���������
		final EditText editText2 = (EditText) findViewById(R.id.pass_edit);

		button1.getBackground().setAlpha(60);
		button2.getBackground().setAlpha(60);
		// ΢�ŵ�¼��������
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// //΢�ŵ�½
				// if(!api.isWXAppInstalled()){
				// Toast.makeText(UserLogin.this,"�밲װ΢�ſͻ���֮���ٽ��е�¼",1).show();
				// return;
				// }
				//
				// getCode();

			}
		});
		// ȷ�ϵ�¼��������
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				userName = editText1.getText().toString().trim(); // �û���
				userPass = editText2.getText().toString().trim(); // ����
				
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

							// TODO:�洢��sp
							Editor edit = sp.edit();
							edit.putString("accout", data.getAccount());
							edit.putString("icon", data.getIcon());
							edit.putBoolean("isLogin", true);
							edit.commit();

							System.out.println("save in sp");
							
							// ����Ϊȫ�ֵ�user�����ĸ�ҳ�涼���Է��ʡ�
							((DLApplication) getApplicationContext())
									.setUser(u);

							// ��ת��������
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

				// ��½
				// DLRequestAsynManager.getInstance().login(userName,
				// userPass,new DLObjectCallback<User>() {
				//
				// @Override
				// public void onSuccess(User user) {
				// // Log.i("UserLogin", user.getName());
				//
				// System.out.println(user.getaccount());
				// //TODO���洢�û���Ϣ��sqlite
				//
				// //��ʱֱ����ת������
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

	/** -------------------------΢�ŵ�������¼---------------------- */
	/**
	 * ע�ᵽ΢��
	 **/
	// private void regToWx(){
	// // ͨ��WXAPIFactory����,���IWXAPI��ʵ��
	// api = WXAPIFactory.createWXAPI(UserLogin.this, APP_ID, true);
	// // ��Ӧ�õ�appidע�ᵽ΢��
	// api.registerApp(APP_ID);
	// }
	// //��ȡ΢�ŷ���getCode
	// private void getCode(){
	// final SendAuth.Req req = new SendAuth.Req();
	// req.scope = "snsapi_userinfo";
	// req.state = "wechat_sdk_demo_test";
	// api.sendReq(req);
	// }

}