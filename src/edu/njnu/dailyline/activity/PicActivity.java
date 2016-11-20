package edu.njnu.dailyline.activity;

import java.io.Serializable;
import java.util.List;

import edu.njnu.dailyline.R;
import edu.njnu.dailyline.adapter.ImageBucketAdapter;
import edu.njnu.dailyline.domain.ImageBucket;
import edu.njnu.dailyline.utils.AlbumHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PicActivity extends Activity {
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;
	AlbumHelper helper;
	SharedPreferences wea_info;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

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
		
		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				PicActivity.this.finish();
			}	
		});
		
		initData();
		initView();
	}

	private void initData() {
		dataList = helper.getImagesBucketList(false);	
		bimap=BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_focused);
	}
	
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(PicActivity.this, dataList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent(PicActivity.this,
						ImageGridActivity.class);
				intent.putExtra(PicActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				finish();
			}

		});
	}
}
