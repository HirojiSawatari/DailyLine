package edu.njnu.dailyline.activity;

import edu.njnu.dailyline.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_test);
		
		
		String url = getIntent().getStringExtra("url");
		Toast.makeText(getApplicationContext(), url, 1).show();
	}
}
