package edu.njnu.dailyline.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.njnu.dailyline.AddUserEvent;
import edu.njnu.dailyline.R;
import edu.njnu.dailyline.adapter.ImageGridAdapter;
import edu.njnu.dailyline.adapter.ImageGridAdapter.TextCallback;
import edu.njnu.dailyline.domain.ImageItem;
import edu.njnu.dailyline.utils.AlbumHelper;
import edu.njnu.dailyline.utils.Bimp;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	// ArrayList<Entity> dataList;//鐢ㄦ潵瑁呰浇鏁版嵁婧愮殑鍒楄〃
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	ImageButton bt;
	SharedPreferences wea_info;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this, "最多选择1张图片", 400).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_grid);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

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
		
		initView();
		
		//选择完毕之后
		bt = (ImageButton) findViewById(R.id.imageButton1);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}

				if (Bimp.act_bool) {
					Intent intent = new Intent(ImageGridActivity.this,PublishedActivity.class);
					startActivity(intent);
					ImageGridActivity.this.finish();
					
//					Bimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 1) {
						Bimp.drr.add(list.get(i));
					}
				}
				finish();
			}

		});

		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				ImageGridActivity.this.finish();
			}	
		});
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}
		});
	}
}
