package edu.njnu.dailyline;

import edu.njnu.dailyline.activity.PicActivity;
import edu.njnu.dailyline.domain.UserEvent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class AddUserEvent extends Activity {

	SQLiteDatabase db;
	
	LinearLayout linearLayout1;
	TextView timeView;
	TextView locView;
	EditText nameText;
	EditText conText;
	
	private UserEvent event;
	private Uri uri = null;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 0;
	private int min = 0;
	private double lat = 0.0;
	private double lng = 0.0;
	private String name = "";
	private String content = "";
	
	/**
	 * @param args
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_event);
		
		//初始化图片容器布局
		nameText = (EditText) findViewById(R.id.editText1);
		conText = (EditText) findViewById(R.id.editText2);
		linearLayout1 = (LinearLayout)findViewById(R.id.pic_layout);
		timeView = (TextView) findViewById(R.id.temperaText);
		locView = (TextView) findViewById(R.id.textView5);
		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);
		
		imageButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				
				content = conText.getText().toString();
				if(uri != null && year > 0 && lat > 0 && nameText.length() > 0 && conText.length() > 0){
					
					String uriStr = uri.toString();
					event = new UserEvent();
					
					//event.setUri(uri);
					event.setLat(lat);
					event.setLng(lng);
					event.setYear(year);
					event.setMonth(month);
					event.setDay(day);
					event.setHour(hour);
					event.setMin(min);
					event.setContent(content);
					
					//打开SQLite数据库
					db = openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);
					//若不存在event表新建该表
					db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
			                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
			                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
			                "uri VARCHAR, content VARCHAR)");  
					//保存至本地SQLite数据库
					db.execSQL("INSERT INTO event VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
							new Object[]{lat, lng, year, month, day, 
							hour, min, uriStr, content});

					db.close();
					Toast.makeText(getApplicationContext(), "事件保存完毕", Toast.LENGTH_SHORT).show();
					AddUserEvent.this.finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "事件信息不全，请完善信息", Toast.LENGTH_SHORT).show();
				}
			}	
		});
		
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
        		AddUserEvent.this.finish();
			}	
		});
		
		button1.setOnClickListener(new OnClickListener(){	//设置位置
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				lat = 0.0;
				lng = 0.0;
				locView.setText("");
				Intent intent = new Intent(AddUserEvent.this,AddEventLoc.class);
				startActivityForResult(intent, 0);
			}		
		});
		
		button2.setOnClickListener(new OnClickListener(){	//设置时间
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				year = 0;
				month = 0;
				day = 0;
				hour = 0;
				min = 0;
				timeView.setText("");
				Intent intent = new Intent(AddUserEvent.this,AddEventTime.class);
				startActivityForResult(intent, 1);
			}		
		});
		
		//选择图片
		button3.setOnClickListener(new OnClickListener(){	//添加图片
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
//				uri = null;
//				linearLayout1.removeAllViews();
//				selectImageFromGallery();
				
				//列出系统所有照片几何，以相册为单位，每个相册中是照片集合
				
				Intent intent= new Intent(AddUserEvent.this,PicActivity.class);
				startActivity(intent);
			}		
		});
		
	}

	// 直接从相册选择图片
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {			// 返回的数据
		switch (requestCode){
		case 0:
			if (data != null) {
				lat = data.getDoubleExtra("lat", 0);
				lng = data.getDoubleExtra("lng", 0);
				locView.setText("位置坐标已设置");
			}
			break;
		case 1:	//设时间返回
			if (data != null) {
				year = data.getIntExtra("year", 0);
				month = data.getIntExtra("month", 0);
				day = data.getIntExtra("day", 0);
				hour = data.getIntExtra("hour", 0);
				min = data.getIntExtra("min", 0);
				if(min < 10){
					timeView.setText(year + "年" + month + "月" + day + "日  " + hour + ":0" + min);				
				}
				else{
					timeView.setText(year + "年" + month + "月" + day + "日  " + hour + ":" + min);
				}
			}
			break;
		case 2:	//读图返回
    		if (data != null) {
				// 得到图片的全路径
				uri = data.getData();
				System.out.println(uri);
				ImageView imageView1 = new ImageView(this);
				imageView1.setLayoutParams(new LayoutParams(100,100));
				imageView1.setImageURI(uri);
    			linearLayout1.addView(imageView1);
			}
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}
}
