package edu.njnu.dailyline.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.njnu.dailyline.AddEventLoc;
import edu.njnu.dailyline.AddEventTime;
import edu.njnu.dailyline.AddUserEvent;
import edu.njnu.dailyline.R;
import edu.njnu.dailyline.domain.UserEvent;
import edu.njnu.dailyline.utils.Bimp;
import edu.njnu.dailyline.utils.FileUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PublishedActivity extends Activity {

	SQLiteDatabase db;
	
	private GridView noScrollgridview;
	private GridAdapter adapter;
	
	SharedPreferences wea_info;
	
	TextView timeView;
	TextView locView;
	EditText conText;
	
	private UserEvent event;
	private String uri = null;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 0;
	private int min = 0;
	private double lat = 0.0;
	private double lng = 0.0;
	private String content = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectimg);
		Init();
		
		ImageButton button1 = (ImageButton) findViewById(R.id.locButton);
		ImageButton button2 = (ImageButton) findViewById(R.id.timeButton);
		timeView = (TextView) findViewById(R.id.timeText);
		locView = (TextView) findViewById(R.id.locText);
		
		wea_info = getSharedPreferences("wea_info", 0); // 开启sp
		TextView headText = (TextView) findViewById(
				R.id.TextView1);
		// 默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
		if (weaCode == 1) { // 晴天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_orange));
		}
		if (weaCode == 3) { // 雨天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_gray));
		}
		if (weaCode == 4) { // 雪天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_blue));
		}
		
		button1.setOnClickListener(new OnClickListener(){	//设置位置
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				lat = 0.0;
				lng = 0.0;
				locView.setText("");
				Intent intent = new Intent(PublishedActivity.this,AddEventLoc.class);
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
				Intent intent = new Intent(PublishedActivity.this,AddEventTime.class);
				startActivityForResult(intent, 1);
			}		
		});
		
	}

	public void Init() {
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublishedActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublishedActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});

		conText = (EditText) findViewById(R.id.setContent);
		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				PublishedActivity.this.finish();
			}	
		});
		
		imageButton1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				uri = Bimp.drr.get(0);
				content = conText.getText().toString();
				if(uri != null && year > 0 && lat > 0 && conText.length() > 0){
					event = new UserEvent();
					
					event.setUri(uri);
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
							hour, min, uri, content});

					db.close();
					Toast.makeText(getApplicationContext(), "事件保存完毕", Toast.LENGTH_SHORT).show();
					PublishedActivity.this.finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "事件信息不全，请完善信息", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位�?
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 2) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(PublishedActivity.this,
							PicActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/", String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, 2);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
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
		case 2:
			if (Bimp.drr.size() < 1 && resultCode == -1) {
				Bimp.drr.add(path);
			}
			break;
		}
	}

}
