package edu.njnu.dailyline.activity;

import edu.njnu.dailyline.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class UserInfoActivity extends Activity{

	SharedPreferences wea_info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_detail);

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
		
	}
}
